package top.begonia.wizardry.api.item;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public interface IMultiTexturedItem {
    Identifier getModelName(ItemStack stack);
}
