package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class Liberation extends Item {
    private static final String LAST_USE_TIME_KEY = "last_use_time";
    private static final String FLIGHT_ACTIVE_KEY = "flight_active";
    private static final int FLIGHT_DURATION = 2400;
    private static final int COOLDOWN_DURATION = 6000;

    public Liberation(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) return ActionResult.SUCCESS;
        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.ANTIGRAVITY)) return ActionResult.FAIL;
            if (isFlightActive(stack)) {
                player.sendMessage(Text.literal("§cПолёт уже активен!"), true);
                return ActionResult.FAIL;
            }
            if (canUse(stack, world)) {
                enableFlight(player, stack);
                setLastUseTime(stack, world.getTime());
                setFlightActive(stack, true);
                player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_DURATION);
                player.sendMessage(Text.literal("§aПолёт активирован на 2 минуты!"), true);
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
        if (entity instanceof ServerPlayerEntity player) {
            if (isFlightActive(stack)) {
                Long lastUseTime = getLastUseTime(stack);
                if (lastUseTime != null) {
                    long currentTime = world.getTime();
                    if ((currentTime - lastUseTime) >= FLIGHT_DURATION) {
                        disableFlight(player, stack);
                        return;
                    }
                    if (!player.getAbilities().allowFlying && !player.isCreative() && !player.isSpectator()) {
                        player.getAbilities().allowFlying = true;
                        player.getAbilities().flying = true;
                        player.sendAbilitiesUpdate();
                    }
                }
            } else {
                if (!player.isCreative() && !player.isSpectator() && player.getAbilities().flying) {
                    if (!hasActiveFlightItem(player)) {
                        player.getAbilities().allowFlying = false;
                        player.getAbilities().flying = false;
                        player.sendAbilitiesUpdate();
                    }
                }
            }
        }
    }

    private boolean hasActiveFlightItem(ServerPlayerEntity player) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.getItem() instanceof Liberation && isFlightActive(stack)) {
                Long lastUseTime = getLastUseTime(stack);
                if (lastUseTime != null) {
                    long currentTime = player.getEntityWorld().getTime();
                    if ((currentTime - lastUseTime) < FLIGHT_DURATION) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canUse(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) return true;
        long currentTime = world.getTime();
        return (currentTime - lastUseTime) >= COOLDOWN_DURATION && !isFlightActive(stack);
    }

    private void enableFlight(ServerPlayerEntity player, ItemStack stack) {
        player.getAbilities().allowFlying = true;
        player.getAbilities().flying = true;
        player.sendAbilitiesUpdate();
    }

    private void disableFlight(ServerPlayerEntity player, ItemStack stack) {
        if (!player.isCreative() && !player.isSpectator()) {
            if (!hasActiveFlightItem(player)) {
                player.getAbilities().allowFlying = false;
                player.getAbilities().flying = false;
                player.sendAbilitiesUpdate();
            }
            setFlightActive(stack, false);
            player.sendMessage(Text.literal("§eЭффект полёта закончился"), true);
        }
    }

    private long getRemainingCooldown(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) return 0;
        long currentTime = world.getTime();
        long elapsed = currentTime - lastUseTime;
        return Math.max(0, COOLDOWN_DURATION - elapsed);
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

    private void setFlightActive(ItemStack stack, boolean active) {
        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtComponent newComponent = nbtComponent.apply(nbt -> nbt.putBoolean(FLIGHT_ACTIVE_KEY, active));
        stack.set(DataComponentTypes.CUSTOM_DATA, newComponent);
    }

    private boolean isFlightActive(ItemStack stack) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent != null) {
            var nbt = nbtComponent.copyNbt();
            if (nbt.contains(FLIGHT_ACTIVE_KEY)) {
                return nbt.getBoolean(FLIGHT_ACTIVE_KEY).get();
            }
        }
        return false;
    }
}