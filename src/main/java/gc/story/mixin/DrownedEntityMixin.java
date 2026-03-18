package gc.story.mixin;

import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage3BuffHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DrownedEntity.class)
public class DrownedEntityMixin {

    /**
     * @author Pikachy
     * @reason Модификация логики атаки утопленника - нейтралитет для игроков с баффом
     */
    @Overwrite
    public boolean canDrownedAttackTarget(@Nullable LivingEntity target) {
        if (target == null) {
            return false;
        }

        // Получаем текущего утопленника
        DrownedEntity drowned = (DrownedEntity) (Object) this;

        // Проверяем, является ли цель игроком с баффом
        if (target instanceof ServerPlayerEntity player) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.UNDERWATER) &&
                    InfectionHandler.getCurrentStage(player) == 3) {

                // Если игрок атаковал этого утопленника - разрешаем ответную атаку
                if (drowned.getAttacker() == player || drowned.getTarget() == player) {
                    return target.getEntityWorld().isDay() || target.isTouchingWater();
                }

                // Иначе - утопленник игнорирует игрока
                return false;
            }
        }

        // Стандартная логика для всех остальных
        return target.getEntityWorld().isDay() || target.isTouchingWater();
    }
}