package gc.story.networking;

import gc.story.blocks.pc.CompScreenHandler;
import gc.story.events.buff.BrokenWingsBuff;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class StoryNetwork {

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ActivateAbilityPayload.ID, ActivateAbilityPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                ActivateAbilityPayload.ID,
                (payload, context) -> {
                    context.server().execute(() -> {
                        ServerPlayerEntity player = context.player();
                        MinecraftServer server = context.server();

                      //  BrokenWingsBuff.activateAbility(player, server.getOverworld());
                    });
                }
        );

        PayloadTypeRegistry.playC2S().register(TaskButtonClickPayload.ID, TaskButtonClickPayload.CODEC);

        // Регистрируем обработчик на сервере
        ServerPlayNetworking.registerGlobalReceiver(TaskButtonClickPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                int taskIndex = payload.taskIndex();

                // Проверяем, что открыт нужный экран
                if (player.currentScreenHandler instanceof CompScreenHandler handler) {
                    handler.handleTaskButtonClick(taskIndex);
                }
            });
        });


        PayloadTypeRegistry.playC2S().register(QuestionCreatePayload.ID, QuestionCreatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AnswerCreatePayload.ID, AnswerCreatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(QuestionDeletePayload.ID, QuestionDeletePayload.CODEC);

        // Обработчик создания вопроса
        ServerPlayNetworking.registerGlobalReceiver(QuestionCreatePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                if (context.player() != null) {
                    // Получаем блок, который открыт у игрока
                    if (context.player().currentScreenHandler instanceof gc.story.blocks.max.MaxScreenHandler handler) {
                        gc.story.blocks.max.MaxBlockEntity blockEntity = handler.getBlockEntity();
                        blockEntity.createQuestion(payload.question(), context.player());
                    }
                }
            });
        });

        // Обработчик создания ответа
        ServerPlayNetworking.registerGlobalReceiver(AnswerCreatePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                if (context.player() != null) {
                    if (context.player().currentScreenHandler instanceof gc.story.blocks.max.MaxScreenHandler handler) {
                        gc.story.blocks.max.MaxBlockEntity blockEntity = handler.getBlockEntity();
                        blockEntity.setAnswer(payload.answer(), context.player());
                    }
                }
            });
        });

        // Обработчик удаления вопроса
        ServerPlayNetworking.registerGlobalReceiver(QuestionDeletePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                if (context.player() != null) {
                    if (context.player().currentScreenHandler instanceof gc.story.blocks.max.MaxScreenHandler handler) {
                        gc.story.blocks.max.MaxBlockEntity blockEntity = handler.getBlockEntity();
                        blockEntity.deleteQuestion();
                    }
                }
            });
        });
    }
}