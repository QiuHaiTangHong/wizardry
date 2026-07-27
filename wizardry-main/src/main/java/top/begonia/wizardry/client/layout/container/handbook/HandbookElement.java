package top.begonia.wizardry.client.layout.container.handbook;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.container.IContainerElement;
import top.begonia.wizardry.client.layout.util.Context;
import top.begonia.wizardry.client.layout.util.PageTurner;

public class HandbookElement implements IContainerElement {
    private int xOffset, yOffset;
    private final @NotNull PageTurner pageTurner;
    private static Runnable tooltipTask = null;

    public HandbookElement(Context context) {
        this.pageTurner = new PageTurner();
        this.format(context);
    }

    public static void pushTooltip(Runnable task) {
        tooltipTask = task;
    }

    public @NonNull PageTurner getPageTurner() {
        return this.pageTurner;
    }

    public static void renderDeferredTooltip() {
        if (tooltipTask != null) {
            tooltipTask.run();
            tooltipTask = null;
        }
    }

    @Override
    public void format(Context context) {
        this.pageTurner.format(context);
    }

    @Override
    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
        this.pageTurner.getDisplaySection().forEach(sectionElement -> sectionElement.setXOffset(xOffset));
    }

    @Override
    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
        this.pageTurner.getDisplaySection().forEach(sectionElement -> sectionElement.setYOffset(yOffset));
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
        for (IContainerElement iContainerElement : this.pageTurner.getDisplaySection()) {
            if (iContainerElement.mouseClicked(event, doubleClick)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (IContainerElement iContainerElement : this.pageTurner.getDisplaySection()) {
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
        if (!this.pageTurner.getDisplaySection().isEmpty()) {
            this.pageTurner.getDisplaySection().forEach(displayPage -> displayPage.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick));
            renderDeferredTooltip();
        }
    }
}
