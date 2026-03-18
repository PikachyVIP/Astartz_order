package gc.story.events.debuff;

import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage2Handler;
import gc.story.events.MutationStage3BuffHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class LostDebuff {
    private static final Random RANDOM = new Random();
    private static final int TELEPORT_RADIUS = 8;


    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(LostDebuff::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.LOST)) {
                if(player.isSleeping()){
                    player.damage(server.getOverworld() ,player.getDamageSources().magic(), 1);
                }
                if (server.getTicks() % 300 == 0) applyPerlThrow(player);
            }
        }
    }

    private static void applyPerlThrow(ServerPlayerEntity player){
        int randomValue = -120 + RANDOM.nextInt(111);
        int randomValue2 = -120 + RANDOM.nextInt(400);
        ProjectileEntity.spawnWithVelocity(EnderPearlEntity::new, player.getEntityWorld(), new ItemStack(Items.ENDER_PEARL), player, randomValue, 1.5f, randomValue2);
    }
}