package top.begonia.wizardry.core.inventory.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.api.event.SpellBindEvent;
import top.begonia.wizardry.core.block.BookshelfBlock;
import top.begonia.wizardry.core.inventory.handler.BookshelfItemHandler;
import top.begonia.wizardry.core.inventory.slot.*;
import top.begonia.wizardry.client.util.ISpellSortable;
import top.begonia.wizardry.core.entity.block.ArcaneWorkbenchBlockEntity;
import top.begonia.wizardry.core.item.IWorkbenchItem;
import top.begonia.wizardry.core.item.impl.*;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.ItemStackHelper;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 奥术工作台菜单类
 * <p> 负责管理奥术工作台的容器逻辑和交互界面, 提供法术绑定, 升级和书架检索等核心功能.
 * <p> 该类作为 {@code AbstractContainerMenu} 的子类, 实现了 {@code ISpellSortable} 接口,
 * 支撑复杂的多槽位布局和动态槽位显隐机制, 并集成了书架物品检索与排序功能.
 * <p> 主要功能包括:
 * <ul>
 *   <li> 管理法术, 水晶, 中心工作台, 升级和书架等多种内置槽位类型 </li>
 *   <li> 基于中心物品动态计算周边法术槽位的环形布局位置 </li>
 *   <li> 通过书架物品处理器实现外部书架内容的聚合查询和排序 (按法术等级排序)</li>
 *   <li> 支持法术绑定和应用按钮触发绑定事件 </li>
 *   <li> 提供书架内容滚动浏览和槽位委托绑定机制 </li>
 * </ul>
 * <p> 设计上不直接处理请求, 而是通过委托槽位和物品处理器抽象物品存储访问, 避免对底层基础设施的直接关注.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.07.08
 * @since 1.0.0
 */
