package gc.story.inits;

import gc.story.Story;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final RegistryKey<ItemGroup> STORY_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(Story.MOD_ID, "story_group")
    );

    public static final ItemGroup STORY_GROUP = register(
            STORY_GROUP_KEY,
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemGroup." + Story.MOD_ID + ".story_group"))
                    .icon(() -> new ItemStack(ModItems.IFIRIUM))
                    .entries((context, entries) -> {
                        entries.add(ModItems.IFIRIUM);
                        entries.add(ModBlocks.IFIRIUM_ORE);
                        entries.add(ModBlocks.IFIRIUM_BLOCK);
                    })
                    .build()
    );

    private static ItemGroup register(RegistryKey<ItemGroup> key, ItemGroup itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, key.getValue(), itemGroup);
    }

    public static void initialize() {
        Story.LOGGER.info("Registering item groups for " + Story.MOD_ID);
    }
}