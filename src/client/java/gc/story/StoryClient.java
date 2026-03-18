package gc.story;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class StoryClient implements ClientModInitializer {

	private static KeyBinding specKey;

	@Override
	public void onInitializeClient() {



		specKey = new KeyBinding(
				"key.story.specability",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_C,
				KeyBinding.Category.create(Identifier.of("abi"))
		);

		KeyBindingHelper.registerKeyBinding(specKey);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (specKey.wasPressed()) {
				if (client.player != null) {
					StoryClientNetwork.sendActivateAbilityRequest();
				}
			}
		});
	}
}