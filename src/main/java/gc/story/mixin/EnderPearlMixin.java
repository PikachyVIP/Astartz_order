package gc.story.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import gc.story.events.MutationStage2Handler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderPearlEntity.class)
public class EnderPearlMixin {

    /**
     * Перехватываем момент телепортации игрока и добавляем эффект иссушения
     */
    @WrapOperation(
            method = "onCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;teleportTo(Lnet/minecraft/world/TeleportTarget;)Lnet/minecraft/server/network/ServerPlayerEntity;")
    )
    private ServerPlayerEntity onPlayerTeleport(ServerPlayerEntity player, TeleportTarget teleportTarget, Operation<ServerPlayerEntity> original) {
        ServerPlayerEntity teleportedPlayer = original.call(player, teleportTarget);

        if (teleportedPlayer != null && MutationStage2Handler.hasDebuff(teleportedPlayer, MutationStage2Handler.Debuff.LOST)) {
            teleportedPlayer.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.WITHER,
                    100,
                    0,
                    false,
                    true,
                    true
            ));
        }

        return teleportedPlayer;
    }
}