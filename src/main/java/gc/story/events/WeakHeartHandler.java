package gc.story.events;

import gc.story.events.MutationStage2Handler;
import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WeakHeartHandler {

    private static final Identifier HEALTH_MODIFIER_ID = Identifier.of("b1c2d3e4-f5a6-7890-1234-567890abcdef");
    private static final Map<UUID, Integer> sprintAccumulator = new HashMap<>();
    private static final Map<UUID, Integer> idleTime = new HashMap<>();

    private static final int SPRINT_THRESHOLD = 200;
    private static final int IDLE_RESET_TIME = 100;
    private static final long DEBUFF_DURATION = 400;
    private static final int SLOWNESS_AMPLIFIER = 2;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                int stage = InfectionHandler.getCurrentStage(player);

                // Стадия 2 - дебафф
                if (stage == 2 && MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.WEAK_HEART)) {
                    tickDebuff(player);
                    applyHealthModifier(player, -10.0);
                }
                // Стадия 3 - бафф
                else if (stage == 3 && MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.WEAK_HEART)) {
                    applyRegen(player);
                    applyHealthModifier(player, -6);
                }
                // Ничего - убираем модификатор
                else {
                    removeHealthModifier(player);
                    sprintAccumulator.remove(player.getUuid());
                    idleTime.remove(player.getUuid());
                }
            }
        });
    }

    private static void tickDebuff(ServerPlayerEntity player) {
        UUID playerUuid = player.getUuid();
        boolean isSprinting = player.isSprinting();

        if (isSprinting) {
            int currentAccumulator = sprintAccumulator.getOrDefault(playerUuid, 0);
            sprintAccumulator.put(playerUuid, currentAccumulator + 1);
            idleTime.remove(playerUuid);

            if (currentAccumulator + 1 >= SPRINT_THRESHOLD) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.BLINDNESS,
                        (int) DEBUFF_DURATION,
                        0,
                        false,
                        true,
                        true
                ));
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SLOWNESS,
                        (int) DEBUFF_DURATION,
                        SLOWNESS_AMPLIFIER,
                        false,
                        true,
                        true
                ));
                sprintAccumulator.put(playerUuid, 0);
            }
        } else {
            int currentIdle = idleTime.getOrDefault(playerUuid, 0) + 1;
            if (currentIdle >= IDLE_RESET_TIME) {
                sprintAccumulator.remove(playerUuid);
                idleTime.remove(playerUuid);
            } else {
                idleTime.put(playerUuid, currentIdle);
            }
        }
    }

    private static void applyRegen(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.REGENERATION,
                60,
                0,
                false,
                false,
                true
        ));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOW_FALLING,
                60,
                0,
                false,
                false,
                true
        ));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.RESISTANCE,
                60,
                0,
                false,
                false,
                true
        ));
    }

    private static void applyHealthModifier(ServerPlayerEntity player, double amount) {
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            removeHealthModifier(player);

            EntityAttributeModifier healthModifier = new EntityAttributeModifier(
                    HEALTH_MODIFIER_ID,
                    amount,
                    EntityAttributeModifier.Operation.ADD_VALUE
            );

            maxHealthAttribute.addPersistentModifier(healthModifier);

            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    private static void removeHealthModifier(ServerPlayerEntity player) {
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
        if (maxHealthAttribute != null) {
            maxHealthAttribute.removeModifier(HEALTH_MODIFIER_ID);
        }
    }
}