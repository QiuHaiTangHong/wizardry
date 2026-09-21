package top.begonia.wizardry.core.inventory.slot;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class ItemListSlot extends ResourceHandlerSlot {
    private final int stackLimit;
    private final Predicate<Item> validator;

    public ItemListSlot(
            ItemStacksResourceHandler itemStacksResourceHandler, int slotIndex,
            int x, int y,
            int stackLimit, Predicate<Item> validator
    ) {
        super(itemStacksResourceHandler, itemStacksResourceHandler::set, slotIndex, x, y);
        this.validator = validator;
        this.stackLimit = stackLimit;
    }

    @Override
    public int getMaxStackSize() {
        return this.stackLimit;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (validator.test(stack.getItem())) {
            return super.mayPlace(stack);
        }
        return false;
    }
}
