package gc.story.events.buff;

import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class SunBuff {


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SunBuff::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.SUN_BURN)) {
                applySunBuff(player, server.getOverworld());
            }
        }
    }

    private static void applySunBuff(ServerPlayerEntity player, ServerWorld world) {;
        boolean isDay = world.isDay();

        if (!isDay) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SPEED, 100, 0, false, true, true
            ));
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.JUMP_BOOST, 100, 0, false, true, true
            ));
        }

        if (!world.isSkyVisible(player.getBlockPos()) || !isDay){
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.REGENERATION, 100, 0, false, true, true
            ));
        }
    }
}