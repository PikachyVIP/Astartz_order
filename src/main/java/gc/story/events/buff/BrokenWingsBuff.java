package gc.story.events.buff;

import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BrokenWingsBuff {
    private static final Random RANDOM = new Random();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 2 * 60 * 1000;
    private static final long ABILITY_DURATION = 5 * 1000;
    private static final Map<UUID, Long> activeAbilities = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(BrokenWingsBuff::onServerTick);
    }

        private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.BROKEN_WINGS) && InfectionHandler.getCurrentStage(player) == 3) {
                if(player.getEntityWorld().isDay()) {
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.SPEED,
                            100,
                            1,
                            false,
                            false,
                            false
                    ));
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.JUMP_BOOST,
                            100,
                            0,
                            false,
                            false,
                            false
                    ));
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.HASTE,
                            100,
                            1,
                            false,
                            false,
                            false
                    ));
                }else if(player.getEntityWorld().isNight()){
                    player.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.GLOWING,
                            100,
                            1,
                            false,
                            false,
                            false
                    ));
                }
            }
        }
    }


//    private static void onServerTick(MinecraftServer server) {
//        long currentTime = System.currentTimeMillis();
//
//
//        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
//            UUID playerUuid = player.getUuid();
//
//
//            // Проверяем активные способности
//            if (activeAbilities.containsKey(playerUuid)) {
//                long activationTime = activeAbilities.get(playerUuid);
//
//                if (currentTime - activationTime >= ABILITY_DURATION) {
//                    if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
//                        player.changeGameMode(GameMode.SURVIVAL);
//                        activeAbilities.remove(playerUuid);
//                        for (int i = 0; i < 5; i++) {
//                            double offsetX = (RANDOM.nextDouble() - 0.5) * 1.5;
//                            double offsetY = RANDOM.nextDouble() * 2.0;
//                            double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.5;
//
//                            server.getOverworld().spawnParticles(
//                                    ParticleTypes.SOUL,
//                                    player.getX() + offsetX,
//                                    player.getY() + offsetY,
//                                    player.getZ() + offsetZ,
//                                    20,
//                                    0.1, 0.1, 0.1,
//                                    0.01
//                            );
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public static boolean activateAbility(ServerPlayerEntity player, ServerWorld world) {
//        UUID playerUuid = player.getUuid();
//        long currentTime = System.currentTimeMillis();
//
//        if (!MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.BROKEN_WINGS)) {
//            return false;
//        }
//
//        if (cooldowns.containsKey(playerUuid)) {
//            long lastUse = cooldowns.get(playerUuid);
//            long timeLeft = (lastUse + COOLDOWN_TIME - currentTime) / 1000;
//
//            if (timeLeft > 0) {
//                return false;
//            }
//        }
//
//        for (int i = 0; i < 5; i++) {
//            double offsetX = (RANDOM.nextDouble() - 0.5) * 1.5;
//            double offsetY = RANDOM.nextDouble() * 2.0;
//            double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.5;
//
//            world.spawnParticles(
//                    ParticleTypes.SOUL,
//                    player.getX() + offsetX,
//                    player.getY() + offsetY,
//                    player.getZ() + offsetZ,
//                    20,
//                    0.1, 0.1, 0.1,
//                    0.01
//            );
//        }
//
//        player.changeGameMode(GameMode.SPECTATOR);
//
//        activeAbilities.put(playerUuid, currentTime);
//        cooldowns.put(playerUuid, currentTime);
//
//        return true;
//    }
}