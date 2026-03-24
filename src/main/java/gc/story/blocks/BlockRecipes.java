package gc.story.blocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import gc.story.inits.ModItems;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BlockRecipes {

    public static class Recipe {
        private final Item input;
        private final int inputCount;
        private final Item output;
        private final int outputCount;
        private final int timeTicks;

        public Recipe(Item input, int inputCount, Item output, int outputCount, int timeTicks) {
            this.input = input;
            this.inputCount = inputCount;
            this.output = output;
            this.outputCount = outputCount;
            this.timeTicks = timeTicks;
        }

        public Item getInput() { return input; }
        public int getInputCount() { return inputCount; }
        public Item getOutput() { return output; }
        public int getOutputCount() { return outputCount; }
        public int getTimeTicks() { return timeTicks; }

        public ItemStack getOutputStack() {
            return new ItemStack(output, outputCount);
        }
    }

    public static class SplavRecipe {
        private final Item input1;
        private final int input1Count;
        private final Item input2;
        private final int input2Count;
        private final Item output;
        private final int outputCount;
        private final int timeTicks;

        public SplavRecipe(Item input1, int input1Count, Item input2, int input2Count, Item output, int outputCount, int timeTicks) {
            this.input1 = input1;
            this.input1Count = input1Count;
            this.input2 = input2;
            this.input2Count = input2Count;
            this.output = output;
            this.outputCount = outputCount;
            this.timeTicks = timeTicks;
        }

        public Item getInput1() { return input1; }
        public int getInput1Count() { return input1Count; }
        public Item getInput2() { return input2; }
        public int getInput2Count() { return input2Count; }
        public Item getOutput() { return output; }
        public int getOutputCount() { return outputCount; }
        public int getTimeTicks() { return timeTicks; }

        public ItemStack getOutputStack() {
            return new ItemStack(output, outputCount);
        }
    }

    public static final Map<Item, Recipe> CLEANER_RECIPES = new HashMap<>();
    public static final Map<Item, Recipe> CRUSHER_RECIPES = new HashMap<>();
    public static final Map<Item, Recipe> PRESS_RECIPES = new HashMap<>();
    public static final Map<SplavKey, SplavRecipe> SPLAY_RECIPES = new HashMap<>();

    public static class SplavKey {
        private final Item input1;
        private final Item input2;

        public SplavKey(Item input1, Item input2) {
            this.input1 = input1;
            this.input2 = input2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SplavKey splavKey = (SplavKey) o;
            return input1 == splavKey.input1 && input2 == splavKey.input2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(input1, input2);
        }
    }

    public static void registerRecipes() {
        //ОЧИСТИТЕЛЬ
        registerCleanerRecipe(ModItems.IFIRIUM, 1, ModItems.CRYSTAL_CLEAR, 1, 1200*20);
        registerCleanerRecipe(ModItems.DUST, 1, ModItems.DUST_CLEAR, 1, 1200*20);
        registerCleanerRecipe(ModItems.CRYSTAL_IGNOT_INF, 1, ModItems.CRYSTAL_IGNOT_CLEAR, 1, 1200*20);
        registerCleanerRecipe(ModItems.CRYSTAL_IGNOT_UNSTABLE, 1, ModItems.CRYSTAL_IGNOT_STABLE, 1, 1200*20);

        //ДРОБИЛКА
        registerCrusherRecipe(ModItems.CRYSTAL_CLEAR, 1, ModItems.DUST, 1, 1200*40);

        //ПРЕСС
        registerPressRecipe(ModItems.DUST, 1, ModItems.MASS, 1, 1200*30);

        //СПЛАВЫ
        registerSplavRecipe(ModItems.MASS, 1, Items.GOLD_INGOT, 1, ModItems.CRYSTAL_IGNOT_INF, 1, 1200*30);
        registerSplavRecipe(Items.NETHERITE_INGOT, 1, Items.BRICK, 1, ModItems.KIRPITCH, 1, 1200*30);
        registerSplavRecipe(ModItems.CRYSTAL_IGNOT_CLEAR, 1, Items.NETHERITE_INGOT, 1, ModItems.CRYSTAL_IGNOT_UNSTABLE, 1, 1200*60);


    }

    public static void registerCleanerRecipe(Item input, int inputCount, Item output, int outputCount, int timeTicks) {
        CLEANER_RECIPES.put(input, new Recipe(input, inputCount, output, outputCount, timeTicks));
    }

    public static void registerCrusherRecipe(Item input, int inputCount, Item output, int outputCount, int timeTicks) {
        CRUSHER_RECIPES.put(input, new Recipe(input, inputCount, output, outputCount, timeTicks));
    }

    public static void registerPressRecipe(Item input, int inputCount, Item output, int outputCount, int timeTicks) {
        PRESS_RECIPES.put(input, new Recipe(input, inputCount, output, outputCount, timeTicks));
    }

    public static void registerSplavRecipe(Item input1, int input1Count, Item input2, int input2Count, Item output, int outputCount, int timeTicks) {
        SPLAY_RECIPES.put(new SplavKey(input1, input2), new SplavRecipe(input1, input1Count, input2, input2Count, output, outputCount, timeTicks));
        SPLAY_RECIPES.put(new SplavKey(input2, input1), new SplavRecipe(input1, input1Count, input2, input2Count, output, outputCount, timeTicks));
    }

    public static Recipe getCleanerRecipe(Item input) {
        return CLEANER_RECIPES.get(input);
    }

    public static Recipe getCrusherRecipe(Item input) {
        return CRUSHER_RECIPES.get(input);
    }

    public static Recipe getPressRecipe(Item input) {
        return PRESS_RECIPES.get(input);
    }

    public static SplavRecipe getSplavRecipe(Item input1, Item input2) {
        SplavRecipe recipe = SPLAY_RECIPES.get(new SplavKey(input1, input2));
        if (recipe == null) {
            recipe = SPLAY_RECIPES.get(new SplavKey(input2, input1));
        }
        return recipe;
    }
}