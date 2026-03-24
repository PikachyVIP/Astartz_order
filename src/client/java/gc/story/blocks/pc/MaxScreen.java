package gc.story.blocks.pc;

import gc.story.Story;
import gc.story.blocks.max.MaxBlockEntity;
import gc.story.blocks.max.MaxScreenHandler;
import gc.story.networking.QuestionDeletePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MaxScreen extends HandledScreen<MaxScreenHandler> {

    private static final Identifier GUI_TEXTURE =
            Identifier.of(Story.MOD_ID, "textures/gui/guimax.png");

    // Зона вопроса
    private static final int QUESTION_AREA_X = 9;
    private static final int QUESTION_AREA_Y = 10;
    private static final int QUESTION_AREA_WIDTH = 159;
    private static final int QUESTION_AREA_HEIGHT = 65;

    // Зона ответа
    private static final int ANSWER_AREA_X = 9;
    private static final int ANSWER_AREA_Y = (140/2)+ 4;
    private static final int ANSWER_AREA_WIDTH = 159;
    private static final int ANSWER_AREA_HEIGHT = 67;

    private CustomButtonWidget createButton;
    private CustomButtonWidget deleteButton;
    private CustomButtonWidget answerButton;

    public MaxScreen(MaxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    }

    @Override
    protected void init() {
        super.init();

        int guiX = (width - backgroundWidth) / 2;
        int guiY = (height - backgroundHeight) / 2;

        this.createButton = new CustomButtonWidget(
                guiX + 7, guiY + 145, 39, 14,
                Text.literal("Создать"),
                button -> onCreatePressed(),
                0.8f
        );
        this.addDrawableChild(createButton);

        this.deleteButton = new CustomButtonWidget(
                guiX + 50, guiY + 145, 39, 14,
                Text.literal("Удалить"),
                button -> onDeletePressed(),
                0.8f
        );

        if (isCreativeMode()) {
            this.addDrawableChild(deleteButton);
        }

        this.answerButton = new CustomButtonWidget(
                guiX + 93, guiY + 145, 39, 14,
                Text.literal("Ответить"),
                button -> onAnswerPressed(),
                0.8f
        );

        if (isCreativeMode()) {
            this.addDrawableChild(answerButton);
        }
    }

    private static class CustomButtonWidget extends ButtonWidget {
        private final float textScale;

        public CustomButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, float textScale) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
            this.textScale = textScale;
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(),
                    isHovered() ? 0xFFAAAAAA : 0xFF888888);
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            String text = getMessage().getString();
            int textWidth = textRenderer.getWidth(text);

            context.getMatrices().pushMatrix();
            context.getMatrices().translate(
                    getX() + (getWidth() - textWidth * textScale) / 2,
                    getY() + (getHeight() - textRenderer.fontHeight * textScale) / 2,
                    context.getMatrices()
            );
            context.getMatrices().scale(textScale, textScale, context.getMatrices());
            context.drawText(textRenderer, text, 0, 0, 0xFFFFFFFF, false);
            context.getMatrices().popMatrix();
        }
    }

    private void onCreatePressed() {
        if (client != null && client.player != null) {
            MaxBlockEntity blockEntity = handler.getBlockEntity();
            if (isCreativeMode() || !blockEntity.hasQuestion()) {
                TextInputScreen inputScreen = new TextInputScreen(TextInputScreen.Type.QUESTION, blockEntity);
                inputScreen.setOnCloseCallback(() -> {
                    if (client != null) {
                        client.setScreen(this);
                    }
                });
                client.setScreen(inputScreen);
            } else {
                client.player.sendMessage(Text.literal("§cВопрос уже существует! Сначала удалите текущий."), false);
            }
        }
    }

    private void onDeletePressed() {
        if (client != null && client.player != null) {
            if (isCreativeMode()) {
                // Отправляем пакет на сервер
                ClientPlayNetworking.send(new QuestionDeletePayload());
                client.player.sendMessage(Text.literal("§aЗапрос на удаление отправлен!"), false);
            }
        }
    }

    private void onAnswerPressed() {
        if (client != null && client.player != null) {
            if (isCreativeMode()) {
                MaxBlockEntity blockEntity = handler.getBlockEntity();
                if (blockEntity.hasQuestion()) {
                    TextInputScreen inputScreen = new TextInputScreen(TextInputScreen.Type.ANSWER, blockEntity);
                    inputScreen.setOnCloseCallback(() -> {
                        if (client != null) {
                            client.setScreen(this);
                        }
                    });
                    client.setScreen(inputScreen);
                } else {
                    client.player.sendMessage(Text.literal("§cНет активного вопроса для ответа!"), false);
                }
            }
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        int guiX = (width - backgroundWidth) / 2;
        int guiY = (height - backgroundHeight) / 2;

        drawQuestionAndAnswer(context, guiX, guiY);

        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private void drawQuestionAndAnswer(DrawContext context, int guiX, int guiY) {
        MaxBlockEntity blockEntity = handler.getBlockEntity();

        int questionX = guiX + QUESTION_AREA_X;
        int questionY = guiY + QUESTION_AREA_Y;
        int answerX = guiX + ANSWER_AREA_X;
        int answerY = guiY + ANSWER_AREA_Y;

        // Рамка вопроса
        drawAreaBorder(context, questionX, questionY, QUESTION_AREA_WIDTH, QUESTION_AREA_HEIGHT);
        // Рамка ответа
        drawAreaBorder(context, answerX, answerY, ANSWER_AREA_WIDTH, ANSWER_AREA_HEIGHT);

        if (blockEntity != null && blockEntity.hasQuestion()) {
            String question = blockEntity.getQuestion();
            String answer = blockEntity.getAnswer();

            drawWrappedText(context, question, questionX + 1, questionY + 1, 20, 0xFFFFFF00);

            if (!answer.isEmpty()) {
                drawWrappedText(context, answer, answerX + 1, answerY + 1, 20, 0xFF00FF00);
            }
        }
    }

    private void drawAreaBorder(DrawContext context, int x, int y, int width, int height) {
        int borderColor = 0xFF444444;
        context.fill(x, y, x + width, y + 1, borderColor);
        context.fill(x, y + height - 1, x + width, y + height, borderColor);
        context.fill(x, y, x + 1, y + height, borderColor);
        context.fill(x + width - 1, y, x + width, y + height, borderColor);
    }

    private void drawWrappedText(DrawContext context, String text, int x, int y, int maxWidth, int color) {
        if (text == null || text.isEmpty()) {
            return;
        }

        int lineY = y;
        int currentIndex = 0;
        int textLength = text.length();

        while (currentIndex < textLength) {
            int endIndex = Math.min(currentIndex + 25, textLength);
            String line = text.substring(currentIndex, endIndex);
            context.drawText(textRenderer, line, x, lineY, color, false);
            lineY += textRenderer.fontHeight;
            currentIndex = endIndex;
        }
    }

    private boolean isCreativeMode() {
        return client != null && client.player != null && client.player.isCreative();
    }
}