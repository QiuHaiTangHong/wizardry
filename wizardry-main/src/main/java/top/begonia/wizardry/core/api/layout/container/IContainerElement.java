package top.begonia.wizardry.core.api.layout.container;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.layout.IElement;
import top.begonia.wizardry.core.api.layout.util.Context;

public interface IContainerElement extends IElement {
    IContainerElement EMPTY_ELEMENT = new IContainerElement() {
        @Override
        public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {

        }

        @Override
        public void format(Context context) {

        }

        @Override
        public boolean isVisible() {
            return false;
        }

        @Override
        public void setXOffset(int x) {

        }

        @Override
        public void setYOffset(int y) {

        }

        @Override
        public int getXOffset() {
            return 0;
        }

        @Override
        public int getYOffset() {
            return 0;
        }
    };
}
