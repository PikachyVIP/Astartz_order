package gc.story.events;

import gc.story.items.Liberation;
import gc.story.items.UnderwaterMask;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class UnderWaterMaskHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(UnderWaterMaskHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0) return;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (hasProtectiveItem(player)) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.WATER_BREATHING,
                        200,
                        0,
                        false,
                        false,
                        true
                ));
            }
        }
    }
    private static boolean hasProtectiveItem(ServerPlayerEntity player) {
        for (ItemStack stack : player.getInventory().getMainStacks()) {
            if (stack.getItem() instanceof UnderwaterMask) {
                return true;
            }
        }
        return false;
    }

}
