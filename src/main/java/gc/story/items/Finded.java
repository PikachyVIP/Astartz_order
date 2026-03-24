package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Finded extends Item {
    private static final String LAST_USE_TIME_KEY = "last_use_time";
    private static final String SPECTATOR_ACTIVE_KEY = "spectator_active";
    private static final String SPECTATOR_START_TIME_KEY = "spectator_start_time";

    private static final int COOLDOWN_TICKS = 6000;
    private static final int SPECTATOR_DURATION = 200;

    public Finded(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.LOST)) return ActionResult.FAIL;
            if (isSpectatorActive(stack)) {
                player.sendMessage(Text.literal("§cРежим спектатора уже активен!"), true);
                return ActionResult.FAIL;
            }

            if (canUse(stack, world)) {
                enableSpectator(player, stack);
                setLastUseTime(stack, world.getTime());
                setSpectatorActive(stack, true);
                setSpectatorStartTime(stack, world.getTime());
                player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_TICKS);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.0f);
                player.sendMessage(Text.literal("§bРежим спектатора активирован на 10 секунд!"), true);
                return ActionResult.SUCCESS;
            } else {
                long remainingTime = getRemainingCooldown(stack, world);
                long remainingSeconds = remainingTime / 20;
                player.sendMessage(Text.literal("§cПредмет на перезарядке! Осталось: " + remainingSeconds + " сек."), true);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (!(entity instanceof ServerPlayerEntity player)) return;
        if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.LOST)) return;
        if (isSpectatorActive(stack)) {
            Long startTime = getSpectatorStartTime(stack);
            if (startTime != null) {
                long currentTime = world.getTime();
                long elapsed = currentTime - startTime;

                if (elapsed >= SPECTATOR_DURATION) {
                    disableSpectator(player, stack);
                    player.sendMessage(Text.literal("§7Режим спектатора закончился"), true);
                } else {
                    if (player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
                        player.changeGameMode(GameMode.SPECTATOR);
                    }
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 30, 0, true, false, true));
                }
            }
        }
    }

    private void enableSpectator(ServerPlayerEntity player, ItemStack stack) {
        player.changeGameMode(GameMode.SPECTATOR);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, SPECTATOR_DURATION, 0, true, false, true));
    }

    private void disableSpectator(ServerPlayerEntity player, ItemStack stack) {
        if (player.interactionManager.getGameMode() == GameMode.SPECTATOR) {
            player.changeGameMode(GameMode.SURVIVAL);
        }
        player.removeStatusEffect(StatusEffects.INVISIBILITY);
        setSpectatorActive(stack, false);
    }

    private boolean canUse(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) return true;
        long currentTime = world.getTime();
        return (currentTime - lastUseTime) >= COOLDOWN_TICKS && !isSpectatorActive(stack);
    }

    private long getRemainingCooldown(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) return 0;
        long currentTime = world.getTime();
        long elapsed = currentTime - lastUseTime;
        return Math.max(0, COOLDOWN_TICKS - elapsed);
    }

    private void setLastUseTime(ItemStack stack, long time) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putLong(LAST_USE_TIME_KEY, time));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private Long getLastUseTime(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(LAST_USE_TIME_KEY)) {
                return nbt.getLong(LAST_USE_TIME_KEY).get();
            }
        }
        return null;
    }

    private void setSpectatorActive(ItemStack stack, boolean active) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putBoolean(SPECTATOR_ACTIVE_KEY, active));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private boolean isSpectatorActive(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(SPECTATOR_ACTIVE_KEY)) {
                return nbt.getBoolean(SPECTATOR_ACTIVE_KEY).get();
            }
        }
        return false;
    }

    private void setSpectatorStartTime(ItemStack stack, long time) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putLong(SPECTATOR_START_TIME_KEY, time));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private Long getSpectatorStartTime(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(SPECTATOR_START_TIME_KEY)) {
                return nbt.getLong(SPECTATOR_START_TIME_KEY).get();
            }
        }
        return null;
    }
}