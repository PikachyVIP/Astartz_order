package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class Devourer extends Item {
    private static final String SATIETY_ACTIVE_KEY = "satiety_active";
    private static final String SATIETY_START_TIME_KEY = "satiety_start_time";

    private static final int GLOW_RADIUS = 100;
    private static final int SATIETY_DURATION = 12000;
    private static final int EFFECT_DURATION = 400;
    private static final int GLOW_COOLDOWN = 600;

    private static final Random RANDOM = new Random();

    private static final RegistryEntry<StatusEffect>[] POSITIVE_EFFECTS = new RegistryEntry[]{
            StatusEffects.SPEED,
            StatusEffects.HASTE,
            StatusEffects.STRENGTH,
            StatusEffects.INSTANT_HEALTH,
            StatusEffects.JUMP_BOOST,
            StatusEffects.REGENERATION,
            StatusEffects.RESISTANCE,
            StatusEffects.FIRE_RESISTANCE,
            StatusEffects.WATER_BREATHING,
            StatusEffects.INVISIBILITY,
            StatusEffects.NIGHT_VISION,
            StatusEffects.ABSORPTION,
            StatusEffects.SATURATION,
            StatusEffects.LUCK,
            StatusEffects.SLOW_FALLING,
            StatusEffects.CONDUIT_POWER,
            StatusEffects.DOLPHINS_GRACE,
            StatusEffects.HERO_OF_THE_VILLAGE
    };

    public Devourer(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.HUNGER_CURSE)) return ActionResult.FAIL;

            glowEntitiesInRadius(player);

            spawnActivationParticles(player);
            player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_WARDEN_SONIC_BOOM, SoundCategory.PLAYERS, 0.5f, 1.0f);


            player.getItemCooldownManager().set(this.getDefaultStack(), GLOW_COOLDOWN);

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (entity instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.HUNGER_CURSE)) return;
            if (isSatietyActive(stack)) {
                Long startTime = getSatietyStartTime(stack);
                if (startTime != null) {
                    long currentTime = world.getTime();
                    long elapsed = currentTime - startTime;

                    if (elapsed >= SATIETY_DURATION) {
                        setSatietyActive(stack, false);}
                }
            }
        }
    }

    public void onKill(ServerPlayerEntity player, ItemStack stack, LivingEntity killed) {
        if (!(stack.getItem() instanceof Devourer)) return;

        World world = player.getEntityWorld();

        setSatietyActive(stack, true);
        setSatietyStartTime(stack, world.getTime());

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SATURATION,
                SATIETY_DURATION,
                0,
                true,
                true,
                true
        ));

        RegistryEntry<StatusEffect> randomEffect = POSITIVE_EFFECTS[RANDOM.nextInt(POSITIVE_EFFECTS.length)];
        player.addStatusEffect(new StatusEffectInstance(
                randomEffect,
                EFFECT_DURATION,
                0,
                true,
                true,
                true
        ));

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);

        spawnKillParticles(player, killed);
    }

    private void glowEntitiesInRadius(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();

        Box box = player.getBoundingBox().expand(GLOW_RADIUS);
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, box,
                entity -> entity != player && entity.isAlive());

        for (LivingEntity entity : entities) {
            entity.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING,
                    200,
                    0,
                    true,
                    false,
                    true
            ));
        }

    }

    private void spawnActivationParticles(ServerPlayerEntity player) {
        ServerWorld world = player.getEntityWorld();

        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            double x = player.getX() + 2 * Math.cos(angle);
            double z = player.getZ() + 2 * Math.sin(angle);

            world.spawnParticles(ParticleTypes.SCULK_SOUL,
                    x, player.getY() + 0.5, z,
                    1, 0, 0.1, 0, 0);
        }

        world.spawnParticles(ParticleTypes.SONIC_BOOM,
                player.getX(), player.getY() + 1, player.getZ(),
                10, 1, 1, 1, 0.1);
    }

    private void spawnKillParticles(ServerPlayerEntity player, LivingEntity killed) {
        ServerWorld world = player.getEntityWorld();

        world.spawnParticles(ParticleTypes.SOUL,
                killed.getX(), killed.getY() + 1, killed.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);

        world.spawnParticles(ParticleTypes.HEART,
                player.getX(), player.getY() + 1, player.getZ(),
                10, 0.5, 0.5, 0.5, 0.1);
    }

    // Методы для работы с компонентами
    private void setSatietyActive(ItemStack stack, boolean active) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putBoolean(SATIETY_ACTIVE_KEY, active));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private boolean isSatietyActive(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(SATIETY_ACTIVE_KEY)) {
                return nbt.getBoolean(SATIETY_ACTIVE_KEY).get();
            }
        }
        return false;
    }

    private void setSatietyStartTime(ItemStack stack, long time) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putLong(SATIETY_START_TIME_KEY, time));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private Long getSatietyStartTime(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(SATIETY_START_TIME_KEY)) {
                return nbt.getLong(SATIETY_START_TIME_KEY).get();
            }
        }
        return null;
    }
}