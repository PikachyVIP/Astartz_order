package gc.story.events.buff;

import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class HungryCurseBuff {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(HungryCurseBuff::onServerTick);
    }


    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.HUNGER_CURSE)) {
                applySpeed(player);
            }
        }
    }
    public static void applySpeed(ServerPlayerEntity player){
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                90,
                0,
                false,
                false,
                true
        ));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HASTE,
                90,
                1,
                false,
                false,
                true
        ));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.NIGHT_VISION,
                200,
                0,
                false,
                false,
                true
        ));
    }
}
