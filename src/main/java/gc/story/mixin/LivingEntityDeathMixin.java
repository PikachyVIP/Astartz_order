package gc.story.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import gc.story.items.Devourer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityDeathMixin {

    @WrapOperation(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;drop(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;)V"
            )
    )
    private void onDeathWrapDrop(LivingEntity entity, ServerWorld world, DamageSource damageSource, Operation<Void> original) {
        original.call(entity, world, damageSource);

        Entity attacker = damageSource.getAttacker();

        if (attacker instanceof ServerPlayerEntity player) {
            for (ItemStack stack : player.getInventory()) {
                if (stack.getItem() instanceof Devourer devourer) {
                    devourer.onKill(player, stack, entity);
                    break;
                }
            }
        }
    }
}