package top.begonia.wizardry.client.layout.atom;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.util.Context;

public abstract class AbstractAtomElement implements IAtomElement {
    protected int xOffset, yOffset;
    protected int width, height;
    protected Font font;

    @Override
    public abstract void format(@NonNull Context context);

    @Override
    public abstract void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick);

    @Override
    public final int getHeight() {
        return this.height;
    }

    @Override
    public final int getWidth() {
        return this.width;
    }

    @Override
    public final void setHeight(int height) {
        this.height = height;
    }

    @Override
    public final void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    @Override
    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    @Override
    public final int getXOffset() {
        return this.xOffset;
    }

    @Override
    public final int getYOffset() {
        return this.yOffset;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.xOffset && mouseX <= this.getWidth() + this.xOffset && mouseY >= this.yOffset && mouseY <= this.yOffset + this.getHeight();
    }

    public final Font getFont() {
        return font;
    }
}
