package top.begonia.wizardry.client.layout.hybrid;

import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.atom.IAtomElement;
import top.begonia.wizardry.client.data.definition.handbook.part.CentreConfigData;
import top.begonia.wizardry.client.layout.util.Context;

import java.util.List;

public class PageElement extends AbstractHybridElement {

    public PageElement(List<IAtomElement> pageElements) {
        this.addAllElement(pageElements);
    }

    @Override
    public void format(@NonNull Context context) {
        super.setWidth(context.getMaxWidth());
        super.setHeight(context.getMaxHeight());
    }

    public void format(@NonNull Context context, @NonNull CentreConfigData centreConfigData) {
        this.format(context);
        if (centreConfigData.x()) {
            for (IAtomElement element : this.getElements()) {
                int centeredX = (context.getMaxWidth() - element.getWidth()) / 2;
                element.setXOffset(centeredX + this.getXOffset());
                if (element instanceof LineElement lineElement) {
                    lineElement.centerX();
                }
            }
        }

        if (centreConfigData.y() && !this.getElements().isEmpty()) {
            IAtomElement first = this.getFirstElement();
            IAtomElement last = this.getLastElement();
            int contentTop = first.getYOffset();
            int contentBottom = last.getYOffset() + last.getHeight();
            int totalContentHeight = contentBottom - contentTop;
            int targetTop = (context.getMaxHeight() - totalContentHeight) / 2;
            int deltaY = targetTop - contentTop;
            for (IAtomElement element : this.getElements()) {
                element.setYOffset(element.getYOffset() + deltaY + this.getYOffset());
            }
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        for (IAtomElement iAtomElement : this.getElements()) {
            if (iAtomElement.mouseClicked(event, doubleClick)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (IAtomElement iAtomElement : this.getElements()) {
            if (iAtomElement.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
