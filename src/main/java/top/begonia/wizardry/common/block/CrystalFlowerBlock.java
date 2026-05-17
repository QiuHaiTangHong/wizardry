package top.begonia.wizardry.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

public class CrystalFlowerBlock extends BushBlock {

    protected static final VoxelShape SHAPE = Block.box(4.8D, 0.0D, 4.8D, 11.2D, 9.6D, 11.2D);

    public CrystalFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, RandomSource random) {
        if (random.nextBoolean()) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + (random.nextDouble() * 0.5D) + 0.5D;
            double z = pos.getZ() + random.nextDouble();
        }
    }
}
