package top.begonia.wizardry.common.inventory.slot;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;

public class ItemClassListSlot extends ResourceHandlerSlot {
    private final Class<? extends Item>[] itemClasses;
    private final int stackLimit;

    @SafeVarargs
    public ItemClassListSlot(ItemStacksResourceHandler itemStacksResourceHandler, int slotIndex, int x, int y, int stackLimit, Class<? extends Item>... allowedItemClasses) {
        super(itemStacksResourceHandler, itemStacksResourceHandler::set, slotIndex, x, y);
        this.itemClasses = allowedItemClasses;
        this.stackLimit = stackLimit;
    }

    @Override
    public int getMaxStackSize() {
        return this.stackLimit;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;
        Class<? extends Item> itemClassInSlot = stack.getItem().getClass();
        for (Class<? extends Item> allowedClass : itemClasses) {
            if (allowedClass.isAssignableFrom(itemClassInSlot)) {
                return true;
            }
        }
        return false;
    }
}
