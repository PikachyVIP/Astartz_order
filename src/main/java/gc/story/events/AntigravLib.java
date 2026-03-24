package gc.story.events;

import gc.story.events.buff.FireBuff;
import gc.story.items.Liberation;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class AntigravLib {
    public static void register() {
        AttackEntityCallback.EVENT.register(AntigravLib::onAttackEntity);
    }

    private static ActionResult onAttackEntity(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (world.isClient()) return ActionResult.PASS;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            if (hasProtectiveItem(serverPlayer)) {
                if (entity instanceof LivingEntity livingTarget) {
                    livingTarget.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.LEVITATION,
                            200,
                            0,
                            false,
                            false,
                            true
                    ));
                }
            }
        }

        return ActionResult.PASS;
    }

    private static boolean hasProtectiveItem(ServerPlayerEntity player) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.getItem() instanceof Liberation) {
                return true;
            }
        }
        return false;
    }
}
