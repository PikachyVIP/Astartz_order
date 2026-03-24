package gc.story.blocks.pc;

import gc.story.Story;
import gc.story.networking.TaskButtonClickPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CompScreen extends HandledScreen<CompScreenHandler> {

    private static final Identifier GUI_TEXTURE =
            Identifier.of(Story.MOD_ID, "textures/gui/guicomp.png");

    // Зона отображения заданий (координаты и размеры)
    private static final int TASKS_AREA_X = 9;
    private static final int TASKS_AREA_Y = 10;
    private static final int TASKS_AREA_WIDTH = 95;
    private static final int TASKS_AREA_HEIGHT = 65;

    // Параметры заданий
    private static final int MAX_TASKS = 3;
    private static final int TASK_HEIGHT = 22; // Высота одного задания

    public CompScreen(CompScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Рисуем основной фон GUI
        context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        // Рисуем область заданий
        drawTasksAreaBackground(context, x, y);


        // Рисуем кнопки (для отладки видимые)
        drawDebugButtons(context, x, y, mouseX, mouseY);
    }

    private void drawTasksAreaBackground(DrawContext context, int x, int y) {
        int areaX = x + TASKS_AREA_X;
        int areaY = y + TASKS_AREA_Y;

        // Рамка
        context.fill(areaX - 1, areaY - 1,
                areaX + TASKS_AREA_WIDTH + 1, areaY + TASKS_AREA_HEIGHT + 1,
                0xFF444444);
        // Фон
        context.fill(areaX, areaY,
                areaX + TASKS_AREA_WIDTH, areaY + TASKS_AREA_HEIGHT,
                0xAA000000);
    }


    /**
     * Рисует 3 видимые кнопки для отладки, каждая над своим заданием
     */
    private void drawDebugButtons(DrawContext context, int guiX, int guiY, int mouseX, int mouseY) {
        int buttonWidth = TASKS_AREA_WIDTH; // Ширина кнопки равна ширине задания
        int buttonHeight = TASK_HEIGHT;     // Высота кнопки равна высоте задания

        for (int i = 0; i < MAX_TASKS; i++) {
            int buttonX = guiX + TASKS_AREA_X;
            int buttonY = guiY + TASKS_AREA_Y + (i * TASK_HEIGHT);

            if (isMouseOverDebugButton(mouseX, mouseY, guiX, guiY, i)) {
                context.fill(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, 0x44c7c7c7);
            }
        }
    }

    /**
     * Проверяет, находится ли мышь над кнопкой отладки
     */
    private boolean isMouseOverDebugButton(int mouseX, int mouseY, int guiX, int guiY, int buttonIndex) {
        int buttonX = guiX + TASKS_AREA_X;
        int buttonY = guiY + TASKS_AREA_Y + (buttonIndex * TASK_HEIGHT);
        int buttonWidth = TASKS_AREA_WIDTH;
        int buttonHeight = TASK_HEIGHT;

        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    private boolean isMouseOverTask(int mouseX, int mouseY, int guiX, int guiY, int taskIndex) {
        int taskX = guiX + TASKS_AREA_X;
        int taskY = guiY + TASKS_AREA_Y + (taskIndex * TASK_HEIGHT);
        return mouseX >= taskX && mouseX <= taskX + TASKS_AREA_WIDTH &&
                mouseY >= taskY && mouseY <= taskY + TASK_HEIGHT;
    }
    private List<Text> wrapText(String text, int lineLength) {
        List<Text> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add(Text.literal("").withColor(0xFFFFAA));
            return lines;
        }

        for (int i = 0; i < text.length(); i += lineLength) {
            int end = Math.min(i + lineLength, text.length());
            String line = text.substring(i, end);
            lines.add(Text.literal(line).withColor(0xFFFFAA));
        }
        return lines;
    }


    private void drawTaskTooltip(DrawContext context, Task task, int mouseX, int mouseY) {
        java.util.List<Text> tooltip = new java.util.ArrayList<>();
        List<Text> wrappedText = wrapText(task.getText(), 45);
        for (Text line : wrappedText) {
            tooltip.add(line);
        }
        tooltip.add(Text.literal(""));
        tooltip.add(Text.literal("Требуется: " + task.getRequiredCount() + "x " +
                task.getRequiredItem().getName().getString()));
        tooltip.add(Text.literal("Прогресс: " + task.getCurrentProgress() + "/" + task.getRequiredCount()));
        tooltip.add(Text.literal("Награда: " + task.getRewardCount() + "x " +
                task.getRewardItem().getName().getString()));
        tooltip.add(Text.literal("Тип: " + (task.getType() == Task.TaskType.INFINITE ? "Бесконечное" : "Одноразовое")));

        if (task.isCompleted()) {
            tooltip.add(Text.literal(""));
            tooltip.add(Text.literal("✓ Задание выполнено!").withColor(0x55FF55));
            tooltip.add(Text.literal("Нажмите для получения награды").withColor(0xAAAAAA));
        }

        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        // Рисуем весь контент ДО super.render
        // Ваш GUI фон
        int guiX = (width - backgroundWidth) / 2;
        int guiY = (height - backgroundHeight) / 2;

        // Рисуем фон GUI
        context.fill(guiX, guiY, guiX + backgroundWidth, guiY + backgroundHeight, 0x88000000);
        drawMouseoverTooltip(context, mouseX, mouseY);
        drawTasks(context, guiX, guiY, mouseX, mouseY);

        // super.render должен быть в конце, чтобы не перекрывать ваш текст
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawTasks(DrawContext context, int guiX, int guiY, int mouseX, int mouseY) {
        List<Task> tasks = handler.getTasks();
        int taskIndex = 0;
        for (Task task : tasks) {
            if (taskIndex >= MAX_TASKS) break;

            int taskY = guiY + TASKS_AREA_Y + (taskIndex * TASK_HEIGHT);
            int taskX = guiX + TASKS_AREA_X;

            // Фон задания
            int bgColor = task.isCompleted() ? 0x8800AA00 : 0x88444444;
            context.fill(taskX, taskY, taskX + TASKS_AREA_WIDTH, taskY + TASK_HEIGHT, bgColor);

            // Разделительная линия
            if (taskIndex < MAX_TASKS - 1) {
                context.fill(taskX, taskY + TASK_HEIGHT - 1,
                        taskX + TASKS_AREA_WIDTH, taskY + TASK_HEIGHT,
                        0xFF666666);
            }

            // Иконка предмета
            ItemStack requiredItem = task.getRequiredItem();
            if (!requiredItem.isEmpty()) {
                context.drawItem(requiredItem, taskX + 2, taskY + 2);
            } else {
                context.fill(taskX + 2, taskY + 2, taskX + 20, taskY + 20, 0xFF8B5A2B);
            }

            // Название задания - убеждаемся что текст не пустой
            String taskTextString = task.getText();
            if (taskTextString != null && !taskTextString.isEmpty()) {
                Text taskText = Text.literal(taskTextString);
                if (taskTextString.length() > 10) {
                    taskTextString = taskTextString.substring(0, 11) + "...";
                }
                context.drawText(textRenderer, taskTextString, taskX + 24, taskY + 4,
                        task.isCompleted() ? 0xFF55FF55 : 0xFFFFFFFF, false);
            } else {
                // Отладочный текст если пусто
                context.drawText(textRenderer, "NO TEXT", taskX + 24, taskY + 4, 0xFFFFFFFF, false);
            }

            // Прогресс
            String progressText = String.format("%d/%d", task.getCurrentProgress(), task.getRequiredCount());
            context.drawText(textRenderer, progressText, taskX + 17, taskY + 12,
                    0xFFAAAAAA, false);

            // Галочка для выполненного
            if (task.isCompleted()) {
                context.drawText(textRenderer, "✓", taskX + TASKS_AREA_WIDTH - 12, taskY + 6,
                        0xFF55FF55, false);
            }

            // Тултип при наведении
            if (isMouseOverTask(mouseX, mouseY, guiX, guiY, taskIndex)) {
                drawTaskTooltip(context, task, mouseX, mouseY);
            }

            taskIndex++;
        }
    }

    // Добавь метод для отправки пакета
    private void sendTaskButtonClickToServer(int taskIndex) {
        ClientPlayNetworking.send(new TaskButtonClickPayload(taskIndex));
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int guiX = (width - backgroundWidth) / 2;
        int guiY = (height - backgroundHeight) / 2;

        int mouseX = (int)click.x();
        int mouseY = (int)click.y();

        // Проверяем нажатие на кнопки заданий
        for (int buttonIndex = 0; buttonIndex < MAX_TASKS; buttonIndex++) {
            if (isMouseOverDebugButton(mouseX, mouseY, guiX, guiY, buttonIndex)) {
                System.out.println("[CompScreen] Button " + buttonIndex + " clicked - sending packet to server");

                // Отправляем пакет на сервер
                sendTaskButtonClickToServer(buttonIndex);
                return true;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    private void claimReward(Task task) {
        if (task.isCompleted()) {
            handler.claimReward(task.getId());
        }
    }
}