package top.begonia.wizardry.core.inventory.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.inventory.menu.ArcaneWorkbenchMenu;
import top.begonia.wizardry.core.item.impl.SpellBookItem;

public class BookListSlot extends ItemClassListSlot {
    private final ArcaneWorkbenchMenu container;
    private final int listIndex;

    public BookListSlot(ItemStacksResourceHandler inventory, int index, int x, int y, ArcaneWorkbenchMenu container, int listIndex) {
        super(inventory, index, x, y, 64, SpellBookItem.class);
        this.container = container;
        this.listIndex = listIndex;
    }

    public VirtualSlot getDelegate() {
        return hasDelegate() ? container.getVisibleBookshelfSlots().get(listIndex) : null;
    }

    public boolean hasDelegate() {
        return listIndex < container.getVisibleBookshelfSlots().size();
    }

    @Override
    public void onTake(@NonNull Player player, @NonNull ItemStack stack) {

    }

}
