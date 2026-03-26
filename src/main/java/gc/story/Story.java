package gc.story;

import gc.story.blocks.BlockRecipes;
import gc.story.commands.CompAdminCommands;
import gc.story.commands.StoryAdminCommand;
import gc.story.data.BiomeModificationInit;
import gc.story.events.*;
import gc.story.events.buff.ScavengerBuff;
import gc.story.events.debuff.*;
import gc.story.inits.*;
import gc.story.networking.StoryNetwork;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Story implements ModInitializer {
	public static final String MOD_ID = "story";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String str) {
		return Identifier.of(MOD_ID, str);
	}



	@Override
	public void onInitialize() {

		StoryNetwork.register();

		ModItems.initialize();
		ModItems.registerAll();
		ModBlocks.initialize();
		ModEntities.initialize();
		ModBlockEntities.initialize();
		BlockRecipes.registerRecipes();

		BiomeModificationInit.load();
		ModItemGroups.initialize();
		CommandRegistrationCallback.EVENT.register(StoryAdminCommand::register);
		CommandRegistrationCallback.EVENT.register(CompAdminCommands::register);

		InfectionHandler.register();
		StageHandler.register();
		MutationHandler.register();
		MutationStage2Handler.register();
		UnderwaterDebuff.register();
		BreakWingsDebuff.register();
		ScavengerDebuff.register();
		WeakHeartHandler.register();
		HungerCurseDebuff.register();
		FireDebuff.register();
		HitInfHandler.register();
		LostDebuff.register();
		AntigravLib.register();
		ScavGule.register();
		UnderWaterMaskHandler.register();
		ScalpelHandler.register();

		ModScreenHandlers.registerScreenHandlers();


		MutationStage3BuffHandler.register();

		System.out.println("Infection attachments: " +
				InfectionHandler.INFECTED_ATTACHMENT + ", " +
				InfectionHandler.CHANCE_ATTACHMENT);
		LOGGER.info("Hello Fabric world!");
	}
}