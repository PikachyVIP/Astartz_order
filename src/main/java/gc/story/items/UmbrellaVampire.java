package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UmbrellaVampire extends Item {
    private static final String LAST_USE_TIME_KEY = "last_use_time";
    private static final String FLIGHT_ACTIVE_KEY = "flight_active";

    private static final int FLIGHT_DURATION = 6000; // 5 минут в тиках
    private static final int COOLDOWN_DURATION = 12000; // 10 минут в тиках

    public UmbrellaVampire(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);


        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.SUN_BURN)) return ActionResult.FAIL;
            if (isFlightActive(stack)) {
                player.sendMessage(Text.literal("§cПолёт уже активен!"), true);
                return ActionResult.FAIL;
            }

            // Проверяем перезарядку
            if (canUse(stack, world)) {
                // Активируем полёт
                enableFlight(player, stack);

                // Сохраняем время использования
                long currentTime = world.getTime();
                setLastUseTime(stack, currentTime);
                setFlightActive(stack, true);

                // Ставим предмет на перезарядку
                player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_DURATION);

                player.sendMessage(Text.literal("§aПолёт активирован на 5 минут!"), true);

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

        // Проверяем только если это игрок
        if (entity instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.SUN_BURN)) return;
            // Проверяем, активен ли полёт для этого предмета
            if (isFlightActive(stack)) {
                Long lastUseTime = getLastUseTime(stack);
                if (lastUseTime != null) {
                    long currentTime = world.getTime();

                    // ВСЕГДА проверяем, не истекло ли время полёта
                    if ((currentTime - lastUseTime) >= FLIGHT_DURATION) {
                        // Время истекло - отключаем полёт
                        disableFlight(player, stack);
                        return; // Выходим, чтобы не выполнять другие проверки
                    }

                    // Если время не истекло, убеждаемся что полёт включён
                    if (!player.getAbilities().allowFlying && !player.isCreative() && !player.isSpectator()) {
                        player.getAbilities().allowFlying = true;
                        player.getAbilities().flying = true;
                        player.sendAbilitiesUpdate();
                    }
                }
            } else {
                // Если полёт не активен для этого предмета, но игрок летает (кроме креатива)
                if (!player.isCreative() && !player.isSpectator() && player.getAbilities().flying) {
                    // Проверяем, нет ли другого активного предмета с полётом
                    if (!hasActiveFlightItem(player)) {
                        player.getAbilities().allowFlying = false;
                        player.getAbilities().flying = false;
                        player.sendAbilitiesUpdate();
                    }
                }
            }
        }
    }

    // Метод для проверки наличия активного предмета с полётом в инвентаре
    private boolean hasActiveFlightItem(ServerPlayerEntity player) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.getItem() instanceof UmbrellaVampire && isFlightActive(stack)) {
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
        if (lastUseTime == null) {
            return true;
        }

        long currentTime = world.getTime();
        // Предмет можно использовать, если прошло достаточно времени И полёт не активен
        return (currentTime - lastUseTime) >= COOLDOWN_DURATION && !isFlightActive(stack);
    }

    private void enableFlight(ServerPlayerEntity player, ItemStack stack) {
        // Включаем полёт
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
        if (lastUseTime == null) {
            return 0;
        }

        long currentTime = world.getTime();
        long elapsed = currentTime - lastUseTime;

        return Math.max(0, COOLDOWN_DURATION - elapsed);
    }

    // Методы для работы с компонентами
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