package gc.story.events.debuff;

import gc.story.Story;
import gc.story.events.InfectionHandler;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;
import gc.story.events.MutationStage2Handler;

public class UnderwaterDebuff {

    // Аттачмент для отслеживания времени нахождения на суше
    public static final AttachmentType<Integer> TIME_ON_LAND = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "time_on_land"),
            builder -> builder.persistent(Codec.INT).copyOnDeath()
    );


    private static final int MAX_AIR = 300; // Максимальный запас воздуха (15 секунд)

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(UnderwaterDebuff::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        // Проверяем каждый тик для точного отсчета времени
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            // Проверяем, активен ли дебафф "Подводник"
            if (MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.UNDERWATER) && InfectionHandler.getCurrentStage(player) >= 2
            && !player.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
                applyUnderwaterEffects(player, server.getOverworld());
            } else {
                // Если дебафф не активен, сбрасываем таймер и восстанавливаем нормальное дыхание
                player.setAttached(TIME_ON_LAND, null);
                // Возвращаем нормальный механизм дыхания (не вмешиваемся)
            }
        }
    }

    private static void applyUnderwaterEffects(ServerPlayerEntity player, ServerWorld world) {
        if (player.isSubmergedInWater()) {
            int currentAir = player.getAir();

            if (currentAir < MAX_AIR) {
                player.setAir(Math.min(MAX_AIR, currentAir + 2));
            }

            player.setAttached(TIME_ON_LAND, 0);

        } else {
            Integer timeOnLand = player.getAttached(TIME_ON_LAND);
            if (timeOnLand == null) {
                timeOnLand = 0;
            }

            timeOnLand++;
            player.setAttached(TIME_ON_LAND, timeOnLand);
            int currentAir = player.getAir();

            if (currentAir > 0) {
                player.setAir(Math.max(0, currentAir - 5));
            }
        }

            // Если воздух закончился, начинаем наносить урон
            if (player.getAir() <= 0) {
                player.damage(world, player.getDamageSources().drown(), 1.0f);

                // Визуальный и звуковой эффект удушья
                if (world.random.nextInt(10) == 0) {
                    player.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_HURT_DROWN, 1.0f, 1.0f);
                }
            }

        }

    /**
     * Проверяет, задыхается ли игрок на суше
     */
    public static boolean isSuffocating(ServerPlayerEntity player) {
        if (!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.UNDERWATER)) {
            return false;
        }

        return player.getAir() <= 0;
    }

    /**
     * Получить оставшееся время до начала удушья (в секундах)
     */
    public static int getTimeUntilSuffocation(ServerPlayerEntity player) {
        if (!MutationStage2Handler.hasDebuff(player, MutationStage2Handler.Debuff.UNDERWATER)) {
            return -1;
        }

        int currentAir = player.getAir();
        if (currentAir <= 0) return 0;

        // Переводим тики воздуха в секунды (20 тиков = 1 секунда)
        return currentAir / 20;
    }

    /**
     * Получить процент оставшегося воздуха для отображения
     */
    public static int getAirPercentage(ServerPlayerEntity player) {
        return (player.getAir() * 100) / MAX_AIR;
    }
}