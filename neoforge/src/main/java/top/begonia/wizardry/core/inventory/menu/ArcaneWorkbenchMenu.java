package top.begonia.wizardry.core.inventory.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.api.event.SpellBindEvent;
import top.begonia.wizardry.core.block.BookshelfBlock;
import top.begonia.wizardry.core.inventory.slot.*;
import top.begonia.wizardry.client.util.ISpellSortable;
import top.begonia.wizardry.core.entity.block.ArcaneWorkbenchBlockEntity;
import top.begonia.wizardry.core.item.IWorkbenchItem;
import top.begonia.wizardry.core.item.impl.SpellBookItem;
import top.begonia.wizardry.core.registry.WizardryBlocks;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryMenus;

import java.util.*;

public class ArcaneWorkbenchMenu extends AbstractContainerMenu implements ISpellSortable {
    public static final Identifier EMPTY_SLOT_CRYSTAL = Identifier.fromNamespaceAndPath(Wizardry.MODID, "container/empty_slot_crystal");
    public static final Identifier EMPTY_SLOT_UPGRADE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "container/empty_slot_upgrade");
    public static final int CRYSTAL_SLOT = 8;
    public static final int CENTRE_SLOT = 9;
    public static final int UPGRADE_SLOT = 10;
    public static final int SLOT_RADIUS = 42;
    public static final int BOOKSHELF_SLOTS_X = 5;
    public static final int BOOKSHELF_SLOTS_Y = 10;
    public static final int PLAYER_INVENTORY_SIZE = 36;
    public static final int BOOKSHELF_UI_WIDTH = 122;
    private final ContainerLevelAccess access;
    private final ISpellSortable.SortType sortType = ISpellSortable.SortType.TIER;
    private final Map<EmbeddedSlot, Long> inventoryInfos = new LinkedHashMap<>();
    private final List<VirtualSlot> bookshelfSlots = new ArrayList<>();
    public boolean needsRefresh;
    public ArcaneWorkbenchBlockEntity blockEntity;
    private boolean hasBookshelves;
    private List<VirtualSlot> activeBookshelfSlots = new ArrayList<>();
    private int scroll = 0;

    public ArcaneWorkbenchMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(
                containerId,
                playerInventory,
                (ArcaneWorkbenchBlockEntity) playerInventory.player.level().getBlockEntity(pos),
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    public ArcaneWorkbenchMenu(int containerId, Inventory playerInventory, ArcaneWorkbenchBlockEntity blockEntity, ContainerLevelAccess access) {
        super(WizardryMenus.ARCANE_WORKBENCH.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = access;
        for (int i = 0; i < 8; i++) {
            this.addSlot(new ItemClassListSlot(
                    this.blockEntity.getItemHandler(), i,
                    -999, -999,
                    1, SpellBookItem.class)
            );
        }
        this.packInventoryInfo(EmbeddedSlot.STAFF_SLOT, 0, 7);
        this.addSlot(new ItemListSlot(blockEntity.getItemHandler(), 8, 13, 101, 64,
                WizardryItems.MAGIC_CRYSTAL.get(),
                WizardryItems.CRYSTAL_SHARD.get(),
                WizardryItems.GRAND_CRYSTAL.get())
        ).setBackground(EMPTY_SLOT_CRYSTAL);
        this.packInventoryInfo(EmbeddedSlot.CRYSTAL_SLOT, 8, 8);
        this.addSlot(new WorkbenchItemSlot(
                blockEntity.getItemHandler(),
                9,
                80, 64, this
        ));
        this.packInventoryInfo(EmbeddedSlot.CENTRE_SLOT, 9, 9);
        this.addSlot(new ItemListSlot(blockEntity.getItemHandler(), 10, 147, 17, 1,
                WizardryItems.ARCANE_TOME.get(),
                WizardryItems.RESPLENDENT_THREAD.get(),
                WizardryItems.CRYSTAL_SILVER_PLATING.get(),
                WizardryItems.ETHEREAL_CRYSTALWEAVE.get()
        )).setBackground(EMPTY_SLOT_UPGRADE);
        this.packInventoryInfo(EmbeddedSlot.UPGRADE_SLOT, 10, 10);
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 196));
        }
        this.packInventoryInfo(EmbeddedSlot.PLAYER_QUICK_SLOT, 11, 19);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9
                        , 8 + j * 18, 138 + i * 18));
            }
        }
        this.packInventoryInfo(EmbeddedSlot.PLAYER_INVENTORY_SLOT, 20, 46);
        refreshBookshelfSlots();
    }

    public void onApplyButtonPressed(Player player) {

        if (NeoForge.EVENT_BUS.post(new SpellBindEvent(player, this)).isCanceled()) {
            return;
        }

        Slot centre = this.getSlot(CENTRE_SLOT);

        if (centre.getItem().getItem() instanceof IWorkbenchItem iWorkbenchItem) {

            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);

            if (iWorkbenchItem.onApplyButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks)) {
                if (player instanceof ServerPlayer serverPlayer) {
//                    WizardryAdvancementTriggers.arcane_workbench.trigger((EntityPlayerMP) player, centre.getStack());
                }
            }
        }
    }

    public void onClearButtonPressed(Player player) {

        Slot centre = this.getSlot(CENTRE_SLOT);

        if (centre.getItem().getItem() instanceof IWorkbenchItem iWorkbenchItem) {
            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);
            iWorkbenchItem.onClearButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks);
        }
    }

    public static int getBookSlotXOffset(int i, int bookSlotCount) {
        float angle = i * (2 * (float) Math.PI) / bookSlotCount;
        return Math.toIntExact(Math.round(SLOT_RADIUS * Math.sin(angle)));
    }

    public static int getBookSlotYOffset(int i, int bookSlotCount) {
        float angle = i * (2 * (float) Math.PI) / bookSlotCount;
        return Math.toIntExact(Math.round(SLOT_RADIUS * -Math.cos(angle)));
    }

    public void packInventoryInfo(EmbeddedSlot type, int startIndex, int stopIndex) {
        this.inventoryInfos.put(type, ((long) startIndex << 32) | ((long) stopIndex & 0xFFFFFFFFL));
    }

    public void inventoryInfoIterator(EmbeddedSlot type, InventoryInfoConsumer consumer) {
        long packInventoryInfo = this.inventoryInfos.get(type);
        int startIndex = (int) (packInventoryInfo >> 32);
        int stopIndex = (int) packInventoryInfo;
        for (int i = startIndex; i <= stopIndex; i++) {
            Slot slot = this.getSlot(i);
            int relativeIndex = i - startIndex;
            consumer.accept(relativeIndex, slot);
        }
    }

    public void scrollTo(int row) {
        this.scroll = row;
    }

    public ItemStack getItemStack(int index) {
        return this.slots.get(index).getItem();
    }

    public List<VirtualSlot> getBookshelfSlots() {
        List<VirtualSlot> validSlots = new ArrayList<>(bookshelfSlots);
        validSlots.removeIf(s -> !s.isValid());
        return validSlots;
    }

    public List<VirtualSlot> getVisibleBookshelfSlots() {
        List<VirtualSlot> activeSlots = getActiveBookshelfSlots();
        return activeSlots.subList(BOOKSHELF_SLOTS_X * scroll, activeSlots.size());
    }

    public List<VirtualSlot> getActiveBookshelfSlots() {
        return activeBookshelfSlots;
    }

    public boolean hasBookshelves() {
        return hasBookshelves;
    }

    public void refreshBookshelfSlots() {
        bookshelfSlots.clear();
        Level level = blockEntity.getLevel();
        if (level != null) {
            BookshelfBlock.findNearbyBookshelves(blockEntity.getLevel(), blockEntity.getBlockPos(), (itemResourceResourceHandler, blockEntity) -> {
                for (int i = 0; i < itemResourceResourceHandler.size(); i++) {
                    if (itemResourceResourceHandler instanceof ItemStacksResourceHandler concreteHandler) {
                        VirtualSlot slot = new VirtualSlot(concreteHandler, blockEntity, i);
                        bookshelfSlots.add(slot);
                        this.addSlot(slot);
                    }
                }
                return false;
            }, blockEntity);
            if (bookshelfSlots.isEmpty() == hasBookshelves) {
                hasBookshelves = !bookshelfSlots.isEmpty();
            }
            if (blockEntity.getLevel().isClientSide()) {
                updateActiveBookshelfSlots();
            }
        }
    }

    public void updateActiveBookshelfSlots() {
        activeBookshelfSlots = bookshelfSlots.stream().toList();
    }

    public void updateSlotPosition() {
    }

    @Override
    public @NonNull ItemStack quickMoveStack(@NonNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NonNull Player player) {
        return stillValid(this.access, player, WizardryBlocks.ARCANE_WORKBENCH.get());
    }

    @Override
    public SortType getSortType() {
        return this.sortType;
    }

    @Override
    public boolean isSortDescending() {
        return false;
    }

    public void onSlotChanged(int slotNumber, ItemStack stack, Player player) {
        if (slotNumber == CENTRE_SLOT) {
            if (stack.isEmpty()) {
                for (int i = 0; i < CRYSTAL_SLOT; i++) {
                    this.hideSlot(i, player);
                }
            } else {
                if (stack.getItem() instanceof IWorkbenchItem) {
                    int spellSlots = ((IWorkbenchItem) stack.getItem()).getSpellSlotCount(stack);
                    int centreX = this.getSlot(CENTRE_SLOT).x;
                    int centreY = this.getSlot(CENTRE_SLOT).y;
                    for (int i = 0; i < spellSlots; i++) {
                        int x = centreX + getBookSlotXOffset(i, spellSlots);
                        int y = centreY + getBookSlotYOffset(i, spellSlots);
                        showSlot(i, x, y);
                    }
                    for (int i = spellSlots; i < CRYSTAL_SLOT; i++) {
                        hideSlot(i, player);
                    }
                }
            }
        }
    }

    private void showSlot(int index, int x, int y) {
        Slot slot = this.getSlot(index);
        slot.x = x;
        slot.y = y;
    }

    private void hideSlot(int index, Player player) {
        Slot slot = this.getSlot(index);
        slot.x = -999;
        slot.y = -999;
        ItemStack stack = slot.getItem();
        ItemStack remainder = this.quickMoveStack(player, index);
        if (remainder.isEmpty() && !stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
            player.drop(stack, false);
        }
    }

    public enum EmbeddedSlot {
        STAFF_SLOT,
        CRYSTAL_SLOT,
        CENTRE_SLOT,
        UPGRADE_SLOT,
        PLAYER_QUICK_SLOT,
        PLAYER_INVENTORY_SLOT
    }

    @FunctionalInterface
    public interface InventoryInfoConsumer {
        void accept(int relativeIndex, Slot slot);
    }
}
