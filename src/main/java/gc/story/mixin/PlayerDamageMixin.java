package gc.story.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage2Handler;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public class PlayerDamageMixin {

    @WrapOperation(
            method = "applyDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F")
    )
    private float modifyDamageBeforeArmor(PlayerEntity player, DamageSource source, float amount, Operation<Float> original) {
        float modifiedAmount = amount;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (MutationStage2Handler.hasDebuff(serverPlayer, MutationStage2Handler.Debuff.CRYSTALLIZATION) && InfectionHandler.getCurrentStage(serverPlayer) == 2) {
                modifiedAmount = amount + 4.0F;
            }
        }

        return original.call(player, source, modifiedAmount);
    }
}