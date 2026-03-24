package gc.story.blocks.splav;

import gc.story.blocks.BlockRecipes;
import gc.story.blocks.ImplementedInventory;
import gc.story.inits.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SplavBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    private static final int INPUT_SLOT_1 = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int OUTPUT_SLOT = 2;

    private static final int MAX_PROGRESS_DISPLAY = 32766;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 0;
    private int realProgress = 0;
    private int progressMultiplier = 1;
    private BlockRecipes.SplavRecipe currentRecipe = null;

    public SplavBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPLAV, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> SplavBlockEntity.this.progress;
                    case 1 -> SplavBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> SplavBlockEntity.this.progress = value;
                    case 1 -> SplavBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.story.splav");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SplavScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, inventory);
        view.putInt("splav.progress", progress);
        view.putInt("splav.max_progress", maxProgress);
        view.putInt("splav.real_progress", realProgress);
        view.putInt("splav.multiplier", progressMultiplier);
    }

    @Override
    protected void readData(ReadView view) {
        Inventories.readData(view, inventory);
        progress = view.getInt("splav.progress", 0);
        maxProgress = view.getInt("splav.max_progress", 0);
        realProgress = view.getInt("splav.real_progress", 0);
        progressMultiplier = view.getInt("splav.multiplier", 1);
        super.readData(view);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (hasRecipe()) {
            increaseCraftingProgress();
            markDirty(world, pos, state);
            world.setBlockState(pos, state.with(Splav.ACTIVE, true));

            if (hasCraftingFinished()) {
                craftItem();
                resetProgress();
                currentRecipe = null;
                world.setBlockState(pos, state.with(Splav.ACTIVE, false));
            }
        } else {
            resetProgress();
            world.setBlockState(pos, state.with(Splav.ACTIVE, false));
            currentRecipe = null;
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.realProgress = 0;
        this.progressMultiplier = 1;
        this.maxProgress = 0;
    }

    private void craftItem() {
        if (currentRecipe == null) return;

        ItemStack output = currentRecipe.getOutputStack();

        this.removeStack(INPUT_SLOT_1, currentRecipe.getInput1Count());
        this.removeStack(INPUT_SLOT_2, currentRecipe.getInput2Count());

        if (this.getStack(OUTPUT_SLOT).isEmpty()) {
            this.setStack(OUTPUT_SLOT, output.copy());
        } else {
            this.getStack(OUTPUT_SLOT).increment(output.getCount());
        }
    }

    private boolean hasCraftingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseCraftingProgress() {
        int recipeTime = currentRecipe.getTimeTicks();

        if (recipeTime <= MAX_PROGRESS_DISPLAY) {
            if (progressMultiplier != 1) {
                progressMultiplier = 1;
                realProgress = progress;
            }
            realProgress++;
            progress++;
        }
        else {
            if (progressMultiplier == 1 && maxProgress == 0) {
                progressMultiplier = (int) Math.ceil((double) recipeTime / MAX_PROGRESS_DISPLAY);
                maxProgress = (int) Math.ceil((double) recipeTime / progressMultiplier);
                if (maxProgress > MAX_PROGRESS_DISPLAY) {
                    maxProgress = MAX_PROGRESS_DISPLAY;
                }
            }

            realProgress++;

            int expectedProgress = realProgress / progressMultiplier;
            if (expectedProgress > progress) {
                progress = Math.min(expectedProgress, maxProgress);
            }
        }
    }

    private boolean hasRecipe() {
        ItemStack inputStack1 = this.getStack(INPUT_SLOT_1);
        ItemStack inputStack2 = this.getStack(INPUT_SLOT_2);

        if (inputStack1.isEmpty() || inputStack2.isEmpty()) return false;

        BlockRecipes.SplavRecipe newRecipe = BlockRecipes.getSplavRecipe(inputStack1.getItem(), inputStack2.getItem());
        if (newRecipe == null) return false;

        // Проверка на смену рецепта
        boolean recipeChanged = currentRecipe != newRecipe;

        if (recipeChanged) {
            int recipeTime = newRecipe.getTimeTicks();

            if (recipeTime <= MAX_PROGRESS_DISPLAY) {
                this.maxProgress = recipeTime;
                this.progressMultiplier = 1;
                this.realProgress = 0;
                this.progress = 0;
            } else {
                this.progressMultiplier = (int) Math.ceil((double) recipeTime / MAX_PROGRESS_DISPLAY);
                this.maxProgress = (int) Math.ceil((double) recipeTime / progressMultiplier);
                if (this.maxProgress > MAX_PROGRESS_DISPLAY) {
                    this.maxProgress = MAX_PROGRESS_DISPLAY;
                }
                this.realProgress = 0;
                this.progress = 0;
            }
        }

        currentRecipe = newRecipe;

        if (inputStack1.getCount() < currentRecipe.getInput1Count()) return false;
        if (inputStack2.getCount() < currentRecipe.getInput2Count()) return false;

        return canInsertAmountIntoOutputSlot(currentRecipe.getOutputCount()) &&
                canInsertItemIntoOutputSlot(currentRecipe.getOutputStack());
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return this.getStack(OUTPUT_SLOT).isEmpty() ||
                this.getStack(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        int currentCount = this.getStack(OUTPUT_SLOT).getCount();
        int maxCount = this.getStack(OUTPUT_SLOT).getMaxCount();
        return currentCount + count <= maxCount;
    }

    public long getRealRemainingTicks() {
        if (currentRecipe == null) return 0;

        int totalRealTicks = currentRecipe.getTimeTicks();
        int currentRealProgress = realProgress;

        long remaining = (long) totalRealTicks - (long) currentRealProgress;
        return Math.max(0, remaining);
    }

    public int getProgressMultiplier() {
        return progressMultiplier;
    }



    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }
}