package top.begonia.wizardry.core.entity.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import javax.annotation.Nullable;

public class LecternBlockEntity extends BlockEntity {

    public static final double BOOK_OPEN_DISTANCE = 5;

    public int ticksExisted;
    public float pageFlip;
    public float pageFlipPrev;
    public float flipT;
    public float flipA;
    public float bookSpread;
    public float bookSpreadPrev;
    public AbstractSpell currentSpell = WizardrySpells.NONE.get();

    public LecternBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(WizardryBlockEntities.LECTERN.get(), worldPosition, blockState);
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {

        this.bookSpreadPrev = this.bookSpread;
        Player entityplayer = null;
        if (this.level != null) {
            entityplayer = this.level.getNearestPlayer(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5,
                    this.getBlockPos().getZ() + 0.5, BOOK_OPEN_DISTANCE, false);
        }
        if (entityplayer != null) {

            this.bookSpread += 0.1f;

            if (this.bookSpread < 0.5f || level.getRandom().nextInt(40) == 0) {
                float f1 = this.flipT;
                while (f1 == flipT) this.flipT += (float) (level.getRandom().nextInt(4) - level.getRandom().nextInt(4));
            }

        } else {
            this.bookSpread -= 0.1f;
        }

        this.bookSpread = Mth.clamp(this.bookSpread, 0.0f, 1.0f);

        this.ticksExisted++;

        this.pageFlipPrev = this.pageFlip;
        float f = (this.flipT - this.pageFlip) * 0.4f;
        f = Mth.clamp(f, -0.2f, 0.2f);
        this.flipA += (f - this.flipA) * 0.9f;
        this.pageFlip += this.flipA;

    }

    public void sync() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            this.setChanged();
        }
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
