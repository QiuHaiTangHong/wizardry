package top.begonia.wizardry.core.enchantment;

import net.minecraft.world.item.ItemStack;

public interface Imbuement {
    default void onImbuementRemoval(ItemStack stack) {
    }
}
