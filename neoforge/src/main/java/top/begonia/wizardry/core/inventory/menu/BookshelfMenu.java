package top.begonia.wizardry.core.inventory.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.entity.block.BookshelfBlockEntity;
import top.begonia.wizardry.core.inventory.InventoryUtil;
import top.begonia.wizardry.core.inventory.slot.BookshelfSlot;
import top.begonia.wizardry.core.registry.WizardryBlocks;
import top.begonia.wizardry.core.registry.WizardryMenus;

public class BookshelfMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;

    public BookshelfMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory, (BookshelfBlockEntity) playerInventory.player.level().getBlockEntity(pos), ContainerLevelAccess.create(playerInventory.player.level(), pos));
    }

    public BookshelfMenu(int containerId, Inventory playerInventory, BookshelfBlockEntity blockEntity, ContainerLevelAccess access) {
        super(WizardryMenus.BOOKSHELF.get(), containerId);
        this.access = access;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < BookshelfBlockEntity.SLOT_COUNT / 2; x++) {
                this.addSlot(new BookshelfSlot(
                        blockEntity.getInventory(),
                        x + BookshelfBlockEntity.SLOT_COUNT / 2 * y,
                        35 + x * 18,
                        17 + y * 18,
                        () -> {
                            if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
                                blockEntity.sync();
                            }
                        }
                ));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 124));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, 9 + x + y * 9, 8 + x * 18, 66 + y * 18));
            }
        }
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < BookshelfBlockEntity.SLOT_COUNT) {
                if (!this.moveItemStackTo(itemstack1, BookshelfBlockEntity.SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (InventoryUtil.isBook(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, BookshelfBlockEntity.SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (index < BookshelfBlockEntity.SLOT_COUNT + 9) {
                        if (!this.moveItemStackTo(itemstack1, BookshelfBlockEntity.SLOT_COUNT + 9, this.slots.size(), false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, BookshelfBlockEntity.SLOT_COUNT, BookshelfBlockEntity.SLOT_COUNT + 9, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(this.access, player, WizardryBlocks.BOOKSHELF.get());
    }
}
