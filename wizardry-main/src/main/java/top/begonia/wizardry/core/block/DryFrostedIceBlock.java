package top.begonia.wizardry.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class DryFrostedIceBlock extends FrostedIceBlock {

    public DryFrostedIceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
        if (random.nextInt(3) == 0) {
            this.tick(state, level, pos, random);
        }
    }

    @Override
    protected void tick(@NonNull BlockState state, @NonNull ServerLevel level, @NonNull BlockPos pos, @NonNull RandomSource random) {
    }

    @Override
    protected void melt(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos) {
        level.destroyBlock(pos, false);
    }
}
