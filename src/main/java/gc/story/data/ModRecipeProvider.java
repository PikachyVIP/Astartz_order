package gc.story.data;

import java.util.concurrent.CompletableFuture;

import gc.story.inits.ModBlocks;
import gc.story.inits.ModItems;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);

                createShaped(RecipeCategory.MISC, ModBlocks.IFIRIUM_BLOCK, 1)
                        .pattern("lll")
                        .pattern("lll")
                        .pattern("lll")
                        .input('l', ModItems.IFIRIUM)
                        .group("multi_bench")
                        .criterion(hasItem(ModBlocks.IFIRIUM_BLOCK), conditionsFromItem(ModBlocks.IFIRIUM_BLOCK))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.THICK_LEATHER, 1)
                        .pattern("ll")
                        .pattern("ll")
                        .input('l', Items.LEATHER)
                        .group("multi_bench")
                        .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.DUB_LEATHER, 1)
                        .pattern("rrr")
                        .pattern("lll")
                        .input('l', ModItems.THICK_LEATHER)
                        .input('r', Items.WHITE_WOOL)
                        .group("multi_bench")
                        .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.GUIDITE_HELMET, 1)
                        .pattern("lll")
                        .pattern("l l")
                        .input('l', ModItems.DUB_LEATHER)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.DUB_LEATHER), conditionsFromItem(ModItems.DUB_LEATHER))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.GUIDITE_CHESTPLATE, 1)
                        .pattern("l l")
                        .pattern("lll")
                        .pattern("lll")
                        .input('l', ModItems.DUB_LEATHER)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.DUB_LEATHER), conditionsFromItem(ModItems.DUB_LEATHER))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.GUIDITE_LEGGINGS, 1)
                        .pattern("lll")
                        .pattern("l l")
                        .pattern("l l")
                        .input('l', ModItems.DUB_LEATHER)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.DUB_LEATHER), conditionsFromItem(ModItems.DUB_LEATHER))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.GUIDITE_BOOTS, 1)
                        .pattern("l l")
                        .pattern("l l")
                        .input('l', ModItems.DUB_LEATHER)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.DUB_LEATHER), conditionsFromItem(ModItems.DUB_LEATHER))
                        .offerTo(exporter);


                createShaped(RecipeCategory.MISC, ModItems.UMBRELLA, 1)
                        .pattern("wlw")
                        .pattern("dbd")
                        .pattern(" b ")
                        .input('l', ModItems.IFIRIUM)
                        .input('d', ModItems.DUB_LEATHER)
                        .input('w', Items.BLACK_WOOL)
                        .input('b', Items.BREEZE_ROD)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.UMBRELLA_BLUE, 1)
                        .pattern("wlw")
                        .pattern("dbd")
                        .pattern(" b ")
                        .input('l', ModItems.IFIRIUM)
                        .input('d', ModItems.DUB_LEATHER)
                        .input('w', Items.BLUE_WOOL)
                        .input('b', Items.BREEZE_ROD)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.UMBRELLA_GREEN, 1)
                        .pattern("wlw")
                        .pattern("dbd")
                        .pattern(" b ")
                        .input('l', ModItems.IFIRIUM)
                        .input('d', ModItems.DUB_LEATHER)
                        .input('w', Items.GREEN_WOOL)
                        .input('b', Items.BREEZE_ROD)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.UMBRELLA_FLWR, 1)
                        .pattern("wlw")
                        .pattern("dbd")
                        .pattern(" b ")
                        .input('l', ModItems.IFIRIUM)
                        .input('d', ModItems.DUB_LEATHER)
                        .input('w', Items.PINK_WOOL)
                        .input('b', Items.BREEZE_ROD)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.UMBRELLA_HONEY, 1)
                        .pattern("wlw")
                        .pattern("dbd")
                        .pattern(" b ")
                        .input('l', ModItems.IFIRIUM)
                        .input('d', ModItems.DUB_LEATHER)
                        .input('w', Items.YELLOW_WOOL)
                        .input('b', Items.BREEZE_ROD)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.UMBRELLA_MSHRM, 1)
                        .pattern("wlw")
                        .pattern("dbd")
                        .pattern(" b ")
                        .input('l', ModItems.IFIRIUM)
                        .input('d', ModItems.DUB_LEATHER)
                        .input('w', Items.RED_WOOL)
                        .input('b', Items.BREEZE_ROD)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.UMBRELLA_PURPLE, 1)
                        .pattern("wlw")
                        .pattern("dbd")
                        .pattern(" b ")
                        .input('l', ModItems.IFIRIUM)
                        .input('d', ModItems.DUB_LEATHER)
                        .input('w', Items.PURPLE_WOOL)
                        .input('b', Items.BREEZE_ROD)
                        .group("multi_bench")
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);
            }


        };
    }

    @Override
    public String getName() {
        return "FabricDocsReferenceRecipeProvider";
    }
}