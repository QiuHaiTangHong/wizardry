package top.begonia.wizardry.common.entity.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.inventory.menu.ArcaneWorkbenchMenu;
import top.begonia.wizardry.common.inventory.handler.ArcaneWorkbenchItemHandler;
import top.begonia.wizardry.common.registry.WizardryBlockEntities;

public class ArcaneWorkbenchBlockEntity extends BlockEntity implements MenuProvider {
    public final ArcaneWorkbenchItemHandler inventory = new ArcaneWorkbenchItemHandler(this, 11);
    public float timer = 0;

    public ArcaneWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(WizardryBlockEntities.ARCANE_WORKBENCH.get(), pos, state);
    }

    public void sync() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            this.setChanged();
        }
    }

    @Override
    public @NonNull Component getDisplayName() {
        return Component.translatable("container." + Wizardry.MODID + ".arcane_workbench");
    }

    public void tick() {
        if (this.level != null && this.level.isClientSide()) {
            timer++;
        }
    }

    public ItemStack getItem(int slot) {
        return inventory.getStack(slot);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, @NonNull Inventory inventory, @NonNull Player player) {
        return new ArcaneWorkbenchMenu(i, inventory, this, this.level != null ? ContainerLevelAccess.create(this.level, this.worldPosition) : ContainerLevelAccess.NULL);
    }
}
