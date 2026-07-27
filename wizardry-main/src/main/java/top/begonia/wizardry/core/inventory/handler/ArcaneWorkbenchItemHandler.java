package top.begonia.wizardry.core.inventory.handler;

import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.entity.block.ArcaneWorkbenchBlockEntity;
import top.begonia.wizardry.core.inventory.menu.ArcaneWorkbenchMenu;

/**
 * 弧形工作台项处理类
 * <p> 负责处理与弧形工作台相关的业务逻辑, 主要包含初始化项处理器和内容更改响应处理等功能.
 * 该类继承自 AbstractItemHandler, 并为 ArcaneWorkbenchBlockEntity 进行特定处理.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.07.06
 */
public class ArcaneWorkbenchItemHandler extends AbstractItemHandler<ArcaneWorkbenchBlockEntity> {

    public ArcaneWorkbenchItemHandler(ArcaneWorkbenchBlockEntity blockEntity, int size) {
        super(blockEntity, size);
    }

    @Override
    protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
        this.blockEntity.setChanged();
    }
}
