package gc.story.events.debuff;

import gc.story.events.InfectionHandler;
import gc.story.events.MutationStage2Handler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

public class FireDebuff {

    private static final int DAMAGE_INTERVAL = 60;
    private static final float DAMAGE_AMOUNT = 8.0f;
    private static final int WEAKNESS_DURATION = 100;
    private static final int WEAKNESS_AMPLIFIER = 0;

    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FireDebuff::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        tickCounter++;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

            if (MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.FIRE)
                    && InfectionHandler.getCurrentStage(player) >= 2) {

                boolean inWater = player.isTouchingWater() || player.isSubmergedInWater();
                boolean inRain = isRainingAt(player, server.getOverworld());
                if(!InfectionHandler.hasUmbrella(player)) {
                    if (player.getEntityWorld().getRegistryKey() == World.OVERWORLD) {
                        if (inWater || inRain) {
                            player.addStatusEffect(new StatusEffectInstance(
                                    StatusEffects.WEAKNESS,
                                    WEAKNESS_DURATION,
                                    WEAKNESS_AMPLIFIER,
                                    false,
                                    false,
                                    true
                            ));

                            if (tickCounter % DAMAGE_INTERVAL == 0) {
                                player.damage(server.getOverworld(), player.getDamageSources().magic(), DAMAGE_AMOUNT);

                            }
                        }
                    }
                }
            }
        }
        if (tickCounter >= DAMAGE_INTERVAL) {
            tickCounter = 0;
        }
    }

    private static boolean isRainingAt(ServerPlayerEntity player, ServerWorld world) {
        BlockPos pos = player.getBlockPos();
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);

        return world.isRaining()
                && world.isSkyVisible(pos)
                && biomeEntry.value().getPrecipitation(pos, world.getSeaLevel()) == Biome.Precipitation.RAIN;
    }

}