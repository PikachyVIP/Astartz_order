package gc.story.events.buff;

import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;


public class AntigravBuff {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(AntigravBuff::onServerTick);
    }


    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.ANTIGRAVITY)) {
                applyStoneKoja(player);
            }
        }
    }
    public static void applyStoneKoja(ServerPlayerEntity player){
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.STRENGTH,
                60,
                0,
                false,
                false,
                true
        ));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.JUMP_BOOST,
                60,
                1,
                false,
                false,
                true
        ));
    }
}
