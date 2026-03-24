package gc.story.blocks.pc;

import gc.story.blocks.max.MaxBlockEntity;
import gc.story.networking.AnswerCreatePayload;
import gc.story.networking.QuestionCreatePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class TextInputScreen extends Screen {
    private final Type inputType;
    private final MaxBlockEntity blockEntity;
    private TextFieldWidget textField;
    private Runnable onCloseCallback;

    public enum Type {
        QUESTION,
        ANSWER
    }

    public TextInputScreen(Type type, MaxBlockEntity blockEntity) {
        super(Text.literal(type == Type.QUESTION ? "Создать вопрос" : "Ввести ответ"));
        this.inputType = type;
        this.blockEntity = blockEntity;
    }

    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.textField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY - 20,
                200,
                20,
                Text.literal("")
        );

        if (inputType == Type.QUESTION) {
            this.textField.setMaxLength(256);
            this.textField.setPlaceholder(Text.literal("Введите вопрос..."));
        } else {
            this.textField.setMaxLength(256);
            this.textField.setPlaceholder(Text.literal("Введите ответ..."));
        }

        ButtonWidget confirmButton = ButtonWidget.builder(
                Text.literal("Подтвердить"),
                button -> onConfirm()
        ).dimensions(centerX - 105, centerY + 20, 100, 20).build();

        ButtonWidget cancelButton = ButtonWidget.builder(
                Text.literal("Отмена"),
                button -> close()
        ).dimensions(centerX + 5, centerY + 20, 100, 20).build();

        this.addDrawableChild(textField);
        this.addDrawableChild(confirmButton);
        this.addDrawableChild(cancelButton);

        this.setInitialFocus(textField);
    }

    private void onConfirm() {
        String text = textField.getText();
        if (!text.isEmpty()) {
            if (inputType == Type.QUESTION) {
                // Отправляем пакет на сервер
                ClientPlayNetworking.send(new QuestionCreatePayload(text));
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                            Text.literal("§aВопрос отправлен на сервер!"), false
                    );
                }
            } else {
                // Отправляем пакет на сервер
                ClientPlayNetworking.send(new AnswerCreatePayload(text));
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                            Text.literal("§aОтвет отправлен на сервер!"), false
                    );
                }
            }
        }
        close();
    }

    @Override
    public void close() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        } else {
            super.close();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderDarkening(context);
        super.render(context, mouseX, mouseY, delta);

        String title = inputType == Type.QUESTION ? "Создание вопроса:" : "Ввод ответа:";
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                title,
                this.width / 2,
                this.height / 2 - 50,
                0xFFFFFF
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}