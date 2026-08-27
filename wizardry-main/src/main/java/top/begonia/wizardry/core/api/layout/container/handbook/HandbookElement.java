package top.begonia.wizardry.core.api.layout.container.handbook;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.layout.container.IContainerElement;
import top.begonia.wizardry.core.api.layout.util.Context;
import top.begonia.wizardry.core.api.layout.util.PageTurner;

public class HandbookElement implements IContainerElement {
    private int xOffset, yOffset;
    private final @NotNull PageTurner pageTurner;

    public HandbookElement(Context context) {
        this.pageTurner = new PageTurner();
        this.format(context);
    }

    public @NonNull PageTurner getPageTurner() {
        return this.pageTurner;
    }

    @Override
    public void format(Context context) {
        this.pageTurner.format(context);
    }

    @Override
    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
        for (int i = 0; i < this.pageTurner.getDisplayPageElements().size(); i++) {
            this.pageTurner.getDisplayPageElements().get(i).setXOffset(xOffset + ((i + 1) % 2 == 0 ? 150 : 16));
        }
    }

    @Override
    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
        this.pageTurner.getDisplayPageElements().forEach(sectionElement -> sectionElement.setYOffset(yOffset + 16));
    }

    @Override
    public int getXOffset() {
        return this.xOffset;
    }

    @Override
    public int getYOffset() {
        return this.yOffset;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        for (IContainerElement iContainerElement : this.pageTurner.getDisplayPageElements()) {
            if (iContainerElement.mouseClicked(event, doubleClick)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (IContainerElement iContainerElement : this.pageTurner.getDisplayPageElements()) {
            if (iContainerElement.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        if (!this.pageTurner.getDisplayPageElements().isEmpty()) {
            this.pageTurner.getDisplayPageElements().forEach(displayPage -> displayPage.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick));
        }
    }
}
