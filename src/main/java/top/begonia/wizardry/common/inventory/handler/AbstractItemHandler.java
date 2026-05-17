package top.begonia.wizardry.common.inventory.handler;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;

public abstract class AbstractItemHandler<T extends BlockEntity> extends ItemStacksResourceHandler {
    protected final T blockEntity;

    public AbstractItemHandler(T blockEntity, int size) {
        super(size);
        this.blockEntity = blockEntity;
    }

    protected abstract void onContentsChanged(int index, @NonNull ItemStack previousContents);

    public ItemStack getStack(int index) {
        if (index < 0 || index >= this.stacks.size()) {
            return ItemStack.EMPTY;
        }
        return this.stacks.get(index);
    }

    public void loadFromValueInput(List<ItemStackWithSlot> items) {
        Collections.fill(this.stacks, ItemStack.EMPTY);
        for (ItemStackWithSlot entry : items) {
            int slot = entry.slot();
            if (slot >= 0 && slot < this.stacks.size()) {
                this.stacks.set(slot, entry.stack());
            }
        }
    }

    public T getBlockEntity() {
        return this.blockEntity;
    }

    public NonNullList<ItemStack> getStacksList() {
        return this.stacks;
    }

    public void setStacksList(NonNullList<ItemStack> newStacks) {
        this.setStacks(newStacks);
    }
}
