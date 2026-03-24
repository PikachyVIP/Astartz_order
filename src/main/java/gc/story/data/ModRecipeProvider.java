package gc.story.data;

import java.util.concurrent.CompletableFuture;

import gc.story.inits.ModBlocks;
import gc.story.inits.ModItems;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
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
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.IFIRIUM, 9)
                        .pattern("l  ")
                        .pattern("   ")
                        .pattern("   ")
                        .input('l', ModBlocks.IFIRIUM_BLOCK)
                        .criterion(hasItem(ModBlocks.IFIRIUM_BLOCK), conditionsFromItem(ModBlocks.IFIRIUM_BLOCK))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.THICK_LEATHER, 1)
                        .pattern("ll")
                        .pattern("ll")
                        .input('l', Items.LEATHER)
                        .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.DUB_LEATHER, 1)
                        .pattern("rrr")
                        .pattern("lll")
                        .input('l', ModItems.THICK_LEATHER)
                        .input('r', Items.WHITE_WOOL)
                        .criterion(hasItem(Items.LEATHER), conditionsFromItem(Items.LEATHER))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.GUIDITE_HELMET, 1)
                        .pattern("lll")
                        .pattern("l l")
                        .input('l', ModItems.DUB_LEATHER)
                        .criterion(hasItem(ModItems.DUB_LEATHER), conditionsFromItem(ModItems.DUB_LEATHER))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.GUIDITE_CHESTPLATE, 1)
                        .pattern("l l")
                        .pattern("lll")
                        .pattern("lll")
                        .input('l', ModItems.DUB_LEATHER)
                        .criterion(hasItem(ModItems.DUB_LEATHER), conditionsFromItem(ModItems.DUB_LEATHER))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.GUIDITE_LEGGINGS, 1)
                        .pattern("lll")
                        .pattern("l l")
                        .pattern("l l")
                        .input('l', ModItems.DUB_LEATHER)
                        .criterion(hasItem(ModItems.DUB_LEATHER), conditionsFromItem(ModItems.DUB_LEATHER))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.GUIDITE_BOOTS, 1)
                        .pattern("l l")
                        .pattern("l l")
                        .input('l', ModItems.DUB_LEATHER)
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
                        .criterion(hasItem(ModItems.IFIRIUM), conditionsFromItem(ModItems.IFIRIUM))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.CLEANER_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.CLEANER_CHU)
                        .criterion(hasItem(ModItems.CLEANER_CHU), conditionsFromItem(ModItems.CLEANER_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.CRUSHER_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.CRUSHER_CHU)
                        .criterion(hasItem(ModItems.CRUSHER_CHU), conditionsFromItem(ModItems.CRUSHER_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModBlocks.CLEANER, 1)
                        .pattern("lsl")
                        .pattern("iri")
                        .pattern("ege")
                        .input('l', Items.IRON_BLOCK)
                        .input('g', Items.HEART_OF_THE_SEA)
                        .input('e', Items.ECHO_SHARD)
                        .input('s', Items.BREWING_STAND)
                        .input('r', ModItems.CLEANER_CHU)
                        .input('i', ModBlocks.IFIRIUM_BLOCK)
                        .criterion(hasItem(ModItems.CLEANER_CHU), conditionsFromItem(ModItems.CLEANER_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModBlocks.CRUSHER, 1)
                        .pattern("lsl")
                        .pattern("iri")
                        .pattern("ege")
                        .input('l', Items.IRON_BLOCK)
                        .input('g', Items.NETHER_STAR)
                        .input('e', Items.OBSIDIAN)
                        .input('s', Items.NETHERITE_PICKAXE)
                        .input('r', ModItems.CRUSHER_CHU)
                        .input('i', ModBlocks.IFIRIUM_BLOCK)
                        .criterion(hasItem(ModItems.CRUSHER_CHU), conditionsFromItem(ModItems.CRUSHER_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModBlocks.PRESS, 1)
                        .pattern("lsl")
                        .pattern("iri")
                        .pattern("ege")
                        .input('l', Items.IRON_BLOCK)
                        .input('g', Items.HEAVY_CORE)
                        .input('e', Items.ANVIL)
                        .input('s', Items.GLOWSTONE_DUST)
                        .input('r', ModItems.PRESS_CHU)
                        .input('i', ModBlocks.IFIRIUM_BLOCK)
                        .criterion(hasItem(ModItems.PRESS_CHU), conditionsFromItem(ModItems.PRESS_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModBlocks.SPLAV, 1)
                        .pattern("lsl")
                        .pattern("iri")
                        .pattern("ege")
                        .input('l', Items.IRON_BLOCK)
                        .input('g', Items.DRAGON_HEAD)
                        .input('e', ModItems.MASS)
                        .input('s', Items.BLAZE_POWDER)
                        .input('r', ModItems.SPLAV_CHU)
                        .input('i', ModBlocks.IFIRIUM_BLOCK)
                        .criterion(hasItem(ModItems.SPLAV_CHU), conditionsFromItem(ModItems.SPLAV_CHU))
                        .offerTo(exporter);



                createShaped(RecipeCategory.MISC, ModItems.BOOTS_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.BOOTS_CHU)
                        .criterion(hasItem(ModItems.BOOTS_CHU), conditionsFromItem(ModItems.BOOTS_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.LEGS_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.LEGS_CHU)
                        .criterion(hasItem(ModItems.LEGS_CHU), conditionsFromItem(ModItems.LEGS_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.CHEST_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.CHEST_CHU)
                        .criterion(hasItem(ModItems.CHEST_CHU), conditionsFromItem(ModItems.CHEST_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.HELMET_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.HELMET_CHU)
                        .criterion(hasItem(ModItems.HELMET_CHU), conditionsFromItem(ModItems.HELMET_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.AXE_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.AXE_CHU)
                        .criterion(hasItem(ModItems.AXE_CHU), conditionsFromItem(ModItems.AXE_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.SWORD_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.SWORD_CHU)
                        .criterion(hasItem(ModItems.SWORD_CHU), conditionsFromItem(ModItems.SWORD_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.PICKAXE_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.PICKAXE_CHU)
                        .criterion(hasItem(ModItems.PICKAXE_CHU), conditionsFromItem(ModItems.PICKAXE_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.SHOVEL_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.SHOVEL_CHU)
                        .criterion(hasItem(ModItems.SHOVEL_CHU), conditionsFromItem(ModItems.SHOVEL_CHU))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.HOE_CHU, 2)
                        .pattern("lll")
                        .pattern("lrl")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .input('r', ModItems.HOE_CHU)
                        .criterion(hasItem(ModItems.HOE_CHU), conditionsFromItem(ModItems.HOE_CHU))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.CRYSTAL_CLEAR, 9)
                        .pattern("l  ")
                        .pattern("   ")
                        .pattern("   ")
                        .input('l', ModBlocks.CRYSTAL_BLOCK)
                        .criterion(hasItem(ModBlocks.CRYSTAL_BLOCK), conditionsFromItem(ModBlocks.CRYSTAL_BLOCK))
                        .group("1")
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModBlocks.RAW_CRYSTAL_BLOCK, 1)
                        .pattern("lll")
                        .pattern("lll")
                        .pattern("lll")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .criterion(hasItem(ModItems.CRYSTAL_CLEAR), conditionsFromItem(ModItems.CRYSTAL_CLEAR))
                        .group("2")
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModItems.CRYSTAL_CLEAR, 9)
                        .pattern("l  ")
                        .pattern("   ")
                        .pattern("   ")
                        .input('l', ModBlocks.RAW_CRYSTAL_BLOCK)
                        .criterion(hasItem(ModBlocks.RAW_CRYSTAL_BLOCK), conditionsFromItem(ModBlocks.RAW_CRYSTAL_BLOCK))
                        .group("3")
                        .offerTo(exporter, "raw_to_crystal");
                createShaped(RecipeCategory.MISC, ModBlocks.CRYSTAL_BLOCK, 4)
                        .pattern("ll ")
                        .pattern("ll ")
                        .pattern("   ")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .criterion(hasItem(ModItems.CRYSTAL_CLEAR), conditionsFromItem(ModItems.CRYSTAL_CLEAR))
                        .offerTo(exporter, "crystalclaer_to_crystalblock");
                createShaped(RecipeCategory.MISC, ModBlocks.CRYSTAL_GRATE, 1)
                        .pattern("l l")
                        .pattern(" l ")
                        .pattern("l l")
                        .input('l', ModItems.CRYSTAL_CLEAR)
                        .criterion(hasItem(ModItems.CRYSTAL_CLEAR), conditionsFromItem(ModItems.CRYSTAL_CLEAR))
                        .offerTo(exporter);
                createShaped(RecipeCategory.MISC, ModBlocks.CRYSTAL_CUT_BLOCK, 4)
                        .pattern("ll ")
                        .pattern("ll ")
                        .pattern("   ")
                        .input('l', ModBlocks.CRYSTAL_BLOCK)
                        .criterion(hasItem(ModBlocks.CRYSTAL_BLOCK), conditionsFromItem(ModBlocks.CRYSTAL_BLOCK))
                        .offerTo(exporter, "crystalblock_to_cut");
                createShaped(RecipeCategory.MISC, ModBlocks.CHISELED_CRYSTAL_BLOCK, 4)
                        .pattern("ll ")
                        .pattern("ll ")
                        .pattern("   ")
                        .input('l', ModBlocks.CRYSTAL_CUT_BLOCK)
                        .criterion(hasItem(ModBlocks.CRYSTAL_CUT_BLOCK), conditionsFromItem(ModBlocks.CRYSTAL_CUT_BLOCK))
                        .offerTo(exporter, "cut_to_chiseled");
                createShaped(RecipeCategory.MISC, ModBlocks.CRYSTAL_CUT_BLOCK, 4)
                        .pattern("ll ")
                        .pattern("ll ")
                        .pattern("   ")
                        .input('l', ModBlocks.CHISELED_CRYSTAL_BLOCK)
                        .criterion(hasItem(ModBlocks.CHISELED_CRYSTAL_BLOCK), conditionsFromItem(ModBlocks.CHISELED_CRYSTAL_BLOCK))
                        .offerTo(exporter, "chiseled_to_cut");

                createShapeless(RecipeCategory.MISC, ModItems.POTION_INF, 1)
                        .input(ModItems.DUST_CLEAR)
                        .input(Items.POTION)
                        .criterion(hasItem(ModItems.DUST_CLEAR), conditionsFromItem(ModItems.DUST_CLEAR)).offerTo(exporter);
                createShapeless(RecipeCategory.MISC, ModItems.POTION_CLEAR, 1)
                        .input(ModItems.DUST)
                        .input(Items.POTION)
                        .criterion(hasItem(ModItems.DUST), conditionsFromItem(ModItems.DUST)).offerTo(exporter);

                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.SWORD_CHU),
                                Ingredient.ofItem(Items.NETHERITE_SWORD),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_SWORD)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_SWORD), conditionsFromItem(Items.NETHERITE_SWORD))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_SWORD) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.AXE_CHU),
                                Ingredient.ofItem(Items.NETHERITE_AXE),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_AXE)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_AXE), conditionsFromItem(Items.NETHERITE_AXE))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_AXE) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.PICKAXE_CHU),
                                Ingredient.ofItem(Items.NETHERITE_PICKAXE),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_PICAXE)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_PICKAXE), conditionsFromItem(Items.NETHERITE_PICKAXE))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_PICAXE) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.SHOVEL_CHU),
                                Ingredient.ofItem(Items.NETHERITE_SHOVEL),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_SHOVEL)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_SHOVEL), conditionsFromItem(Items.NETHERITE_SHOVEL))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_SHOVEL) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.HOE_CHU),
                                Ingredient.ofItem(Items.NETHERITE_HOE),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_HOE)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_HOE), conditionsFromItem(Items.NETHERITE_HOE))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_HOE) + "_smithing");

                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.BOOTS_CHU),
                                Ingredient.ofItem(Items.NETHERITE_BOOTS),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_BOOTS)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_BOOTS), conditionsFromItem(Items.NETHERITE_BOOTS))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_BOOTS) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.LEGS_CHU),
                                Ingredient.ofItem(Items.NETHERITE_LEGGINGS),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_LEGGINGS)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_LEGGINGS), conditionsFromItem(Items.NETHERITE_LEGGINGS))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_LEGGINGS) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.CHEST_CHU),
                                Ingredient.ofItem(Items.NETHERITE_CHESTPLATE),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_CHESTPLATE)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_CHESTPLATE), conditionsFromItem(Items.NETHERITE_CHESTPLATE))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_CHESTPLATE) + "_smithing");
                SmithingTransformRecipeJsonBuilder.create(
                                Ingredient.ofItem(ModItems.HELMET_CHU),
                                Ingredient.ofItem(Items.NETHERITE_HELMET),
                                Ingredient.ofItem(ModItems.CRYSTAL_IGNOT_STABLE),
                                RecipeCategory.COMBAT,
                                ModItems.IFIRIUM_HELMET)
                        .criterion(hasItem(ModItems.CRYSTAL_IGNOT_STABLE), conditionsFromItem(ModItems.CRYSTAL_IGNOT_STABLE))
                        .criterion(hasItem(Items.NETHERITE_HELMET), conditionsFromItem(Items.NETHERITE_HELMET))
                        .offerTo(exporter, getItemPath(ModItems.IFIRIUM_HELMET) + "_smithing");

            }
        };
    }

    @Override
    public String getName() {
        return "FabricDocsReferenceRecipeProvider";
    }
}