package gc.story.events.buff;

import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.entity.damage.DamageSource;

public class FireBuff {

    private static final int FIRE_DURATION = 3; // 3 секунды (в секундах)
    private static final int FIRE_TICKS = FIRE_DURATION * 20; // Переводим в тики (20 тиков = 1 секунда)
    private static final int EFFECT_DURATION = 80;
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FireBuff::onServerTick);
        AttackEntityCallback.EVENT.register(FireBuff::onAttackEntity);
    }

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 40 != 0) return;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.FIRE) && InfectionHandler.getCurrentStage(player) == 3) {
                boolean isInNether = player.getEntityWorld().getRegistryKey() == World.NETHER;
                boolean isDay = player.getEntityWorld().isDay();
                boolean isInOverworld = player.getEntityWorld().getRegistryKey() == World.OVERWORLD;

                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.FIRE_RESISTANCE,
                        EFFECT_DURATION,
                        0,
                        false,
                        false,
                        false
                ));

                if (isInNether) {
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SPEED,
                            EFFECT_DURATION,
                            0,
                            false,
                            false,
                            false
                    ));
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.REGENERATION,
                            EFFECT_DURATION,
                            1,
                            false,
                            false,
                            false
                    ));
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SLOW_FALLING,
                            EFFECT_DURATION,
                            1,
                            false,
                            false,
                            false
                    ));
                } else if (isInOverworld && isDay) {
                    // В обычном мире только днём
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SPEED,
                            EFFECT_DURATION,
                            0,
                            false,
                            false,
                            false
                    ));
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SLOW_FALLING,
                            EFFECT_DURATION,
                            0,
                            false,
                            false,
                            false
                    ));
                }
            }
        }
    }

    private static ActionResult onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (world.isClient()) return ActionResult.PASS;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (MutationStage3BuffHandler.hasBuff(serverPlayer, MutationStage3BuffHandler.Buff.FIRE)) {
                if (entity instanceof LivingEntity livingTarget) {
                    livingTarget.setOnFireFor(FIRE_DURATION);
                }
            }
        }

        return ActionResult.PASS;
    }

}