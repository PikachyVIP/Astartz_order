package gc.story.events;

import gc.story.events.debuff.FireDebuff;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ArmorHandler {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ArmorHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 40 != 0) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if(InfectionHandler.hasFullIfiriumArmor(player)){
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.WATER_BREATHING,
                        100,
                        1,
                        false,
                        false,
                        true
                ));
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.REGENERATION,
                        100,
                        0,
                        false,
                        false,
                        true
                ));
            }
        }
    }
}