public class ArcaneWorkbenchMenu extends AbstractContainerMenu implements ISpellSortable {
    public static final Identifier EMPTY_SLOT_CRYSTAL = Identifier.fromNamespaceAndPath(Wizardry.MODID, "container/empty_slot_crystal");
    public static final Identifier EMPTY_SLOT_UPGRADE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "container/empty_slot_upgrade");
    public static final int CRYSTAL_SLOT = 8;
    public static final int CENTRE_SLOT = 9;
    public static final int UPGRADE_SLOT = 10;
    public static final int SLOT_RADIUS = 42;
    public static final int BOOKSHELF_SLOTS_X = 5;
    public static final int BOOKSHELF_SLOTS_Y = 10;
    public static final int BOOKSHELF_UI_WIDTH = 122;
    private final ContainerLevelAccess access;
    private final ISpellSortable.SortType sortType = SortType.TIER;
    private final List<BookshelfItemHandler> bookshelfItemHandlers = new ArrayList<>();
    private boolean sortDescending = false;
    private String searchText = "";
    private final Map<EmbeddedSlot, Long> inventoryInfos = new LinkedHashMap<>();
    private List<BookshelfSlotInfo> bookshelf = new ArrayList<>();
    public ArcaneWorkbenchBlockEntity blockEntity;
    private final DataSlot flags = DataSlot.standalone();
    private final List<DelegateSlot> activeBookshelfSlots = new ArrayList<>();
    private int scroll = 0;
    public final Player player;

    public ArcaneWorkbenchMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(
                containerId,
                playerInventory,
                (ArcaneWorkbenchBlockEntity) playerInventory.player.level().getBlockEntity(pos),
                ContainerLevelAccess.create(playerInventory.player.level(), pos)
        );
    }

    public ArcaneWorkbenchMenu(int containerId, @NonNull Inventory playerInventory, ArcaneWorkbenchBlockEntity blockEntity, ContainerLevelAccess access) {
        super(WizardryMenus.ARCANE_WORKBENCH.get(), containerId);
        this.blockEntity = blockEntity;
        this.access = access;
        this.player = playerInventory.player;
        this.addDataSlot(flags);
        this.packInventoryInfoIterator(EmbeddedSlot.SPELL_SLOT, this::addSlot, fillFunction -> {
            for (int i = 0; i < 8; i++) {
                fillFunction.apply(new ItemClassListSlot(
                        this.blockEntity.getItemHandler(), i,
                        -999, -999,
                        1, SpellBookItem.class)
                );
            }
        });
        this.packInventoryInfoIterator(EmbeddedSlot.CRYSTAL_SLOT, this::addSlot, fillFunction ->
                fillFunction.apply(new ItemListSlot(
                        blockEntity.getItemHandler(),
                        8, 13,
                        101, 64,
                        item -> item instanceof MagicCrystalItem || item == WizardryItems.CRYSTAL_SHARD.get() || item == WizardryItems.GRAND_CRYSTAL.get()
                )).setBackground(EMPTY_SLOT_CRYSTAL));
        this.packInventoryInfoIterator(EmbeddedSlot.CENTRE_SLOT, this::addSlot, fillFunction ->
                fillFunction.apply(new WorkbenchItemSlot(
                        blockEntity.getItemHandler(),
                        9,
                        80, 64,
                        itemStack -> {
                            if (itemStack.getItem() instanceof IWorkbenchItem iWorkbenchItem) {
                                int spellSlots = iWorkbenchItem.getSpellSlotCount(itemStack);
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
                        },
                        _ -> {
                            for (int i = 0; i < CRYSTAL_SLOT; i++) {
                                this.hideSlot(i, player);
                            }
                        }
                )));
        this.packInventoryInfoIterator(EmbeddedSlot.UPGRADE_SLOT, this::addSlot, fillFunction ->
                fillFunction.apply(new ItemListSlot(
                        blockEntity.getItemHandler(),
                        10, 147,
                        17, 1,
                        item -> item instanceof WandUpgradeItem
                                || item instanceof ArcaneTomeItem
                                || item instanceof ArmourUpgradeItem
                )).setBackground(EMPTY_SLOT_UPGRADE));
        this.packInventoryInfoIterator(EmbeddedSlot.PLAYER_QUICK_SLOT, this::addSlot, fillFunction -> {
            for (int k = 0; k < 9; ++k) {
                fillFunction.apply(new Slot(playerInventory, k, 8 + k * 18, 196));
            }
        });
        this.packInventoryInfoIterator(EmbeddedSlot.PLAYER_INVENTORY_SLOT, this::addSlot, fillFunction -> {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    fillFunction.apply(new Slot(playerInventory, j + i * 9 + 9
                            , 8 + j * 18, 138 + i * 18));
                }
            }
        });
        this.packInventoryInfoIterator(EmbeddedSlot.BOOKSHELF_SLOT, this::addSlot, fillFunction -> {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 5; j++) {
                    fillFunction.apply(new DelegateSlot(
                            j + i * 5 + 11,
                            -114 + j * 18, 34 + i * 18,
                            this::onSafeInsertEntrust,
                            this::onTakeEntrust
                    ));
                }
            }
        });
        refreshBookshelfSlots(this.player.level());
    }

    public ItemStack onSafeInsertEntrust(ItemStacksResourceHandler itemStacksResourceHandler, ItemStack inputStack, int inputAmount, int handlerIndex, int slotIndex) {
        int oneEmptyItemHandlerResource = -1;
        for (int i = 0; i < this.bookshelf.size(); i++) {
            BookshelfSlotInfo tempInfo = this.bookshelf.get(i);
            ItemResource itemResource = tempInfo.bookshelfItemHandler().getResource(tempInfo.activeIndex);
            if (itemResource.isEmpty()) {
                oneEmptyItemHandlerResource = i;
                break;
            }
        }
        ItemStack resultStack = inputStack.copy();
        if (oneEmptyItemHandlerResource != -1) {
            int changeValue = inputStack.getCount() - inputAmount;
            if (changeValue == 0) {
                BookshelfSlotInfo bookshelfSlotInfo = this.bookshelf.get(oneEmptyItemHandlerResource);
                bookshelfSlotInfo.bookshelfItemHandler().set(bookshelfSlotInfo.activeIndex, ItemResource.of(inputStack), inputAmount);
                resultStack = ItemStack.EMPTY;
            } else if (changeValue > 0) {
                BookshelfSlotInfo bookshelfSlotInfo = this.bookshelf.get(oneEmptyItemHandlerResource);
                bookshelfSlotInfo.bookshelfItemHandler().set(bookshelfSlotInfo.activeIndex, ItemResource.of(inputStack), inputAmount);
                resultStack.setCount(changeValue);
            }
        }
        this.updateActiveBookshelfSlots();
        return resultStack;
    }

    public void onTakeEntrust(ItemStacksResourceHandler itemStacksResourceHandler, ItemStack carried, int handlerIndex, int slotIndex) {
        BookshelfSlotInfo bookshelfSlotInfo = this.bookshelf.get(slotIndex - 47);
        bookshelfSlotInfo.bookshelfItemHandler().set(handlerIndex, ItemResource.EMPTY, 0);
        this.updateActiveBookshelfSlots();
    }

    public void refreshBookshelfSlots(@NotNull Level level) {
        bookshelfItemHandlers.clear();
        BookshelfBlock.findNearbyBookshelves(level, blockEntity.getBlockPos(), (itemResourceResourceHandler, blockEntity) -> {
            if (itemResourceResourceHandler instanceof BookshelfItemHandler bookshelfItemHandler) {
                this.bookshelfItemHandlers.add(bookshelfItemHandler);
            }
            return false;
        }, blockEntity);
        this.setHasBookshelves(!this.bookshelfItemHandlers.isEmpty());
        updateActiveBookshelfSlots();
    }

    public void updateActiveBookshelfSlots() {
        this.bookshelf.clear();
        List<BookshelfSlotInfo> emptyBookshelfSlotInfos = new ArrayList<>();
        for (BookshelfItemHandler bookshelfItemHandler : this.bookshelfItemHandlers) {
            for (int i = 0; i < bookshelfItemHandler.getStacksList().size(); i++) {
                if (!bookshelfItemHandler.getResource(i).isEmpty()) {
                    this.bookshelf.add(new BookshelfSlotInfo(i, bookshelfItemHandler));
                } else {
                    emptyBookshelfSlotInfos.add(new BookshelfSlotInfo(i, bookshelfItemHandler));
                }
            }
        }
        this.bookshelf = this.bookshelf
                .stream()
                .filter(bookshelfSlotInfo -> {
                    ItemStack stack = bookshelfSlotInfo.bookshelfItemHandler().getStack(bookshelfSlotInfo.activeIndex);
                    AbstractSpell spell = ItemStackHelper.getNotWandSpell(stack);
                    return spell != WizardrySpells.NONE.get() && spell.matches(searchText);
                })
                .sorted(
                        Comparator.comparing(
                                bookshelfSlotInfo -> {
                                    ItemStack stack = bookshelfSlotInfo.bookshelfItemHandler().getStack(bookshelfSlotInfo.activeIndex);
                                    return ItemStackHelper.getNotWandSpell(stack);
                                },
                                sortDescending ? sortType.comparator.reversed() : sortType.comparator
                        )
                )
                .collect(Collectors.toCollection(ArrayList::new));
        this.bookshelf.addAll(emptyBookshelfSlotInfos);
        List<BookshelfSlotInfo> visibleNotEmptySlots = this.bookshelf.subList(0, Math.clamp(this.bookshelf.size(), 0, 50));
        this.inventoryInfoIterator(EmbeddedSlot.BOOKSHELF_SLOT, visibleNotEmptySlots.size(), (index, slot) -> {
            if (slot instanceof DelegateSlot virtualSlot) {
                BookshelfSlotInfo visibleNotEmptySlot = visibleNotEmptySlots.get(index);
                virtualSlot.bind(visibleNotEmptySlot.bookshelfItemHandler(), visibleNotEmptySlot.activeIndex());
            }
        });
    }

    @Override
    public void initializeContents(int stateId, @NonNull List<ItemStack> items, @NonNull ItemStack carried) {
        super.initializeContents(stateId, items, carried);
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
                    WizardryAdvancementTriggers.ARCANE_WORKBENCH.get().trigger(serverPlayer, centre.getItem());
                }
                this.getSlot(CENTRE_SLOT).setChanged();
            }
        }
    }

    public void onClearButtonPressed(Player player) {
        Slot centre = this.getSlot(CENTRE_SLOT);
        if (centre.getItem().getItem() instanceof IWorkbenchItem iWorkbenchItem) {
            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);
            iWorkbenchItem.onClearButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks);
            this.getSlot(CENTRE_SLOT).setChanged();
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

    private void packInventoryInfo(EmbeddedSlot type, int startIndex, int stopIndex) {
        this.inventoryInfos.put(type, ((long) startIndex << 32) | ((long) stopIndex & 0xFFFFFFFFL));
    }

    /**
     * 将指定类型的槽位进行打包登记, 并使用给定的填充逻辑批量注册槽位
     * <p> 该方法通过一个填充消费者, 将一系列槽位添加到容器中, 并记录该类型槽位在容器中的索引范围
     * <p> 填充开始前会记录当前容器的槽位总数作为起始索引, 填充完成后将结束索引一并压缩保存到库存信息映射中
     *
     * @param type         需要记录的槽位类型, 用于后续区分不同功能区域的槽位 (如法术槽, 水晶槽等)
     * @param fillFunction 槽位添加函数, 用于在执行填充逻辑时将由填充消费者生成的槽位对象实际添加到容器中
     * @param fillConsumer 填充逻辑消费者, 接受槽位添加函数并在其内部实现具体的槽位创建与添加操作
     */
    public void packInventoryInfoIterator(EmbeddedSlot type, Function<Slot, Slot> fillFunction, @NonNull Consumer<Function<Slot, Slot>> fillConsumer) {
        int startIndex = this.slots.size();
        fillConsumer.accept(fillFunction);
        this.packInventoryInfo(type, startIndex, this.slots.size() - 1);
    }

    /**
     * 遍历指定类型槽位对应的库存信息, 并对每个槽位执行回调操作
     * <p> 从存储的压缩信息中解包出起始和结束索引, 然后依次获取槽位对象并调用消费者
     *
     * @param type     需要遍历的槽位类型, 用于查找对应的索引范围
     * @param consumer 槽位信息消费接口, 接收相对于起始索引的位置和槽位对象
     */
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

    /**
     * 遍历指定类型槽位对应的库存信息, 并对每个槽位执行回调操作, 限制最大遍历数量
     * <p> 从存储的压缩信息中解包出起始和结束索引, 然后根据最大遍历次数调整结束索引,
     * 最后依次获取槽位对象并调用消费者接口
     *
     * @param type             需要遍历的槽位类型, 用于查找对应的索引范围
     * @param maxIteratorCount 最大遍历的槽位数量
     * @param consumer         槽位信息消费接口, 接收相对于起始索引的位置和槽位对象
     */
    public void inventoryInfoIterator(EmbeddedSlot type, int maxIteratorCount, InventoryInfoConsumer consumer) {
        long packInventoryInfo = this.inventoryInfos.get(type);
        int startIndex = (int) (packInventoryInfo >> 32);
        int stopIndex = (int) packInventoryInfo;
        stopIndex = Math.min(stopIndex, maxIteratorCount + startIndex - 1);
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

    public List<DelegateSlot> getVisibleBookshelfSlots() {
        List<DelegateSlot> activeSlots = getActiveBookshelfSlots();
        return activeSlots.subList(BOOKSHELF_SLOTS_X * scroll, activeSlots.size());
    }

    public List<DelegateSlot> getActiveBookshelfSlots() {
        return activeBookshelfSlots;
    }

    public boolean hasBookshelves() {
        return (this.flags.get() & 1) != 0;
    }

    public void setHasBookshelves(boolean value) {
        if (value) {
            flags.set(flags.get() | 1);
        } else {
            flags.set(flags.get() & ~1);
        }
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
        return this.sortDescending;
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

    /**
     * 书籍架槽位信息记录
     * <p> 该数据类用于存储书籍架槽位的相关信息, 包括活动索引和槽位处理处理器.
     *
     * @param activeIndex          活动索引值, 表示当前展示的书本项的位置
     * @param bookshelfItemHandler 书籍项处理处理器接口, 负责处理书籍信息的操作
     * @author 秋海棠红
     * @version 1.0.0
     * @date 2026.07.08
     */
    public record BookshelfSlotInfo(
            int activeIndex,
            BookshelfItemHandler bookshelfItemHandler
    ) {
    }

    /**
     * 内嵌插槽枚举类
     * <p> 定义了游戏中不同类型的内嵌插槽, 包括法术插槽, 水晶插槽, 中心插槽, 升级插槽等,
     * 用于标识和管理游戏中的物理或逻辑位置.
     *
     * @author 秋海棠红
     * @version 1.0.0
     * @date 2026.07.08
     */
    public enum EmbeddedSlot {
        /**
         * 法术插槽
         */
        SPELL_SLOT,
        /**
         * 水晶插槽
         */
        CRYSTAL_SLOT,
        /**
         * 中心插槽
         */
        CENTRE_SLOT,
        /**
         * 升级插槽
         */
        UPGRADE_SLOT,
        /**
         * 快捷键插槽
         */
        PLAYER_QUICK_SLOT,
        /**
         * 玩家库存插槽, 用于标识玩家物品栏的位置.
         */
        PLAYER_INVENTORY_SLOT,
        /**
         * 书架插槽, 表示游戏中的书籍放置位置.
         */
        BOOKSHELF_SLOT
    }

    /**
     * 库存信息消费接口
     * <p> 定义了一个函数式接口用于消费库存数据, 包括相对索引和槽位的信息处理器.
     *
     * @author 秋海棠红
     * @version 1.0.0
     * @date 2026.07.08
     */
    @FunctionalInterface
    public interface InventoryInfoConsumer {
        void accept(int relativeIndex, Slot slot);
    }
}
