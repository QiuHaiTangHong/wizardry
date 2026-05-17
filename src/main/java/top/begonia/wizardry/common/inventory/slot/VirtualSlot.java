package top.begonia.wizardry.common.inventory.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;

public class VirtualSlot extends ResourceHandlerSlot {

    private final BlockEntity blockEntity;
    private final ItemStack prevStack;

    public VirtualSlot(ItemStacksResourceHandler itemStacksResourceHandler, int index) {
        super(itemStacksResourceHandler, itemStacksResourceHandler::set, index, -999, -999);
        if (!(container instanceof BlockEntity be)) {
            throw new IllegalArgumentException("Inventory must be a block entity!");
        }
        this.blockEntity = be;
        this.prevStack = this.getItem().copy();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public boolean isValid() {
        return !blockEntity.isRemoved();
    }

    @Override
    public boolean mayPlace(@NonNull ItemStack stack) {
        return isValid() && container.canPlaceItem(getContainerSlot(), stack);
    }

    @Override
    public boolean mayPickup(@NonNull Player player) {
        return isValid() && super.mayPickup(player);
    }

    @Override
    public void onTake(@NonNull Player player, @NonNull ItemStack stack) {
        if (isValid()) {
            super.onTake(player, stack);
        }
    }

    public ItemStack getPrevStack() {
        return prevStack;
    }

}
