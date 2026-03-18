package gc.story.events;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class HitInfHandler {

    private static final Random RANDOM = new Random();

    public static void register() {
        AttackEntityCallback.EVENT.register(HitInfHandler::onAttackEntity);
    }

    private static ActionResult onAttackEntity(PlayerEntity attacker, World world, Hand hand, Entity target, @Nullable EntityHitResult hitResult) {
        if (world.isClient() || !(target instanceof ServerPlayerEntity targetPlayer)) {
            return ActionResult.PASS;
        }

        if (!(attacker instanceof ServerPlayerEntity attackerPlayer)) {
            return ActionResult.PASS;
        }

        int attackerInfection = InfectionHandler.getCurrentStage(attackerPlayer);
        int targetInfection = InfectionHandler.getCurrentStage(targetPlayer);

        // Проверяем, что атакующий НЕ в полном комплекте брони заражения
        if (!InfectionHandler.hasFullCustomArmor(attackerPlayer)) {
            if (!InfectionHandler.hasFullCustomArmor(targetPlayer)) {
                if (attackerInfection >= 0 && targetInfection < 0) {
                    if (RANDOM.nextInt(100) < 25) {
                        InfectionHandler.setInfected(targetPlayer, 0);
                        StageHandler.scheduleStageTransition(targetPlayer, 0);
                    }
                }
            }
        }

        return ActionResult.PASS;
    }
}