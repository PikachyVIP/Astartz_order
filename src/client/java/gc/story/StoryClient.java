package gc.story;

import gc.story.blocks.cleaner.CleanerScreen;
import gc.story.blocks.crusher.Crusher;
import gc.story.blocks.crusher.CrusherScreen;
import gc.story.blocks.pc.CompScreen;
import gc.story.blocks.pc.MaxScreen;
import gc.story.blocks.press.PressScreen;
import gc.story.blocks.splav.SplavScreen;
import gc.story.inits.ModBlocks;
import gc.story.inits.ModEntities;
import gc.story.inits.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.ChunkSection;
import org.lwjgl.glfw.GLFW;

public class StoryClient implements ClientModInitializer {

	private static KeyBinding specKey;

	@Override
	public void onInitializeClient() {

		BlockRenderLayerMap.putBlock(ModBlocks.CRYSTAL_GRATE, BlockRenderLayer.TRANSLUCENT);

		EntityRendererRegistry.register(ModEntities.KIRPITH_ENTITY_TYPE,
				(context) -> new FlyingItemEntityRenderer<>(context));

		HandledScreens.register(ModScreenHandlers.CRUSHER_SCREEN_HANDLER, CrusherScreen::new);
		HandledScreens.register(ModScreenHandlers.CLEANER_SCREEN_HANDLER, CleanerScreen::new);
		HandledScreens.register(ModScreenHandlers.PRESS_SCREEN_HANDLER, PressScreen::new);
		HandledScreens.register(ModScreenHandlers.SPLAV_SCREEN_HANDLER, SplavScreen::new);
		HandledScreens.register(ModScreenHandlers.COMP_SCREEN_HANDLER, CompScreen::new);
		HandledScreens.register(ModScreenHandlers.MAX_SCREEN_HANDLER, MaxScreen::new);


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