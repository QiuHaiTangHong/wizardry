package top.begonia.wizardry.api.config.utils;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public record Element(
        @Nullable Component name,
        @Nullable Component tooltip,
        @Nullable AbstractWidget widget,
        @Nullable OptionInstance<?> option,
        boolean undoable
) {
    @ApiStatus.Internal
    public Element {
    }

    public Element(@Nullable final Component name, @Nullable final Component tooltip, final AbstractWidget widget) {
        this(name, tooltip, widget, null, true);
    }

    public Element(@Nullable final Component name, @Nullable final Component tooltip, final AbstractWidget widget, boolean undoable) {
        this(name, tooltip, widget, null, undoable);
    }

    public Element(final Component name, final Component tooltip, final OptionInstance<?> option) {
        this(name, tooltip, null, option, true);
    }

    public Element(final Component name, final Component tooltip, final OptionInstance<?> option, boolean undoable) {
        this(name, tooltip, null, option, undoable);
    }

    public AbstractWidget getWidget(final Options options) {
        return widget != null ? widget : option.createButton(options);
    }

    @Nullable
    public Object any() {
        return widget != null ? widget : option;
    }
}
