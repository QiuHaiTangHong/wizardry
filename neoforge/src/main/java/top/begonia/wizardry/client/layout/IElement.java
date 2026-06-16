package top.begonia.wizardry.client.layout;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.util.Context;

public interface IElement extends GuiEventListener, NarratableEntry, Renderable {
    void format(Context context);

    boolean isVisible();

    void setXOffset(int x);

    void setYOffset(int y);

    int getXOffset();

    int getYOffset();

    @Override
    default void setFocused(boolean b) {

    }

    @Override
    default void updateNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    default @NonNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    default boolean isFocused() {
        return false;
    }
}
