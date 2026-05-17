package top.begonia.wizardry.common.item.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ArcaneTomeItem extends Item {

    public ArcaneTomeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return true;
    }

	@Deprecated
	public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
	}

}
