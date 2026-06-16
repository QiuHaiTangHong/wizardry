package top.begonia.wizardry.core.inventory.handler;

import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.entity.block.BookshelfBlockEntity;
import top.begonia.wizardry.core.inventory.menu.ArcaneWorkbenchMenu;

public class BookshelfItemHandler extends AbstractItemHandler<BookshelfBlockEntity> {
    public BookshelfItemHandler(BookshelfBlockEntity bookshelfBlockEntity, int size) {
        super(bookshelfBlockEntity, size);
    }

    @Override
    protected void onContentsChanged(int index, @NonNull ItemStack previousContents) {
        this.blockEntity.setChanged();
        if (index == ArcaneWorkbenchMenu.CENTRE_SLOT && this.blockEntity.getLevel() != null && !this.blockEntity.getLevel().isClientSide()) {
            this.blockEntity.sync();
        }
    }
}
