package gc.story.inits;

import gc.story.Story;
import gc.story.blocks.Ifirium;
import gc.story.items.IfiriumItemBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {

    public static final Block IFIRIUM_ORE = register(
            "ifirium_ore",
            settings -> new Ifirium(settings, 2, 5),
            AbstractBlock.Settings.create().mapColor(MapColor.BLACK).requiresTool().strength(30, 1200.0F).sounds(BlockSoundGroup.DEEPSLATE),
            true
    );

    public static final Block IFIRIUM_BLOCK = register(
            "ifirium_block",
            settings -> new Ifirium(settings, 3, 10),
            AbstractBlock.Settings.create().mapColor(MapColor.BLACK).requiresTool().strength(30, 1200.0F).sounds(BlockSoundGroup.NETHERITE),
            true
    );

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);

            IfiriumItemBlock blockItem = new IfiriumItemBlock(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey(), 2);
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Story.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Story.MOD_ID, name));
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_ORE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(IFIRIUM_BLOCK));

    }
}