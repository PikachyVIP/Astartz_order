package gc.story.events.debuff;

import gc.story.Story;
import gc.story.events.InfectionHandler;
import gc.story.items.NightLight;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import gc.story.events.MutationStage2Handler;
import net.minecraft.util.math.BlockPos;

public class BreakWingsDebuff {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(BreakWingsDebuff::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.BROKEN_WINGS) && InfectionHandler.getCurrentStage(player) >= 2) {
                if (server.getTicks() % 20 == 0){
                    ItemStack mainHandStack = player.getMainHandStack();
                    ItemStack offHandStack = player.getOffHandStack();
                    if (!(mainHandStack.getItem() instanceof NightLight) && !(offHandStack.getItem() instanceof NightLight)) {
                        damageShadow(player, server);
                    }
                }
            }
        }
    }


    private static void damageShadow(ServerPlayerEntity player, MinecraftServer server) {
        BlockPos blockPos = player.getBlockPos();

        int lightLevel = player.getEntityWorld().getLightLevel(blockPos);
        if (lightLevel <= 5) {
            player.damage(player.getEntityWorld(), player.getDamageSources().magic(), 8.0f);
            server.getOverworld().spawnParticles(
                    ParticleTypes.PORTAL,
                    player.getX(),
                    player.getY()+1,
                    player.getZ(),
                    25,
                    0.5,
                    0.5,
                    0.5,
                    0.1
            );
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.DARKNESS,
                    60,
                    1,
                    false,
                    false,
                    true
            ));

        }
    }
}