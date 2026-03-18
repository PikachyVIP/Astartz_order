package gc.story.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gc.story.events.MutationStage3BuffHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeletonEntity.class)
public class SkeletonEntityMixin {

    @WrapOperation(
            method = "initGoals",
            at = @At(value = "NEW", target = "(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;Z)Lnet/minecraft/entity/ai/goal/ActiveTargetGoal;")
    )
    private ActiveTargetGoal<?> modifyTargetGoal(MobEntity mob, Class<?> targetClass, boolean checkVisibility, Operation<ActiveTargetGoal<?>> original) {
        if (targetClass == PlayerEntity.class) {
            TargetPredicate.EntityPredicate predicate = (target, world) -> {
                if (target instanceof ServerPlayerEntity player) {
                    return !MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.SUN_BURN);
                }
                return true;
            };

            return new ActiveTargetGoal<>(
                    mob,
                    PlayerEntity.class,
                    10,
                    checkVisibility,
                    false,
                    predicate
            );
        }
        return original.call(mob, targetClass, checkVisibility);
    }
}