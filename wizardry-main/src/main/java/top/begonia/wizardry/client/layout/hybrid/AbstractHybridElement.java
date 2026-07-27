package top.begonia.wizardry.client.layout.hybrid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.atom.IAtomElement;
import top.begonia.wizardry.client.layout.util.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHybridElement implements IHybridElement {
    protected int xOffset, yOffset;
    protected int width, height;
    protected Font font = Minecraft.getInstance().font;
    protected List<IAtomElement> elements = new ArrayList<>();

    public final void clearElements() {
        this.elements.clear();
    }

    public final void addElement(IAtomElement iAtomElement) {
        this.elements.add(iAtomElement);
    }

    public final void addAllElement(List<IAtomElement> iAtomElements) {
        this.elements.addAll(iAtomElements);
    }

    public final List<IAtomElement> getElements() {
        return this.elements;
    }

    public final IAtomElement getFirstElement() {
        return this.elements.getFirst();
    }

    public final IAtomElement getLastElement() {
        return this.elements.getLast();
    }

    @Override
    public abstract void format(@NonNull Context context);

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        this.getElements().forEach(iAtomElement -> iAtomElement.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick));
    }

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
        int change = xOffset - this.getXOffset();
        this.xOffset = xOffset;
        this.elements.forEach(iAtomElement -> iAtomElement.setXOffset(change + iAtomElement.getXOffset()));
    }

    @Override
    public void setYOffset(int yOffset) {
        int change = yOffset - this.getYOffset();
        this.yOffset = yOffset;
        this.elements.forEach(iAtomElement -> iAtomElement.setYOffset(change + iAtomElement.getYOffset()));
    }

    @Override
    public final int getXOffset() {
        return this.xOffset;
    }

    @Override
    public final int getYOffset() {
        return this.yOffset;
    }

    public final Font getFont() {
        return font;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.xOffset && mouseX <= this.getWidth() + this.xOffset && mouseY >= this.yOffset && mouseY <= this.yOffset + this.getHeight();
    }
}
