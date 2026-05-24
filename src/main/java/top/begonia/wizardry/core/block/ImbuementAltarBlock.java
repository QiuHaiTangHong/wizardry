package top.begonia.wizardry.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.entity.block.ImbuementAltarBlockEntity;
import top.begonia.wizardry.core.registry.WizardryBlockEntities;
import top.begonia.wizardry.core.registry.WizardryBlocks;

import javax.annotation.Nullable;

public class ImbuementAltarBlock extends BaseEntityBlock {
    public static final MapCodec<ImbuementAltarBlock> CODEC = simpleCodec(ImbuementAltarBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public ImbuementAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    public boolean checkStructureConditions(LevelReader level, BlockPos altarPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = altarPos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!neighborState.is(WizardryBlocks.RECEPTACLE.get())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    protected @NonNull BlockState updateShape(
            @NonNull BlockState state,
            @NonNull LevelReader level,
            @NonNull ScheduledTickAccess ticks,
            @NonNull BlockPos pos,
            @NonNull Direction directionToNeighbour,
            @NonNull BlockPos neighbourPos,
            @NonNull BlockState neighbourState,
            @NonNull RandomSource random
    ) {
        if (directionToNeighbour.getAxis().isHorizontal()) {
            boolean isPerfect = this.checkStructureConditions(level, pos);
            if (state.getValue(ACTIVE) != isPerfect) {
                return state.setValue(ACTIVE, isPerfect);
            }
        }
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (!(tileEntity instanceof ImbuementAltarBlockEntity altar) || player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        ItemStack currentStack = altar.getStack();
        ItemStack toInsert = player.getItemInHand(player.getUsedItemHand());
        if (currentStack.isEmpty()) {
            if (!toInsert.isEmpty()) {
                ItemStack stackCopy = toInsert.copy();
                stackCopy.setCount(1);
                altar.setStack(stackCopy);
                altar.setLastUser(player);
                if (!player.isCreative()) {
                    toInsert.shrink(1);
                }
            }
        } else {
            if (toInsert.isEmpty()) {
                player.setItemInHand(player.getUsedItemHand(), currentStack);
            } else if (!player.getInventory().add(currentStack)) {
                player.drop(currentStack, false);
            }

            altar.setStack(ItemStack.EMPTY);
            altar.setLastUser(null);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return WizardryBlockEntities.IMBUEMENT_ALTAR.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, WizardryBlockEntities.IMBUEMENT_ALTAR.get(), (lvl, pos, st, blockEntity) -> blockEntity.tick(lvl, pos, st));
    }
}