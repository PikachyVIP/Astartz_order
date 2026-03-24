package gc.story.blocks.pc;

import gc.story.inits.ModScreenHandlers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CompScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    public final CompBlockEntity blockEntity;

    public CompScreenHandler(int syncId, PlayerInventory inventory, BlockPos pos) {
        this(syncId, inventory, inventory.player.getEntityWorld().getBlockEntity(pos), new ArrayPropertyDelegate(2));
    }

    public CompScreenHandler(int syncId, PlayerInventory playerInventory,
                             BlockEntity blockEntity, PropertyDelegate arrayPropertyDelegate) {
        super(ModScreenHandlers.COMP_SCREEN_HANDLER, syncId);
        this.inventory = ((Inventory) blockEntity);
        this.blockEntity = ((CompBlockEntity) blockEntity);
        this.propertyDelegate = arrayPropertyDelegate;

        this.addSlot(new Slot(inventory, 0, 121, 28));
        this.addSlot(new Slot(inventory, 1, 121, 53));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addProperties(arrayPropertyDelegate);
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    public List<Task> getTasks() {
        return blockEntity.getTasks();
    }

    public void claimReward(String taskId) {
        blockEntity.claimReward(taskId);
    }

    public boolean giveRewardToOutputSlot(ItemStack reward) {
        ItemStack outputStack = this.inventory.getStack(1); // OUTPUT_SLOT

        // Проверяем, можно ли добавить награду в слот
        if (outputStack.isEmpty()) {
            this.inventory.setStack(1, reward.copy());
            return true;
        } else if (outputStack.getItem() == reward.getItem() &&
                outputStack.getCount() + reward.getCount() <= outputStack.getMaxCount()) {
            outputStack.increment(reward.getCount());
            return true;
        }
        return false;
    }

    public void handleTaskButtonClick(int taskIndex) {
        if (taskIndex < 0 || taskIndex >= blockEntity.getTasks().size()) {
            return;
        }

        Task task = blockEntity.getTasks().get(taskIndex);

        if (task.isCompleted()) {
            // Если задание выполнено - выдаём награду
            claimTaskReward(task);
        } else {
            // Если не выполнено - пробуем выполнить
            tryCompleteTask(task);
        }
    }

    private void tryCompleteTask(Task task) {
        if (task.isCompleted()) {
            return;
        }

        // Получаем предмет из INPUT_SLOT (слот 0)
        ItemStack inputStack = this.inventory.getStack(0);

        // Проверяем, соответствует ли предмет требуемому
        if (!inputStack.isEmpty() && inputStack.getItem() == task.getRequiredItem().getItem()) {
            int requiredCount = task.getRequiredCount();
            int currentProgress = task.getCurrentProgress();
            int needed = requiredCount - currentProgress;

            // Сколько предметов нужно взять для выполнения
            int toTake = Math.min(needed, inputStack.getCount());

            if (toTake > 0) {
                // Забираем предметы из слота
                inputStack.decrement(toTake);

                // Добавляем прогресс заданию
                task.addProgress(toTake);

                // Синхронизируем изменения
                blockEntity.sync();
            }
        }
    }

    private void claimTaskReward(Task task) {
        if (task.isCompleted()) {
            ItemStack reward = task.claimReward();
            if (!reward.isEmpty()) {
                // Пытаемся добавить награду в OUTPUT_SLOT (слот 1)
                ItemStack outputStack = this.inventory.getStack(1);

                if (outputStack.isEmpty()) {
                    this.inventory.setStack(1, reward.copy());
                } else if (outputStack.getItem() == reward.getItem() &&
                        outputStack.getCount() + reward.getCount() <= outputStack.getMaxCount()) {
                    outputStack.increment(reward.getCount());
                } else {
                    // Нет места для награды - возвращаем
                    return;
                }

                // Если задание одноразовое - удаляем его
                if (task.getType() == Task.TaskType.ONCE) {
                    blockEntity.removeTask(task.getId());
                }

                // Синхронизируем изменения
                blockEntity.sync();
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
