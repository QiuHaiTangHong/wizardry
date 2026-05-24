package top.begonia.wizardry.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public final class DrawingUtils {

    public static void drawScaledStringToWidth(
            GuiGraphicsExtractor guiGraphics, @NonNull Font font, Component text,
            float x, float y, float scale, int colour,
            float width, boolean centre, boolean alignR
    ) {
        float textWidth = font.width(text) * scale;
        float textHeight = font.lineHeight * scale;
        if (textWidth > width) {
            scale *= width / textWidth;
        } else if (alignR) {
            x += width - textWidth;
        }
        if (centre) {
            y += (font.lineHeight - textHeight) / 2.0F;
        }
        drawScaledTranslucentString(guiGraphics, font, text, x, y, scale, colour);
    }

    public static void drawScaledTranslucentString(
            @NonNull GuiGraphicsExtractor guiGraphics, Font font, Component text,
            float x, float y, float scale, int colour
    ) {
        Matrix3x2fStack matrixStack = guiGraphics.pose();
        matrixStack.pushMatrix();
        matrixStack.scale(scale, scale);
        float invScaleX = x / scale;
        float invScaleY = y / scale;
        guiGraphics.text(font, text, Mth.floor(invScaleX), Mth.floor(invScaleY), colour, true);
        matrixStack.popMatrix();
    }

    public static void drawGlitchRect(
            GuiGraphicsExtractor guiGraphics, Identifier texture, RandomSource random,
            int x, int y, int u, int v, int width, int height,
            int textureWidth, int textureHeight, boolean flipX, boolean flipY
    ) {
        for (int i = 0; i < height; i++) {
            int currentI = flipY ? (height - i - 1) : i;
            int offset = random.nextInt(4) == 0 ? (random.nextInt(6) - 3) : 0;
            drawTexturedFlippedRect(
                    guiGraphics, texture,
                    x + offset, y + currentI,
                    u, v + currentI,
                    width, 1,
                    textureWidth, textureHeight,
                    flipX, flipY
            );
        }
    }

    public static int makeTranslucent(int colour, float opacity) {
        return colour + ((int) (opacity * 0xff) << 24);
    }

    public static void drawTexturedRect(
            GuiGraphicsExtractor guiGraphicsExtractor,
            Identifier texture,
            int x, int y,
            int u, int v,
            int width, int height,
            int textureWidth, int textureHeight
    ) {
        DrawingUtils.drawTexturedFlippedRect(
                guiGraphicsExtractor, texture,
                x, y,
                u, v,
                width, height,
                textureWidth, textureHeight,
                false, false
        );
    }

    public static void drawTexturedFlippedRect(
            @NonNull GuiGraphicsExtractor guiGraphics, Identifier texture,
            int x, int y, int u, int v, int width, int height,
            int textureWidth, int textureHeight, boolean flipX, boolean flipY
    ) {
        int blitWidth = flipX ? -width : width;
        int blitHeight = flipY ? -height : height;
        int blitU = flipX ? (u + width) : u;
        int blitV = flipY ? (v + height) : v;
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x, y,
                blitU, blitV,
                width, height,
                blitWidth, blitHeight,
                textureWidth, textureHeight
        );
    }
}
