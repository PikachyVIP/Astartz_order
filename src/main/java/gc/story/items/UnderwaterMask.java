package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.world.World;

import java.util.Optional;

public class UnderwaterMask extends Item {
    private static final String LAST_USE_TIME_KEY = "last_use_time";
    private static final int COOLDOWN_TICKS = 600;

    public UnderwaterMask(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.UNDERWATER)) return ActionResult.FAIL;
            if (canUse(stack, world)) {
                throwTrident(player);
                setLastUseTime(stack, world.getTime());
                player.getItemCooldownManager().set(this.getDefaultStack(), COOLDOWN_TICKS);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
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

    private void throwTrident(ServerPlayerEntity player) {
        World world = player.getEntityWorld();

        ItemStack tridentStack = new ItemStack(Items.TRIDENT);

        RegistryEntry<Enchantment> channellingEnchantment = world.getRegistryManager()
                .getOrThrow(RegistryKeys.ENCHANTMENT)
                .getOrThrow(Enchantments.CHANNELING);
        tridentStack.addEnchantment(channellingEnchantment, 1);

        TridentEntity tridentEntity = new TridentEntity(world, player, tridentStack);

        tridentEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, 2.5f, 1.0f);

        tridentEntity.setOwner(player);
        tridentEntity.pickupType = TridentEntity.PickupPermission.DISALLOWED;

        world.spawnEntity(tridentEntity);
    }

    private boolean canUse(ItemStack stack, World world) {
        Long lastUseTime = getLastUseTime(stack);
        if (lastUseTime == null) return true;
        long currentTime = world.getTime();
        return (currentTime - lastUseTime) >= COOLDOWN_TICKS;
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
}