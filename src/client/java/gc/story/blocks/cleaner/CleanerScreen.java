package gc.story.blocks.cleaner;

import gc.story.Story;
import gc.story.blocks.cleaner.CleanerScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CleanerScreen extends HandledScreen<CleanerScreenHandler> {

    private static final Identifier GUI_TEXTURE =
            Identifier.of(Story.MOD_ID, "textures/gui/gui.png");
    private static final Identifier ARROW_TEXTURE =
            Identifier.of(Story.MOD_ID, "textures/gui/arrow_progress.png");

    public CleanerScreen(CleanerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        renderProgressArrow(context, x, y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if (handler.isCrafting()) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, ARROW_TEXTURE, x + 73, y + 35, 0, 0,
                    handler.getScaledArrowProgress(), 16, 24, 16);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (handler.isCrafting()) {
            int x = (width - backgroundWidth) / 2;
            int y = (height - backgroundHeight) / 2;
            if (mouseX >= x + 73 && mouseX <= x + 73 + 24 &&
                    mouseY >= y + 35 && mouseY <= y + 35 + 16) {
                String timeText = getFormattedRemainingTime();
                if (!timeText.isEmpty()) {
                    context.drawTooltip(textRenderer, Text.literal("Осталось: " + timeText), mouseX, mouseY);
                }
            }
        }

        drawMouseoverTooltip(context, mouseX, mouseY);
    }
    private String getFormattedRemainingTime() {
        int progress = handler.getProgress();
        int maxProgress = handler.getMaxProgress();

        if (progress <= 0 || maxProgress <= 0) return "";
        long remainingTicks = handler.getRealRemainingTicks();

        if (remainingTicks <= 0) {
            remainingTicks = (long) maxProgress - (long) progress;
            int multiplier = handler.getProgressMultiplier();
            if (multiplier > 1) {
                remainingTicks *= multiplier;
            }
        }

        if (remainingTicks < 0) {
            long unsignedRemaining = ((long) maxProgress & 0xFFFFFFFFL) - ((long) progress & 0xFFFFFFFFL);
            int multiplier = handler.getProgressMultiplier();
            if (multiplier > 1) {
                unsignedRemaining *= multiplier;
            }
            remainingTicks = unsignedRemaining;

            if (remainingTicks < 0) return "";
        }

        long totalSeconds = remainingTicks / 20L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;

        if (minutes > 0) {
            return String.format("%d мин %02d сек", minutes, seconds);
        } else if (totalSeconds > 0) {
            return String.format("%d сек", seconds);
        } else {
            return "менее 1 сек";
        }
    }
}
