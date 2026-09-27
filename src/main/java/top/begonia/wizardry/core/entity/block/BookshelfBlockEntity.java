package top.begonia.wizardry.core.entity.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.inventory.handler.BookshelfItemHandler;
import top.begonia.wizardry.core.inventory.menu.BookshelfMenu;
import top.begonia.wizardry.core.network.data.SyncAllSlotPayload;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;

public class BookshelfBlockEntity extends RandomizableContainerBlockEntity implements BlockEntityTicker<BookshelfBlockEntity> {
    /**
     * 货架随机物品生成距离
     */
    public static final int LOOT_GEN_DISTANCE = 32;
    /**
     * 货架内部库存槽数量
     */
    public static final int SLOT_COUNT = 12;
    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private final BookshelfItemHandler inventory = new BookshelfItemHandler(this, SLOT_COUNT);

    public BookshelfBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(WizardryBlockEntities.BOOKSHELF.get(), worldPosition, blockState);
    }

    public BookshelfItemHandler getInventory() {
        return this.inventory;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected @NonNull Component getDefaultName() {
        return Component.translatable("container." + Wizardry.MODID + ".bookshelf");
    }

    @Override
    public @NonNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public void setItems(@NonNull NonNullList<ItemStack> nonNullList) {
        this.items = nonNullList;
    }

    @Override
    protected @NonNull AbstractContainerMenu createMenu(int i, @NonNull Inventory inventory) {
        return new BookshelfMenu(
                i,
                inventory,
                this,
                this.level != null ? ContainerLevelAccess.create(this.level, this.worldPosition) : ContainerLevelAccess.NULL
        );
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items);
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items);
        }
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public void tick(
            @NonNull Level level,
            @NonNull BlockPos blockPos,
            @NonNull BlockState blockState,
            @NonNull BookshelfBlockEntity blockEntity
    ) {
        // 当玩家靠近时生成战利品，只会生成一次
        if (level instanceof ServerLevel) {
            if (this.lootTable != null) {
                Player player = level.getNearestPlayer(
                        blockPos.getX() + 0.5,
                        blockPos.getY() + 0.5,
                        blockPos.getZ() + 0.5,
                        LOOT_GEN_DISTANCE,
                        false
                );
                if (player instanceof ServerPlayer serverPlayer) {
                    this.unpackLootTable(player);
                    PacketDistributor.sendToPlayer(serverPlayer, new SyncAllSlotPayload(this.getItems(), blockPos));
                }
            }
        }
    }
}
