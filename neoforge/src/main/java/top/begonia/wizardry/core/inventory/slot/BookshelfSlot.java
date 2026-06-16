package top.begonia.wizardry.core.inventory.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.inventory.InventoryUtil;

public class BookshelfSlot extends ResourceHandlerSlot {
    private final Runnable onChanged;

    public BookshelfSlot(
            ItemStacksResourceHandler itemStacksResourceHandler,
            int index, int x, int y,
            Runnable onChanged
    ) {
        super(itemStacksResourceHandler, itemStacksResourceHandler::set, index, x, y);
        this.onChanged = onChanged;
    }

    @Override
    public boolean mayPlace(@NonNull ItemStack stack) {
        return super.mayPlace(stack) && InventoryUtil.isBook(stack);
    }

    @Override
    protected void setStackCopy(@NonNull ItemStack stack) {
        super.setStackCopy(stack);
        this.onChanged.run();
    }
}
