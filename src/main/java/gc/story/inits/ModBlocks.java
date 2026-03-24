package gc.story.inits;

import gc.story.Story;
import gc.story.blocks.cleaner.Cleaner;
import gc.story.blocks.crusher.Crusher;
import gc.story.blocks.Ifirium;
import gc.story.blocks.max.Max;
import gc.story.blocks.pc.Comp;
import gc.story.blocks.press.Press;
import gc.story.blocks.splav.Splav;
import gc.story.items.IfiriumItemBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {

    public static final Block CRUSHER = register(
            "crusher",
            Crusher::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK),
            true
    );
    public static final Block CLEANER = register(
            "cleaner",
            Cleaner::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK),
            true
    );
    public static final Block PRESS = register(
            "press",
            Press::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK),
            true
    );
    public static final Block SPLAV = register(
            "splav",
            Splav::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK),
            true
    );
    public static final Block COMP = register(
            "lectern",
            Comp::new,
            AbstractBlock.Settings.copy(Blocks.BEDROCK).nonOpaque(),
            true
    );
    public static final Block MAX = register(
            "max",
            Max::new,
            AbstractBlock.Settings.copy(Blocks.BEDROCK).nonOpaque(),
            true
    );

    public static final Block RAW_CRYSTAL_BLOCK = register(
            "raw_crystal",
            Block::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK),
            true
    );
    public static final Block CHISELED_CRYSTAL_BLOCK = register(
            "chiseled",
            Block::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK),
            true
    );
    public static final Block CRYSTAL_BLOCK = register(
            "crystal_block",
            Block::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK),
            true
    );
    public static final Block CRYSTAL_GRATE = register(
            "grate",
            TransparentBlock::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK)
                    .blockVision((state, world, pos) -> false)
                    .nonOpaque(),
            true
    );
    public static final Block CRYSTAL_CUT_BLOCK = register(
            "cut",
            Block::new,
            AbstractBlock.Settings.copy(Blocks.IRON_BLOCK),
            true
    );

    public static final Block IFIRIUM_ORE = registerIfirium(
            "ifirium_ore",
            settings -> new Ifirium(settings, 2, 5),
            AbstractBlock.Settings.create().mapColor(MapColor.BLACK).requiresTool().strength(30, 1200.0F).sounds(BlockSoundGroup.DEEPSLATE),
            true
    );

    public static final Block IFIRIUM_BLOCK = registerIfirium(
            "ifirium_block",
            settings -> new Ifirium(settings, 3, 10),
            AbstractBlock.Settings.create().mapColor(MapColor.BLACK).requiresTool().strength(30, 1200.0F).sounds(BlockSoundGroup.NETHERITE),
            true
    );

    private static Block registerIfirium(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);

            IfiriumItemBlock blockItem = new IfiriumItemBlock(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey(), 2);
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }
    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey).useBlockPrefixedTranslationKey());
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

        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CLEANER));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRUSHER));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(PRESS));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(SPLAV));

        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(RAW_CRYSTAL_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_CUT_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CHISELED_CRYSTAL_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(CRYSTAL_GRATE));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(COMP));
        ItemGroupEvents.modifyEntriesEvent(ModItemGroups.STORY_GROUP_KEY).register(group ->
                group.add(MAX));

    }
}