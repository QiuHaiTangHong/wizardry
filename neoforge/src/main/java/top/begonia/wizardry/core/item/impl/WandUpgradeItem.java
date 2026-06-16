package top.begonia.wizardry.core.item.impl;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.util.TextHelper;

import java.util.function.Consumer;

public class WandUpgradeItem extends Item {
    public WandUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack itemStack) {
        Component name = itemStack.getComponents().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY);
        return name.copy().withStyle(Rarity.UNCOMMON.getStyleModifier());
    }

    @SuppressWarnings("deprecation")
    @Deprecated
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        TextHelper.addMultiLineDescription(
                builder,
                this.getDescriptionId() + ".desc",
                Style.EMPTY
        );
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}
