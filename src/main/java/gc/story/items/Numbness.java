package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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

public class Numbness extends Item {
    private static final String LAST_USE_TIME_KEY = "last_use_time";

    private static final int COOLDOWN_TICKS = 9600;
    private static final int EFFECT_DURATION = 6000;
    private static final int REGEN_AMPLIFIER = 1;
    private static final int STRENGTH_AMPLIFIER = 2;

    private static final double AOE_RADIUS = 8.0;
    private static final float AOE_DAMAGE = 6.0f;

    public Numbness(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.CRYSTALLIZATION)) return ActionResult.FAIL;

            if (canUse(stack, world)) {
                activateNumbness(player, stack);

                setLastUseTime(stack, world.getTime());

                player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_TICKS);

                return ActionResult.SUCCESS;
            } else {
                long remainingTime = getRemainingCooldown(stack, world);
                long remainingSeconds = remainingTime / 20;
                long remainingMinutes = remainingSeconds / 60;
                remainingSeconds = remainingSeconds % 60;

                player.sendMessage(Text.literal("§cПредмет на перезарядке! Осталось: " + remainingMinutes + " мин " + remainingSeconds + " сек."), true);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }

    private void activateNumbness(ServerPlayerEntity player, ItemStack stack) {
        ServerWorld world = player.getEntityWorld();

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.STRENGTH,
                EFFECT_DURATION,
                STRENGTH_AMPLIFIER,
                true,
                true,
                true
        ));

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.REGENERATION,
                EFFECT_DURATION,
                REGEN_AMPLIFIER,
                true,
                true,
                true
        ));

        dealAOEDamage(player, world);

        world.spawnParticles(ParticleTypes.EXPLOSION,
                player.getX(), player.getY() + 0.5, player.getZ(),
                5, 1, 1, 1, 0);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private void dealAOEDamage(ServerPlayerEntity player, ServerWorld world) {
        Box box = player.getBoundingBox().expand(AOE_RADIUS);
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box,
                entity -> entity != player && entity.isAlive());

        int count = 0;
        for (LivingEntity target : entities) {
            target.damage(world, player.getDamageSources().explosion(null), AOE_DAMAGE);

            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance > 0) {
                double knockback = 1.5 * (1 - distance / AOE_RADIUS);
                target.addVelocity(dx / distance * knockback, 0.5, dz / distance * knockback);
                target.velocityModified = true;
            }

            count++;
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