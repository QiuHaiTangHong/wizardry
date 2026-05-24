package top.begonia.wizardry.core.item.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.util.TooltipBuilder;

import java.util.function.Consumer;

public class PurifyingElixirItem extends Item {
    public PurifyingElixirItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        TooltipBuilder.addMultiLineDescription(
                builder,
                this.getDescriptionId() + ".desc",
                Style.EMPTY
        );
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
