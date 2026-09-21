package top.begonia.wizardry.core.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NonNull;

public class WizardryRecipeInput implements RecipeInput {
    @Override
    public @NonNull ItemStack getItem(int slotIndex) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}