package top.begonia.wizardry.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.entity.block.BookshelfBlockEntity;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;

import java.util.List;

public class BookshelfBlock extends BaseEntityBlock {
    public static final MapCodec<BookshelfBlock> CODEC = simpleCodec(BookshelfBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WoodTypeEnum> BOOKSHELF_WOOD_TYPE = EnumProperty.create("wood_type", WoodTypeEnum.class);

    public BookshelfBlock(Properties properties) {
        super(properties);
    }

    public static @NonNull @Unmodifiable List<BookshelfBlockEntity> findNearbyBookshelves(
            @NonNull Level level,
            @NonNull BlockPos centre,
            BlockEntity... exclude
    ) {
        int radius = CommonConfig.bookshelfSearchRadius;
        List<BlockEntity> excludedList = List.of(exclude);
        return BlockPos.betweenClosedStream(centre.offset(-radius, -radius, -radius), centre.offset(radius, radius, radius))
                .map(level::getBlockEntity)
                .filter(blockEntity -> blockEntity instanceof BookshelfBlockEntity)
                .map(blockEntity -> (BookshelfBlockEntity) blockEntity)
                .filter(bookshelfBlockEntity -> !excludedList.contains(bookshelfBlockEntity))
                .toList();
    }

    @Override
    public BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(BOOKSHELF_WOOD_TYPE);
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
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return WizardryBlockEntities.BOOKSHELF.get().create(blockPos, blockState);
    }
}
