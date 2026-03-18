package gc.story.networking;

import gc.story.events.buff.BrokenWingsBuff;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class StoryNetwork {

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ActivateAbilityPayload.ID, ActivateAbilityPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                ActivateAbilityPayload.ID,
                (payload, context) -> {
                    context.server().execute(() -> {
                        ServerPlayerEntity player = context.player();
                        MinecraftServer server = context.server();

                      //  BrokenWingsBuff.activateAbility(player, server.getOverworld());
                    });
                }
        );
    }
}