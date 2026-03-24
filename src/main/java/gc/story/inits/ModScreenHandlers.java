package gc.story.inits;

import gc.story.Story;
import gc.story.blocks.cleaner.CleanerBlockEntity;
import gc.story.blocks.cleaner.CleanerScreenHandler;
import gc.story.blocks.crusher.CrusherScreenHandler;
import gc.story.blocks.max.MaxScreenHandler;
import gc.story.blocks.pc.CompScreenHandler;
import gc.story.blocks.press.PressScreenHandler;
import gc.story.blocks.splav.SplavScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<CrusherScreenHandler> CRUSHER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Story.MOD_ID, "crusher_screen_handler"),
                    new ExtendedScreenHandlerType<>(CrusherScreenHandler::new, BlockPos.PACKET_CODEC));
    public static final ScreenHandlerType<CleanerScreenHandler > CLEANER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Story.MOD_ID, "cleaner_screen_handler"),
                    new ExtendedScreenHandlerType<>(CleanerScreenHandler::new, BlockPos.PACKET_CODEC));
    public static final ScreenHandlerType<PressScreenHandler> PRESS_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Story.MOD_ID, "press_screen_handler"),
                    new ExtendedScreenHandlerType<>(PressScreenHandler::new, BlockPos.PACKET_CODEC));
    public static final ScreenHandlerType<SplavScreenHandler > SPLAV_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Story.MOD_ID, "splav_screen_handler"),
                    new ExtendedScreenHandlerType<>(SplavScreenHandler::new, BlockPos.PACKET_CODEC));
    public static final ScreenHandlerType<CompScreenHandler > COMP_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Story.MOD_ID, "comp_screen_handler"),
                    new ExtendedScreenHandlerType<>(CompScreenHandler::new, BlockPos.PACKET_CODEC));
    public static final ScreenHandlerType<MaxScreenHandler > MAX_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Story.MOD_ID, "max_screen_handler"),
                    new ExtendedScreenHandlerType<>(MaxScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
    }
}
