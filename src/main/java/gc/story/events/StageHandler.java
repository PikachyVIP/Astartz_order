package gc.story.events;

import gc.story.Story;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;


public class StageHandler {

    // Аттачмент для хранения времени перехода на следующую стадию (в миллисекундах с эпохи)
    public static final AttachmentType<Long> STAGE_TRANSITION_TIME = AttachmentRegistry.create(
            Identifier.of(Story.MOD_ID, "stage_transition_time"),
            builder -> builder.persistent(Codec.LONG).copyOnDeath()
    );

    private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L; // 24 часа в миллисекундах
    private static final long THREE_DAYS_MILLIS = 3 * ONE_DAY_MILLIS;
    private static final long FIVE_DAYS_MILLIS = 5 * ONE_DAY_MILLIS;
    private static final long TWENTY_SECONDS_MILLIS = 20 * 1000L;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(StageHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        // Проверяем каждые 20 тиков (1 секунду) для оптимизации
        if (server.getTicks() % 100 != 0) return;

        long currentTime = System.currentTimeMillis();

        // Проверяем всех игроков
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            checkAndUpdateStage(player, currentTime);
        }
    }

    private static void checkAndUpdateStage(ServerPlayerEntity player, long currentTime) {
        Long transitionTime = player.getAttached(STAGE_TRANSITION_TIME);

        if (transitionTime != null && currentTime >= transitionTime) {
            Integer currentStage = player.getAttached(InfectionHandler.INFECTED_ATTACHMENT);

            if (currentStage != null && currentStage == 0) {
                player.setAttached(InfectionHandler.INFECTED_ATTACHMENT, 1);
                scheduleStageTransition(player, 0);
            }else if (currentStage != null && currentStage == 1) {
                if(MutationStage2Handler.getCurrentDebuff(player) == null) MutationStage2Handler.applyDebuff(player, MutationStage2Handler.getRandomDebuff());
                player.setAttached(InfectionHandler.INFECTED_ATTACHMENT, 2);
                scheduleStageTransition(player, 1);
            }else if (currentStage != null && currentStage == 2) {
                player.setAttached(InfectionHandler.INFECTED_ATTACHMENT, 3);
                player.setAttached(STAGE_TRANSITION_TIME, null);
            }
        }
    }

    public static void scheduleStageTransition(ServerPlayerEntity player, int time) {
        long transitionTime;
        if (time == -1) {
            player.setAttached(STAGE_TRANSITION_TIME, null);
            if (player.getCommandTags().contains("story_debug")) {
                player.sendMessage(
                        net.minecraft.text.Text.literal("§7[DEBUG] Таймер перехода отключен"),
                        false
                );
            }
            return;
        }
        switch (time){
            case 0 -> transitionTime = System.currentTimeMillis() + ONE_DAY_MILLIS;
            case 1 -> transitionTime = System.currentTimeMillis() + THREE_DAYS_MILLIS;
            case 2 -> transitionTime = System.currentTimeMillis() + FIVE_DAYS_MILLIS;
            case 3 -> transitionTime = System.currentTimeMillis() + TWENTY_SECONDS_MILLIS;
            default -> transitionTime = System.currentTimeMillis() + ONE_DAY_MILLIS;
        }

        player.setAttached(STAGE_TRANSITION_TIME, transitionTime);

        // Сохраняем время в формате для отладки
        if (player.getCommandTags().contains("story_debug")) {
            player.sendMessage(
                    net.minecraft.text.Text.literal("§7[DEBUG] Переход на сл стадию запланирован на: " +
                            new java.util.Date(transitionTime).toString()),
                    false
            );
        }
    }

    // Метод для проверки оставшегося времени до перехода
    public static long getTimeUntilTransition(ServerPlayerEntity player) {
        Long transitionTime = player.getAttached(STAGE_TRANSITION_TIME);
        if (transitionTime == null) return -1;

        long remaining = transitionTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    // Метод для форматированного вывода оставшегося времени
    public static String getFormattedTimeUntilTransition(ServerPlayerEntity player) {
        long remaining = getTimeUntilTransition(player);
        if (remaining == -1) return "§7Не запланировано";
        if (remaining <= 0) return "§cВремя перехода наступило";

        long hours = remaining / (60 * 60 * 1000);
        long minutes = (remaining % (60 * 60 * 1000)) / (60 * 1000);
        long seconds = (remaining % (60 * 1000)) / 1000;

        return String.format("§e%02d:%02d:%02d", hours, minutes, seconds);
    }
}