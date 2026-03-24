package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class NightLight extends Item {
    private static final String LAST_USE_TIME_KEY = "last_use_time";
    private static final int COOLDOWN_TICKS = 1200;
    private static final double RADIUS = 10.0;
    private static final float DAMAGE_AMOUNT = 4.0f;
    private static final int FIRE_DURATION = 100;

    public NightLight(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.BROKEN_WINGS)) return ActionResult.FAIL;
            if (canUse(stack, world)) {
                activateNightLight(player, stack);

                setLastUseTime(stack, world.getTime());

                player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_TICKS);

                player.sendMessage(Text.literal("§5Ночной свет активирован!"), true);

                return ActionResult.SUCCESS;
            } else {
                long remainingTime = getRemainingCooldown(stack, world);
                long remainingSeconds = remainingTime / 20;

                player.sendMessage(Text.literal("§cПредмет на перезарядке! Осталось: " + remainingSeconds + " сек."), true);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }

    private void activateNightLight(ServerPlayerEntity player, ItemStack stack) {
        ServerWorld world = player.getEntityWorld();

        spawnParticles(world, player);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS,
                1.0f, 1.0f);

        Box box = player.getBoundingBox().expand(RADIUS);
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box,
                entity -> entity != player && entity.isAlive());

        for (LivingEntity target : entities) {
            target.damage(world ,player.getDamageSources().magic(), DAMAGE_AMOUNT);

            target.setOnFireFor(FIRE_DURATION / 20);

            world.spawnParticles(ParticleTypes.FLAME,
                    target.getX(), target.getY() + 1, target.getZ(),
                    10, 0.5, 0.5, 0.5, 0.1);
        }

        player.sendMessage(Text.literal("§aПоражено врагов: §e" + entities.size()), true);
    }

    private void spawnParticles(ServerWorld world, PlayerEntity player) {
        world.spawnParticles(ParticleTypes.EXPLOSION,
                player.getX(), player.getY() + 0.5, player.getZ(),
                1, 0, 0, 0, 0);

        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double x = player.getX() + RADIUS * Math.cos(angle);
            double z = player.getZ() + RADIUS * Math.sin(angle);

            world.spawnParticles(ParticleTypes.FLAME,
                    x, player.getY() + 0.5, z,
                    2, 0.1, 0.1, 0.1, 0.05);
        }
    }

    private boolean canUse(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) {
            return true;
        }

        long currentTime = world.getTime();
        return (currentTime - lastUseTime) >= COOLDOWN_TICKS;
    }

    private long getRemainingCooldown(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) {
            return 0;
        }

        long currentTime = world.getTime();
        long elapsed = currentTime - lastUseTime;

        return Math.max(0, COOLDOWN_TICKS - elapsed);
    }

    private void setLastUseTime(ItemStack stack, long time) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putLong(LAST_USE_TIME_KEY, time));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private Long getLastUseTime(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(LAST_USE_TIME_KEY)) {
                return nbt.getLong(LAST_USE_TIME_KEY).get();
            }
        }
        return null;
    }
}