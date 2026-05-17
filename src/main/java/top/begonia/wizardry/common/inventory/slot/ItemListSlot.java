package top.begonia.wizardry.common.inventory.slot;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ItemListSlot extends ResourceHandlerSlot {
    private final List<Item> allowedItems;
    private final int stackLimit;

    public ItemListSlot(ItemStacksResourceHandler itemStacksResourceHandler, int slotIndex, int x, int y, int stackLimit, Item... allowedItems) {
        super(itemStacksResourceHandler, itemStacksResourceHandler::set, slotIndex, x, y);
        this.allowedItems = Arrays.asList(allowedItems);
        this.stackLimit = stackLimit;
    }

    @Override
    public int getMaxStackSize() {
        return this.stackLimit;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        for (Item item : allowedItems) {
            if (stack.is(item)) {
                return true;
            }
        }
        return false;
    }
}
