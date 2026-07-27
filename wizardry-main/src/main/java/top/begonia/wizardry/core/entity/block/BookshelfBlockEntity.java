package top.begonia.wizardry.core.entity.block;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.inventory.handler.BookshelfItemHandler;
import top.begonia.wizardry.core.inventory.menu.BookshelfMenu;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;

import java.util.Collections;
import java.util.List;

public class BookshelfBlockEntity extends RandomizableContainerBlockEntity implements ITick {
    /**
     * 自然生成标识符 key
     */
    private static final String NATURAL_NBT_KEY = "NaturallyGenerated";
    /**
     * 货架随机物品生成距离
     */
    private static final int LOOT_GEN_DISTANCE = 32;
    /**
     * 货架内部库存槽数量
     */
    public static final int SLOT_COUNT = 12;
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
    protected @NonNull NonNullList<ItemStack> getItems() {
        return this.inventory.getStacksList();
    }

    @Override
    protected void setItems(@NonNull NonNullList<ItemStack> nonNullList) {
        this.inventory.setStacksList(nonNullList);
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
            ContainerHelper.saveAllItems(output, this.inventory.getStacksList());
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        List<ItemStack> stackList = this.inventory.getStacksList();
        Collections.fill(stackList, ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.inventory.getStacksList());
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
    public <T extends BlockEntity> void serverTick(@NonNull ServerLevel level, BlockPos pos, BlockState state, @NonNull T blockEntity) {
    }

    @Override
    public <T extends BlockEntity> void clientTick(@NonNull ClientLevel level, BlockPos pos, BlockState state, @NonNull T blockEntity) {
    }
}
