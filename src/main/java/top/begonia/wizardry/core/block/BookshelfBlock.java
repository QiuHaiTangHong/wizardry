package top.begonia.wizardry.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.constants.WizardryBlockStateProperties;
import top.begonia.wizardry.core.entity.block.BookshelfBlockEntity;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;

public class BookshelfBlock extends BaseEntityBlock {
    public BookshelfBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NonNull BlockState rotate(@NonNull BlockState state, @NonNull Rotation rotation) {
        return state.setValue(
                BlockStateProperties.FACING,
                rotation.rotate(state.getValue(BlockStateProperties.FACING))
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NonNull BlockState mirror(@NonNull BlockState state, @NonNull Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING)
                .add(WizardryBlockStateProperties.WOOD_TYPE);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BookshelfBlockEntity bookshelfBlockEntity) {
            player.openMenu(bookshelfBlockEntity, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return WizardryBlockEntities.BOOKSHELF.get().create(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> type) {
        return BaseEntityBlock.createTickerHelper(
                type,
                WizardryBlockEntities.BOOKSHELF.get(),
                (tempLevel, blockPos, blockState, blockEntity) ->
                        blockEntity.tick(tempLevel, blockPos, blockState, blockEntity)
        );
    }
}
