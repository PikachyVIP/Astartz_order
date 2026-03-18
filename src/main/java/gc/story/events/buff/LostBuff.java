package gc.story.events.buff;

import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class LostBuff {
    private static final int RADIUS = 10;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(LostBuff::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 10 != 0) return;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.LOST)) {
                applyPot(player);
            }
        }
    }

    public static void applyPot(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SPEED,
                640,
                1,
                false,
                false,
                true
        ));
        if(player.getEntityWorld().isNight()) {
            player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.INVISIBILITY,
                    640,
                    0,
                    false,
                    false,
                    true
            ));
        }
    }
}