package top.begonia.wizardry.core.inventory.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.item.IWorkbenchItem;

import java.util.function.Consumer;

/**
 * 工作台物品槽位类
 * <p>专门用于工作台 UI 的物品容器逻辑, 继承自 {@code ResourceHandlerSlot}
 * <p>主要职责是管理槽位中物品的放入和取出事件回调, 并对可放入的物品类型和单格最大堆叠数施加工作台特有的限制
 * <ul>
 * <li>限制单格最大堆叠数为 16</li>
 * <li>只允许实现了 {@code IWorkbenchItem} 接口且通过 {@code canPlace} 检查的物品放入</li>
 * <li>在物品放入 (包括安全插入) 和取出时, 通过外部注入的 {@code Consumer<ItemStack>} 回调通知子系统</li>
 * </ul>
 * 该类仅作为内部使用的资源槽位扩展, 不处理网络请求或独立业务逻辑
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.07.04
 */
public class WorkbenchItemSlot extends ResourceHandlerSlot {
    private final Consumer<ItemStack> onPutCallback;
    private final Consumer<ItemStack> onTakeCallback;

    public WorkbenchItemSlot(
            ItemStacksResourceHandler itemStacksResourceHandler,
            int slotIndex, int x, int y,
            Consumer<ItemStack> onPutCallback, Consumer<ItemStack> onTakeCallback
    ) {
        super(itemStacksResourceHandler, itemStacksResourceHandler::set, slotIndex, x, y);
        this.onPutCallback = onPutCallback;
        this.onTakeCallback = onTakeCallback;
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public void onTake(@NonNull Player player, @NonNull ItemStack carried) {
        super.onTake(player, carried);
        if (this.onTakeCallback != null) {
            this.onTakeCallback.accept(this.getItem());
        }
    }

    @Override
    protected void setStackCopy(@NonNull ItemStack stack) {
        super.setStackCopy(stack);
        if (onPutCallback != null) {
            this.onPutCallback.accept(stack);
        }
    }

    @Override
    public @NonNull ItemStack safeInsert(@NonNull ItemStack inputStack, int inputAmount) {
        if (onPutCallback != null) {
            this.onPutCallback.accept(inputStack);
        }
        return super.safeInsert(inputStack, inputAmount);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.getItem() instanceof IWorkbenchItem workbenchItem && workbenchItem.canPlace(stack);
    }
}
