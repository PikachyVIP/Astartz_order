package gc.story.blocks.pc;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class Task {

    public enum TaskType {
        ONCE,      // Одноразовое задание
        INFINITE   // Бесконечное (можно выполнять много раз)
    }

    private final String id;
    private final String text;
    private final ItemStack requiredItem;
    private final int requiredCount;
    private final ItemStack rewardItem;
    private final int rewardCount;
    private final TaskType type;
    private int currentProgress;
    private boolean completed;

    public Task(String id, String text, ItemStack requiredItem, int requiredCount,
                ItemStack rewardItem, int rewardCount, TaskType type) {
        this.id = id;
        this.text = text;
        this.requiredItem = requiredItem;
        this.requiredCount = requiredCount;
        this.rewardItem = rewardItem;
        this.rewardCount = rewardCount;
        this.type = type;
        this.currentProgress = 0;
        this.completed = false;
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public ItemStack getRequiredItem() { return requiredItem; }
    public int getRequiredCount() { return requiredCount; }
    public ItemStack getRewardItem() { return rewardItem; }
    public int getRewardCount() { return rewardCount; }
    public TaskType getType() { return type; }
    public int getCurrentProgress() { return currentProgress; }
    public boolean isCompleted() { return completed; }

    public void addProgress(int amount) {
        if (!completed) {
            currentProgress += amount;
            // Не даём прогрессу превысить requiredCount
            if (currentProgress > requiredCount) {
                currentProgress = requiredCount;
            }
            // Если достигли или превысили requiredCount - задание выполнено
            if (currentProgress >= requiredCount) {
                completed = true;
            }
        }
    }

    public ItemStack claimReward() {
        if (completed) {
            ItemStack reward = rewardItem.copy();
            reward.setCount(rewardCount);

            if (type == TaskType.ONCE) {
                // Для одноразовых не сбрасываем состояние, задание будет удалено
                return reward;
            } else {
                // Для бесконечных сбрасываем прогресс
                currentProgress = 0;
                completed = false;
                return reward;
            }
        }
        return ItemStack.EMPTY;
    }

    public void setState(int progress, boolean completed) {
        this.currentProgress = progress;
        this.completed = completed;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", id);
        nbt.putString("text", text);

        // Сохраняем ItemStack через Identifier
        nbt.putString("requiredItemId", Registries.ITEM.getId(requiredItem.getItem()).toString());
        nbt.putInt("requiredCount", requiredCount);
        nbt.putString("rewardItemId", Registries.ITEM.getId(rewardItem.getItem()).toString());
        nbt.putInt("rewardCount", rewardCount);

        nbt.putString("type", type.name());
        nbt.putInt("currentProgress", currentProgress);
        nbt.putBoolean("completed", completed);

        return nbt;
    }

    public static Task fromNbt(NbtCompound nbt) {
        String id = nbt.getString("id", "");
        String text = nbt.getString("text", "");

        String requiredItemId = nbt.getString("requiredItemId", "");
        ItemStack requiredItem = new ItemStack(Registries.ITEM.get(Identifier.tryParse(requiredItemId)));
        int requiredCount = nbt.getInt("requiredCount", 0);

        String rewardItemId = nbt.getString("rewardItemId", "");
        ItemStack rewardItem = new ItemStack(Registries.ITEM.get(Identifier.tryParse(rewardItemId)));
        int rewardCount = nbt.getInt("rewardCount", 0);

        TaskType type = TaskType.valueOf(nbt.getString("type", ""));

        Task task = new Task(id, text, requiredItem, requiredCount, rewardItem, rewardCount, type);
        task.currentProgress = nbt.getInt("currentProgress", 0);
        task.completed = nbt.getBoolean("completed", false);

        return task;
    }
}