package top.begonia.wizardry.core.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.constants.WizardryBlockStateProperties;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.client.gui.LecternScreen;
import top.begonia.wizardry.core.entity.block.LecternBlockEntity;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;
import top.begonia.wizardry.core.registry.WizardryParticles;

public class LecternBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public LecternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
        builder.add(WizardryBlockStateProperties.WOOD_TYPE);
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (level.isClientSide() && blockEntity instanceof LecternBlockEntity lecternBlockEntity) {
            Minecraft.getInstance().gui.setScreen(new LecternScreen(lecternBlockEntity));
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
    public @NonNull BlockState rotate(@NonNull BlockState state, @NonNull Rotation rotation){
        return state.setValue(
                BlockStateProperties.HORIZONTAL_FACING,
                rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING))
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    protected @NonNull BlockState mirror(@NonNull BlockState state, @NonNull Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
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

        if (entityplayer != null && level instanceof ClientLevel clientLevel) {
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    WizardryParticles.DUST.get(),
                    pos.getX() + random.nextFloat(), pos.getY() + 1, pos.getZ() + random.nextFloat()
            ).ifPresent(p -> p.speed(0.0f, 0.03f, 0.0f)
                    .color(1.0f, 1.0f, 0.65f)
                    .endColor(0.7f, 0.0f, 1.0f)
                    .shaded(false)
                    .spawn()
            );
        }
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, WizardryBlockEntities.LECTERN.get(), (lvl, pos, st, blockEntity) -> blockEntity.tick(lvl, pos, st));
    }
}
