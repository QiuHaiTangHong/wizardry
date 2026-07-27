package top.begonia.wizardry.core.inventory.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.util.ItemStackHelper;

/**
 * 书架物品槽位
 * <p> 继承自资源处理槽位, 专门用于存放书籍类物品的槽位
 * <p> 通过重写放置规则限制只能接受书籍物品, 同时支持物品状态的变更回调
 * <p> 该类仅限书架相关组件内部使用, 不对外暴露
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.07.06
 */
public class BookshelfSlot extends ResourceHandlerSlot {

    /**
     * 构造书架物品槽位实例
     * <p> 初始化一个书架物品槽位对象, 该对象继承自资源处理槽位, 并专门用于存放书籍类物品.
     *
     * @param itemStacksResourceHandler 资源处理堆叠实例, 用于管理储物格中的物品状态变更
     * @param index                     槽位索引值
     * @param x                         X 轴坐标值
     * @param y                         Y 轴坐标值
     * @param onChanged                 配置更改时回调的 Runnable 接口实现类, 用于接收状态变化事件
     */
    public BookshelfSlot(
            ItemStacksResourceHandler itemStacksResourceHandler,
            int index, int x, int y
    ) {
        super(itemStacksResourceHandler, itemStacksResourceHandler::set, index, x, y);
    }

    /**
     * 判断是否可以放置物品
     * <p> 重写父类的方法来判断传入的项堆栈是否可以放置. 只有当该堆栈是书籍时才允许.
     *
     * @param stack 要判断的项堆栈
     * @return 如果可以放置则返回 true, 否则返回 false
     */
    @Override
    public boolean mayPlace(@NonNull ItemStack stack) {
        return super.mayPlace(stack) && ItemStackHelper.isBook(stack);
    }

    /**
     * 设置堆栈副本
     * <p> 重写父类方法来复制项堆栈. 此方法会调用父类的同名方法进行实际操作.
     *
     * @param stack 要设置的堆栈副本
     */
    @Override
    protected void setStackCopy(@NonNull ItemStack stack) {
        super.setStackCopy(stack);
    }
}
