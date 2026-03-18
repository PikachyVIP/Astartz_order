package gc.story.events;

import gc.story.Story;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MutationHandler {

    private static final Random RANDOM = new Random();

    // Аттачмент для отслеживания применения эффектов этапа 1
    public static final AttachmentType<Boolean> STAGE_1_EFFECTS_APPLIED = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "stage_1_effects_applied"),
            builder -> builder.persistent(Codec.BOOL).copyOnDeath()
    );

    // UUID для модификатора здоровья (нужен постоянный идентификатор)
    private static final Identifier HEALTH_MODIFIER_ID = Identifier.of("a1b2c3d4-e5f6-7890-1234-567890abcdef");

    // Счетчик тиков для чихания и частиц
    private static final Map<UUID, Integer> sneezeCooldown = new HashMap<>();
    private static final Map<UUID, Integer> particleCooldown = new HashMap<>();

    public static void register() {
        // Проверяем каждый тик
        ServerTickEvents.END_SERVER_TICK.register(MutationHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        // Проверяем каждые 5 тиков для оптимизации
        if (server.getTicks() % 5 != 0) return;

        long currentTime = System.currentTimeMillis();

        // Проверяем всех игроков
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            checkAndApplyStage1Effects(player);
            applyStage1Symptoms(player, server.getOverworld());
        }
    }

    private static void checkAndApplyStage1Effects(ServerPlayerEntity player) {
        Integer currentStage = player.getAttached(InfectionHandler.INFECTED_ATTACHMENT);

        if (currentStage != null && currentStage == 1) {
            applyStage1Effects(player);
            player.setAttached(STAGE_1_EFFECTS_APPLIED, true);
        }

        if (currentStage == null || currentStage != 1) {
            removeStage1Effects(player);
            player.setAttached(STAGE_1_EFFECTS_APPLIED, false);
        }
    }

    private static void applyStage1Effects(ServerPlayerEntity player) {
        // Уменьшаем максимальное здоровье на 1 сердце (2 единицы здоровья)
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        if (maxHealthAttribute != null) {
            // Создаем модификатор, который уменьшает здоровье
            EntityAttributeModifier healthModifier = new EntityAttributeModifier(
                    HEALTH_MODIFIER_ID,
                    -2,
                    EntityAttributeModifier.Operation.ADD_VALUE

            );

            // Удаляем старый модификатор, если он есть
            maxHealthAttribute.removeModifier(HEALTH_MODIFIER_ID);
            // Добавляем новый
            maxHealthAttribute.addPersistentModifier(healthModifier);

            // Восстанавливаем здоровье, если оно превышает новое максимальное
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    private static void removeStage1Effects(ServerPlayerEntity player) {
        // Возвращаем максимальное здоровье
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);

        if (maxHealthAttribute != null) {
            maxHealthAttribute.removeModifier(HEALTH_MODIFIER_ID);
        }

        // Сбрасываем кулдауны
        sneezeCooldown.remove(player.getUuid());
        particleCooldown.remove(player.getUuid());
    }

    private static void applyStage1Symptoms(ServerPlayerEntity player, ServerWorld world) {
        Integer currentStage = player.getAttached(InfectionHandler.INFECTED_ATTACHMENT);

        // Применяем симптомы только если игрок на стадии 1
        if (currentStage == null || currentStage != 1) return;

        UUID playerUuid = player.getUuid();

        // Чихание (раз в 3-7 секунд)
        int sneezeTimer = sneezeCooldown.getOrDefault(playerUuid, 0);
        if (sneezeTimer <= 0) {
            // Воспроизводим звук чихания панды
            world.playSound(
                    null, // null - чтобы слышали все в радиусе
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.ENTITY_PANDA_SNEEZE, // Звук чихания панды
                    SoundCategory.PLAYERS,
                    1.0f, // Громкость
                    1.0f  // Высота тона
            );

            // Новый кулдаун 3-7 секунд (60-140 тиков)
            sneezeCooldown.put(playerUuid, 60 + RANDOM.nextInt(80));
        } else {
            sneezeCooldown.put(playerUuid, sneezeTimer - 1);
        }

        // Частицы (каждые 10-20 тиков)
        int particleTimer = particleCooldown.getOrDefault(playerUuid, 0);
        if (particleTimer <= 0) {
            // Спавним частицы песка душ вокруг игрока
            for (int i = 0; i < 5; i++) {
                double offsetX = (RANDOM.nextDouble() - 0.5) * 1.5;
                double offsetY = RANDOM.nextDouble() * 2.0;
                double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.5;

                world.spawnParticles(
                        ParticleTypes.SOUL, // Частицы песка душ (soul particles)
                        player.getX() + offsetX,
                        player.getY() + offsetY,
                        player.getZ() + offsetZ,
                        1, // Количество частиц
                        0.1, 0.1, 0.1, // Скорость разлета
                        0.01 // Скорость частиц
                );
            }

            particleCooldown.put(playerUuid, 10 + RANDOM.nextInt(10));
        } else {
            particleCooldown.put(playerUuid, particleTimer - 1);
        }
    }
}