package top.begonia.wizardry.client.layout.hybrid;

import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.atom.IAtomElement;
import top.begonia.wizardry.client.layout.util.Context;

import java.util.List;

public class LineElement extends AbstractHybridElement {

    public LineElement(List<IAtomElement> lineElements) {
        this.addAllElement(lineElements);
    }

    public void centerX() {
        IAtomElement lastElement = this.getLastElement();
        int contentWidth = lastElement.getXOffset() + lastElement.getWidth();
        int changeXOffset = (this.width - contentWidth) / 2 - lastElement.getXOffset();
        this.getElements().forEach(iAtomElement -> iAtomElement.setXOffset(changeXOffset + iAtomElement.getXOffset()));
    }

    @Override
    public void format(@NonNull Context context) {
        this.setWidth(context.getMaxWidth());
        this.setHeight(context.getFont().lineHeight);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
        this.getElements().forEach(iAtomElement -> iAtomElement.setYOffset(yOffset));
    }
}
