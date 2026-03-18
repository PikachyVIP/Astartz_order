package gc.story;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import gc.story.networking.ActivateAbilityPayload;

public class StoryClientNetwork {

    public static void sendActivateAbilityRequest() {
        if (ClientPlayNetworking.canSend(ActivateAbilityPayload.ID)) {
            ClientPlayNetworking.send(new ActivateAbilityPayload());
        }
    }
}