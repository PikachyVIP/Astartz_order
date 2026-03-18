package gc.story.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gc.story.events.MutationStage2Handler;
import gc.story.events.MutationStage3BuffHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.entity.mob.BlazeEntity$ShootFireballGoal")
public class BlazeEntityMixin {

    @WrapOperation(
            method = "canStart",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/BlazeEntity;getTarget()Lnet/minecraft/entity/LivingEntity;")
    )
    private LivingEntity modifyTarget(BlazeEntity blaze, Operation<LivingEntity> original) {
        LivingEntity target = original.call(blaze);

        if (target instanceof ServerPlayerEntity player) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.FIRE)) {
                return null;
            }
        }

        return target;
    }
}