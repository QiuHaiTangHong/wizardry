package top.begonia.wizardry.common.item.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ArmourUpgradeItem extends Item {
    public ArmourUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
            @NonNull ItemStack itemStack,
            @NonNull TooltipContext context,
            @NonNull TooltipDisplay display,
            @NonNull Consumer<Component> builder,
            @NonNull TooltipFlag tooltipFlag
    ) {
        builder.accept(Component.translatable(this.getDescriptionId() + ".desc"));
    }
}
