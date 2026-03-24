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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Deceit extends Item {
    private static final String LAST_USE_TIME_KEY = "last_use_time";
    private static final String EFFECTS_ACTIVE_KEY = "effects_active";
    private static final String EFFECTS_START_TIME_KEY = "effects_start_time";

    private static final int COOLDOWN_TICKS = 6000;
    private static final int BUFF_DURATION = 6000;
    private static final int DEBUFF_DURATION = 1200;
    private static final int ABSORPTION_AMPLIFIER = 4;

    public Deceit(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.WEAK_HEART)) return ActionResult.FAIL;
            if (isEffectsActive(stack)) {
                player.sendMessage(Text.literal("§cЭффекты уже активны!"), true);
                return ActionResult.FAIL;
            }

            if (canUse(stack, world)) {
                applyBuffs(player, stack);
                setLastUseTime(stack, world.getTime());
                setEffectsActive(stack, true);
                setEffectsStartTime(stack, world.getTime());
                player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_TICKS);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 1.0f);
                player.sendMessage(Text.literal("§aСила обмана активирована на 5 минут!"), true);
                return ActionResult.SUCCESS;
            } else {
                long remainingTime = getRemainingCooldown(stack, world);
                long remainingSeconds = remainingTime / 20;
                long remainingMinutes = remainingSeconds / 60;
                remainingSeconds = remainingSeconds % 60;
                player.sendMessage(Text.literal("§cПредмет на перезарядке! Осталось: " + remainingMinutes + " мин " + remainingSeconds + " сек."), true);
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }


    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (!(entity instanceof ServerPlayerEntity player)) return;
        if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.WEAK_HEART)) return;
        if (isEffectsActive(stack)) {
            Long startTime = getEffectsStartTime(stack);
            if (startTime != null) {
                long currentTime = world.getTime();
                long elapsed = currentTime - startTime;

                if (elapsed >= BUFF_DURATION) {
                    removeBuffs(player);
                    applyDebuffs(player);
                    setEffectsActive(stack, false);
                }
            }
        }
    }

    private void applyBuffs(ServerPlayerEntity player, ItemStack stack) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, BUFF_DURATION, ABSORPTION_AMPLIFIER, true, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, BUFF_DURATION, 2, true, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, BUFF_DURATION, 2, true, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, BUFF_DURATION, 0, true, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, BUFF_DURATION, 2, true, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, BUFF_DURATION, 0, true, true, true));
    }

    private void removeBuffs(ServerPlayerEntity player) {
        player.removeStatusEffect(StatusEffects.ABSORPTION);
        player.removeStatusEffect(StatusEffects.SPEED);
        player.removeStatusEffect(StatusEffects.JUMP_BOOST);
        player.removeStatusEffect(StatusEffects.REGENERATION);
        player.removeStatusEffect(StatusEffects.STRENGTH);
        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }

    private void applyDebuffs(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, DEBUFF_DURATION, 1, true, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, DEBUFF_DURATION, 1, true, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, DEBUFF_DURATION, 1, true, true, true));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, DEBUFF_DURATION, 0, true, true, true));
        player.sendMessage(Text.literal("§cРасплата: 1 минута слабости, замедления, усталости и слепоты"), true);
    }

    private boolean canUse(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) return true;
        long currentTime = world.getTime();
        return (currentTime - lastUseTime) >= COOLDOWN_TICKS && !isEffectsActive(stack);
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

    private void setEffectsActive(ItemStack stack, boolean active) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putBoolean(EFFECTS_ACTIVE_KEY, active));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private boolean isEffectsActive(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(EFFECTS_ACTIVE_KEY)) {
                return nbt.getBoolean(EFFECTS_ACTIVE_KEY).get();
            }
        }
        return false;
    }

    private void setEffectsStartTime(ItemStack stack, long time) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putLong(EFFECTS_START_TIME_KEY, time));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private Long getEffectsStartTime(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(EFFECTS_START_TIME_KEY)) {
                return nbt.getLong(EFFECTS_START_TIME_KEY).get();
            }
        }
        return null;
    }
}