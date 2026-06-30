package top.begonia.wizardry.core.inventory.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.inventory.menu.ArcaneWorkbenchMenu;
import top.begonia.wizardry.core.item.IWorkbenchItem;

public class WorkbenchItemSlot extends ResourceHandlerSlot {
    private final ArcaneWorkbenchMenu menu;
    private final int index;

    public WorkbenchItemSlot(ItemStacksResourceHandler itemStacksResourceHandler, int slotIndex, int x, int y, ArcaneWorkbenchMenu menu) {
        super(itemStacksResourceHandler, itemStacksResourceHandler::set, slotIndex, x, y);
        this.menu = menu;
        this.index = slotIndex;
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        super.onTake(player, stack);
        this.menu.onSlotChanged(this.index, ItemStack.EMPTY, player);
    }

    @Override
    protected void setStackCopy(@NonNull ItemStack stack) {
        super.setStackCopy(stack);
        this.menu.onSlotChanged(this.index, stack, this.menu.player);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.getItem() instanceof IWorkbenchItem workbenchItem && workbenchItem.canPlace(stack);
    }
}
