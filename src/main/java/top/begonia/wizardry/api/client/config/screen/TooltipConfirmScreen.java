package top.begonia.wizardry.api.client.config.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.client.config.utils.ConfigShared;

public final class TooltipConfirmScreen extends ConfirmScreen {
    public TooltipConfirmScreen(
            BooleanConsumer callback,
            Component title,
            Component message,
            Component yesButton,
            Component noButton
    ) {
        super(callback, title, message, yesButton, noButton);
    }

    @Override
    protected void addButtons(@NonNull LinearLayout layout) {
        super.addButtons(layout);
        if (this.noButton != null) {
            this.noButton.setTooltip(Tooltip.create(ConfigShared.RESTART_NO_TOOLTIP));
        }
    }
}
