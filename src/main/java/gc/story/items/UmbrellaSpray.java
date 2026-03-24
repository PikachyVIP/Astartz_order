package gc.story.items;

import gc.story.events.MutationStage2Handler;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class UmbrellaSpray extends TridentItem {
    public UmbrellaSpray(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);

        if (slot != null && (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND)) {

            if (entity instanceof ServerPlayerEntity player) {
                if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.FIRE)) return;
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.SLOW_FALLING,
                        40,
                        0,
                        false,
                        false,
                        false
                ));
            }
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // Проверяем, что это серверный мир
        if (world.isClient()) {
            return false;
        }

        if (!(user instanceof ServerPlayerEntity playerEntity)) {
            return false;
        }

        // Проверяем наличие дебаффа
        if (!MutationStage2Handler.hasDebuff(playerEntity, MutationStage2Handler.Debuff.FIRE)) {
            return false;
        }

        int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
        if (i < 10) {
            return false;
        }

        float f = 4;
        if (!playerEntity.isTouchingWaterOrRain()) {
            return false;
        }

        if (stack.willBreakNextUse()) {
            return false;
        }

        // Получаем звук
        RegistryEntry<SoundEvent> registryEntry = EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.TRIDENT_SOUND)
                .orElse(SoundEvents.ITEM_TRIDENT_THROW);

        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));

        // Рассчитываем направление рывка
        float g = playerEntity.getYaw();
        float h = playerEntity.getPitch();
        float j = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
        float k = -MathHelper.sin(h * (float) (Math.PI / 180.0));
        float l = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
        float m = MathHelper.sqrt(j * j + k * k + l * l);
        j *= f / m;
        k *= f / m;
        l *= f / m;

        // Применяем скорость
        playerEntity.addVelocity(j, k, l);
        playerEntity.velocityModified = true; // Важно: помечаем, что скорость изменена

        // Активируем эффект рывка
        playerEntity.useRiptide(20, 8.0F, stack);

        if (playerEntity.isOnGround()) {
            playerEntity.move(MovementType.SELF, new Vec3d(0.0, 1.1999999F, 0.0));
        }

        // Воспроизводим звук
        world.playSoundFromEntity(null, playerEntity, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);

        return true;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player) {
            if(!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.FIRE)) return ActionResult.FAIL;
        }
        if (itemStack.willBreakNextUse()) {
            return ActionResult.FAIL;
        } else if (!user.isTouchingWaterOrRain()) {
            return ActionResult.FAIL;
        } else {
            user.setCurrentHand(hand);
            return ActionResult.CONSUME;
        }
    }
}
