package gc.story.events;

import gc.story.inits.ModItems;
import gc.story.items.Scalpel;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class ScalpelHandler {

    private static final Random RANDOM = new Random();

    public static void register() {
        AttackEntityCallback.EVENT.register(ScalpelHandler::onAttackEntity);
    }

    private static ActionResult onAttackEntity(PlayerEntity attacker, World world, Hand hand, Entity target, @Nullable EntityHitResult hitResult) {
        // Проверка на сервере
        if (world.isClient()) {
            return ActionResult.PASS;
        }

        // Проверка что цель - игрок
        if (!(target instanceof ServerPlayerEntity targetPlayer)) {
            return ActionResult.PASS;
        }

        // Проверка что атакующий - игрок
        if (!(attacker instanceof ServerPlayerEntity attackerPlayer)) {
            return ActionResult.PASS;
        }

        // Получаем предмет в руке, которой бьют
        ItemStack itemInHand = attacker.getStackInHand(hand);

        // Проверяем, что в руке скальпель
        if (!(itemInHand.getItem() instanceof Scalpel)) {
            return ActionResult.PASS;
        }

        // Получаем стадию инфекции цели
        int targetInfection = InfectionHandler.getCurrentStage(targetPlayer);

        System.out.println("[ScalpelHandler] Target infection stage: " + targetInfection);
        System.out.println("[ScalpelHandler] Using hand: " + hand);
        System.out.println("[ScalpelHandler] Item in hand: " + itemInHand.getItem().getClass().getSimpleName());

        // Проверяем, заражен ли игрок
        if (targetInfection >= 0) {
            System.out.println("[ScalpelHandler] Player is infected, performing surgery...");

            // Уменьшаем прочность скальпеля (тратим предмет)
            itemInHand.decrement(1);

            // Понижаем стадию инфекции
            InfectionHandler.setInfected(targetPlayer, targetInfection - 1);

            // Добавляем эффекты
            targetPlayer.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NAUSEA,
                    6000,
                    0,
                    false,
                    false,
                    true
            ));
            targetPlayer.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.WEAKNESS,
                    6000,
                    1,
                    false,
                    false,
                    true
            ));
            targetPlayer.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS,
                    6000,
                    2,
                    false,
                    false,
                    true
            ));

            // Спавним ядро
            ItemStack core = new ItemStack(ModItems.CRYSTAL_CORE);
            ItemEntity itemEntity = new ItemEntity(
                    world,
                    targetPlayer.getX(),
                    targetPlayer.getY(),
                    targetPlayer.getZ(),
                    core
            );

            itemEntity.setVelocity(
                    RANDOM.nextDouble() * 0.2 - 0.1,
                    0.2,
                    RANDOM.nextDouble() * 0.2 - 0.1
            );
            itemEntity.setPickupDelay(3);
            world.spawnEntity(itemEntity);

            // Наносим урон
            targetPlayer.damage((ServerWorld) world, targetPlayer.getDamageSources().generic(), 9);

            System.out.println("[ScalpelHandler] Surgery completed successfully");

            // Отменяем обычный урон, чтобы не было двойного урона
            return ActionResult.FAIL;
        } else {
            System.out.println("[ScalpelHandler] Player is not infected, surgery cancelled");
        }

        return ActionResult.PASS;
    }
}