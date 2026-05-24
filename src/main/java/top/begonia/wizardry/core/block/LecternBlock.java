package top.begonia.wizardry.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.gui.LecternScreen;
import top.begonia.wizardry.client.util.ParticleBuilder;
import top.begonia.wizardry.core.entity.block.LecternBlockEntity;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;
import top.begonia.wizardry.core.registry.WizardryParticles;

public class LecternBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<WoodTypeEnum> LECTERN_WOOD_TYPE = EnumProperty.create("wood_type", WoodTypeEnum.class);
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    public static final MapCodec<LecternBlock> CODEC = simpleCodec(LecternBlock::new);

    public LecternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(LECTERN_WOOD_TYPE);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (level.isClientSide() && blockEntity instanceof LecternBlockEntity lecternBlockEntity) {
            Minecraft.getInstance().setScreen(new LecternScreen(lecternBlockEntity));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return WizardryBlockEntities.LECTERN.get().create(pos, state);
    }

    @Override
    public void animateTick(
            @NonNull BlockState state,
            @NonNull Level level,
            @NonNull BlockPos pos,
            @NonNull RandomSource random
    ) {
        Player entityplayer = level.getNearestPlayer(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                LecternBlockEntity.BOOK_OPEN_DISTANCE,
                false
        );

        if (entityplayer != null) {
            ParticleBuilder.create(WizardryParticles.DUST.get()).pos(pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat())
                    .vel(0, 0.03, 0).clr(1, 1, 0.65f).fade(0.7f, 0, 1).shaded(false).spawn(level);
        }
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, WizardryBlockEntities.LECTERN.get(), (lvl, pos, st, blockEntity) -> blockEntity.tick(lvl, pos, st));
    }
}
