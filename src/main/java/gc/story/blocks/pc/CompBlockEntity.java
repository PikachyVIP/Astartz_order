package gc.story.blocks.pc;

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
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CompBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final List<Task> tasks = new ArrayList<>();

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = 0;

    public CompBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMP, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> CompBlockEntity.this.progress;
                    case 1 -> CompBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> CompBlockEntity.this.progress = value;
                    case 1 -> CompBlockEntity.this.maxProgress = value;
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
        return Text.translatable("");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CompScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    public void addTask(Task task) {
        tasks.add(task);
        markDirty();
        sync();
    }

    public boolean removeTask(String id) {
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        if (removed) {
            markDirty();
            sync();
        }
        return removed;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void claimReward(String taskId) {
        System.out.println("CLAIM");
        for (Task task : tasks) {
            if (task.getId().equals(taskId) && task.isCompleted()) {
                ItemStack reward = task.claimReward();
                if (!reward.isEmpty()) {
                    // TODO: добавить награду игроку
                }
                markDirty();
                sync();
                break;
            }
        }
    }

    public ItemStack getInputSlotStack() {
        return inventory.get(INPUT_SLOT);
    }

    public boolean removeFromInputSlot(int count) {
        ItemStack stack = inventory.get(INPUT_SLOT);
        if (stack.getCount() >= count) {
            stack.decrement(count);
            return true;
        }
        return false;
    }

    public void updateProgress(ItemStack item, int count) {
        boolean changed = false;
        for (Task task : tasks) {
            if (!task.isCompleted() && isMatchingItem(task.getRequiredItem(), item)) {
                task.addProgress(count);
                changed = true;
            }
        }
        if (changed) {
            markDirty();
            sync();
        }
    }

    private boolean isMatchingItem(ItemStack required, ItemStack given) {
        return required.getItem() == given.getItem();
    }

    public void sync() {
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }
    public ItemStack getOutputSlotStack() {
        return inventory.get(OUTPUT_SLOT);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        Inventories.writeData(view, inventory);
        view.putInt("computer.progress", progress);
        view.putInt("computer.max_progress", maxProgress);

        // Сохраняем задания
        view.putInt("computer.tasks_count", tasks.size());

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            WriteView taskView = view.get("task_" + i);

            taskView.putString("id", task.getId());
            taskView.putString("text", task.getText());
            taskView.putString("requiredItemId", Registries.ITEM.getId(task.getRequiredItem().getItem()).toString());
            taskView.putInt("requiredCount", task.getRequiredCount());
            taskView.putString("rewardItemId", Registries.ITEM.getId(task.getRewardItem().getItem()).toString());
            taskView.putInt("rewardCount", task.getRewardCount());
            taskView.putString("type", task.getType().name());
            taskView.putInt("currentProgress", task.getCurrentProgress());
            taskView.putBoolean("completed", task.isCompleted());
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        Inventories.readData(view, inventory);
        progress = view.getInt("computer.progress", 0);
        maxProgress = view.getInt("computer.max_progress", 0);

        // Загружаем задания
        tasks.clear();
        int tasksCount = view.getInt("computer.tasks_count", 0);

        for (int i = 0; i < tasksCount; i++) {
            ReadView taskView = view.getReadView("task_" + i);

            String id = taskView.getString("id", "");
            String text = taskView.getString("text", "");
            String requiredItemId = taskView.getString("requiredItemId", "");
            int requiredCount = taskView.getInt("requiredCount", 0);
            String rewardItemId = taskView.getString("rewardItemId", "");
            int rewardCount = taskView.getInt("rewardCount", 0);
            String typeStr = taskView.getString("type", "");
            int currentProgress = taskView.getInt("currentProgress", 0);
            boolean completed = taskView.getBoolean("completed", false);

            if (!id.isEmpty() && !requiredItemId.isEmpty() && !rewardItemId.isEmpty()) {
                ItemStack requiredItem = new ItemStack(Registries.ITEM.get(Identifier.tryParse(requiredItemId)));
                ItemStack rewardItem = new ItemStack(Registries.ITEM.get(Identifier.tryParse(rewardItemId)));
                Task.TaskType type = Task.TaskType.valueOf(typeStr);

                Task task = new Task(id, text, requiredItem, requiredCount, rewardItem, rewardCount, type);
                task.setState(currentProgress, completed);
                tasks.add(task);
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        // Логика работы компьютера
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