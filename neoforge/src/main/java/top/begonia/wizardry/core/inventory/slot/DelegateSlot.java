package top.begonia.wizardry.core.inventory.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.world.inventory.StackCopySlot;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.inventory.handler.BookshelfItemHandler;

/**
 * 委托槽位类, 继承自 {@link StackCopySlot}, 用于将物品栈的操作委托给外部的 {@link ItemStacksResourceHandler} 资源处理器进行管理
 * <p> 该类作为容器与资源处理器之间的桥梁, 不直接持有物品栈数据, 而是通过资源处理器间接读写
 * <p> 当外部调用 {@link #setStackCopy},{@link #getStackCopy},{@link #getSlotIndex} 等方法时, 实际操作会映射到绑定的处理器句柄
 * <p> 适用于需要统一管理多个槽位资源, 避免直接操作物品栈的场景, 例如与 {@link BlockEntity} 体系结合使用时
 * <p> 支持通过 {@link #bind} 方法动态绑定到不同的方块实体和资源处理器, 并在绑定后校验有效性
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.07.06
 */
public class DelegateSlot extends StackCopySlot {
    private BookshelfItemHandler handler;
    private IndexModifier<ItemResource> slotModifier;
    private int handlerIndex;
    private final OnSafeInsertDelegateFunction onSafeInsertEntrust;
    private final OnTakeDelegateFunction onTakeEntrust;

    public DelegateSlot(int index, int x, int y, OnSafeInsertDelegateFunction onSafeInsertEntrust, OnTakeDelegateFunction onTakeEntrust) {
        super(index, x, y);
        this.onSafeInsertEntrust = onSafeInsertEntrust;
        this.onTakeEntrust = onTakeEntrust;
    }

    @FunctionalInterface
    public interface OnTakeDelegateFunction {
        void accept(ItemStacksResourceHandler itemStacksResourceHandler, ItemStack carried, int handlerIndex, int slotIndex);
    }

    @FunctionalInterface
    public interface OnSafeInsertDelegateFunction {
        ItemStack accept(ItemStacksResourceHandler itemStacksResourceHandler, ItemStack inputStack, int inputAmount, int handlerIndex, int slotIndex);
    }

    @Override
    protected @NonNull ItemStack getStackCopy() {
        if (handler == null) {
            return ItemStack.EMPTY;
        }
        return this.handler.getResource(this.handlerIndex).toStack(this.handler.getAmountAsInt(this.handlerIndex));
    }

    @Override
    public int getSlotIndex() {
        return this.index;
    }

    @Override
    public int getMaxStackSize() {
        return this.handler.getCapacityAsInt(this.handlerIndex, ItemResource.EMPTY);
    }

    @Override
    public int getMaxStackSize(@NonNull ItemStack stack) {
        return this.handler.getCapacityAsInt(this.handlerIndex, ItemResource.of(stack));
    }

    @Override
    protected final void setStackCopy(@NonNull ItemStack stack) {
        if (handler == null) {
            return;
        }
        slotModifier.set(this.handlerIndex, ItemResource.of(stack), stack.getCount());
    }

    public void bind(@NonNull BookshelfItemHandler handler, int index) {
        this.handler = handler;
        this.slotModifier = handler::set;
        this.handlerIndex = index;
        ItemStack itemStack = this.handler.getResource(index).toStack();
        int count = this.handler.getAmountAsInt(index);
        itemStack.setCount(count);
        this.set(itemStack);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    public boolean isBind() {
        return this.handler != null && !this.handler.getBlockEntity().isRemoved();
    }

    @Override
    public boolean mayPlace(@NonNull ItemStack stack) {
        return isBind() && container.canPlaceItem(getContainerSlot(), stack);
    }

    @Override
    public boolean mayPickup(@NonNull Player player) {
        return isBind() && super.mayPickup(player);
    }

    @Override
    public void onTake(@NonNull Player player, @NonNull ItemStack carried) {
        if (this.onTakeEntrust != null) {
            this.onTakeEntrust.accept(this.handler, this.getItem(), this.handlerIndex, this.index);
        }
    }

    @Override
    public @NonNull ItemStack safeInsert(@NonNull ItemStack inputStack, int inputAmount) {
        if (onSafeInsertEntrust != null) {
            return this.onSafeInsertEntrust.accept(this.handler, inputStack, inputAmount, this.handlerIndex, this.index);
        }
        return ItemStack.EMPTY;
    }
}
