package gc.story.events.buff;

import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class UnderwaterBuff {


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(UnderwaterBuff::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage3BuffHandler.hasBuff(player, MutationStage3BuffHandler.Buff.UNDERWATER)) {
                applyUnderwaterEffects(player, server.getOverworld());
                //makeMobsNeutral(player, server.getOverworld());
            }
        }
    }

    private static void applyUnderwaterEffects(ServerPlayerEntity player, World world) {
        BlockPos playerPos = player.getBlockPos();

        if (world.getBlockState(playerPos).getFluidState().isEmpty() &&
                world.getBlockState(playerPos.up()).getFluidState().isEmpty()) {
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.DOLPHINS_GRACE,
                100,
                1,
                false,
                false,
                false
        ));
        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.HASTE,
                100,
                0,
                false,
                false,
                false
        ));
    }

    private static void makeMobsNeutral(ServerPlayerEntity player, World world) {
        //if (world.getServer().getTicks() % 5 != 0) return;
        Box box = new Box(player.getBlockPos()).expand(30);

        for (Entity entity : world.getEntitiesByClass(Entity.class, box, e ->
                e instanceof DrownedEntity ||
                        e instanceof GuardianEntity ||
                        e instanceof ElderGuardianEntity)) {

            if (entity instanceof DrownedEntity drowned) {
                drowned.setTarget(null);
            } else if (entity instanceof GuardianEntity guardian) {
                guardian.setTarget(null);
            } else if (entity instanceof ElderGuardianEntity elderGuardian) {
                elderGuardian.setTarget(null);
            }
        }
    }




    public static void applyBuff(ServerPlayerEntity player, World world) {
        applyUnderwaterEffects(player, world);
      //  makeMobsNeutral(player, world);
    }

    public static void removeBuff(ServerPlayerEntity player) {
        player.removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}