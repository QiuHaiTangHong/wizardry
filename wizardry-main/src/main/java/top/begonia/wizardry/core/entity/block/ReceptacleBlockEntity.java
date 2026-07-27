package top.begonia.wizardry.core.entity.block;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;

import javax.annotation.Nullable;

public class ReceptacleBlockEntity extends BlockEntity {
    private ElementEnum element;

    public ReceptacleBlockEntity(BlockPos pos, BlockState state) {
        super(WizardryBlockEntities.RECEPTACLE.get(), pos, state);
    }

    @Nullable
    public ElementEnum getElement() {
        return element;
    }

    public void setElement(@Nullable ElementEnum element) {
        this.element = element;
        this.setChanged();
        if (this.level != null) {
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
            if (!this.level.isClientSide()) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        input.read("Element", Codec.INT).ifPresentOrElse(
                index -> this.element = (index == -1) ? null : ElementEnum.values()[index],
                () -> this.element = null
        );
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Element", this.element == null ? -1 : this.element.ordinal());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Element", this.element == null ? -1 : this.element.ordinal());
        return tag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
