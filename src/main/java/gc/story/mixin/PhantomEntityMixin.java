package gc.story.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gc.story.events.MutationStage2Handler;
import gc.story.events.MutationStage3BuffHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$FindTargetGoal")
public class PhantomEntityMixin {

    @WrapOperation(
            method = "canStart",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getPlayers(Lnet/minecraft/entity/ai/TargetPredicate;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/Box;)Ljava/util/List;")
    )
    private List<PlayerEntity> filterPlayers(ServerWorld world, TargetPredicate predicate, LivingEntity entity, Box box, Operation<List<PlayerEntity>> original) {
        List<PlayerEntity> players = original.call(world, predicate, entity, box);

        // Удаляем игроков с дебаффом из списка потенциальных целей
        players.removeIf(player -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                return MutationStage3BuffHandler.hasBuff(serverPlayer, MutationStage3BuffHandler.Buff.LOST);
            }
            return false;
        });

        return players;
    }
}