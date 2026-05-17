package top.begonia.wizardry.common.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public interface IMultiTexturedItem {
    Identifier getModelName(ItemStack stack);
}
