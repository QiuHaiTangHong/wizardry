package top.begonia.wizardry.core.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;
import top.begonia.wizardry.client.util.GeometryUtils;
import top.begonia.wizardry.client.util.ParticleBuilder;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.entity.block.ReceptacleBlockEntity;
import top.begonia.wizardry.core.item.impl.SpectralDustItem;
import top.begonia.wizardry.core.registry.*;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class ReceptacleBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final MapCodec<ReceptacleBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    propertiesCodec()
            ).apply(instance, ReceptacleBlock::new)
    );
    protected static final VoxelShape STANDING_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    protected static final VoxelShape NORTH_WALL_SHAPE = Block.box(4.0, 2.0, 7.0, 12.0, 10.0, 16.0);
    protected static final VoxelShape SOUTH_WALL_SHAPE = Block.box(4.0, 2.0, 0.0, 12.0, 10.0, 9.0);
    protected static final VoxelShape WEST_WALL_SHAPE = Block.box(7.0, 2.0, 4.0, 16.0, 10.0, 12.0);
    protected static final VoxelShape EAST_WALL_SHAPE = Block.box(0.0, 2.0, 4.0, 9.0, 10.0, 12.0);
    private static final double WALL_PARTICLE_OFFSET = 3 / 16d;
    public static final Map<ElementEnum, int[]> PARTICLE_COLOURS = Collections.unmodifiableMap(
            new EnumMap<>(Map.of(
                    ElementEnum.MAGIC, new int[]{0xe4c7cd, 0xfeffbe, 0x9d2cf3},
                    ElementEnum.FIRE, new int[]{0xff9600, 0xfffe67, 0xd02700},
                    ElementEnum.ICE, new int[]{0xa3e8f4, 0xe9f9fc, 0x138397},
                    ElementEnum.LIGHTNING, new int[]{0x409ee1, 0xf5f0ff, 0x225474},
                    ElementEnum.NECROMANCY, new int[]{0xa811ce, 0xf575f5, 0x382366},
                    ElementEnum.EARTH, new int[]{0xa8f408, 0xc8ffb2, 0x795c28},
                    ElementEnum.SORCERY, new int[]{0x56e8e3, 0xe8fcfc, 0x16a64d},
                    ElementEnum.HEALING, new int[]{0xfff69e, 0xfffff6, 0xa18200}
            ))
    );

    public ReceptacleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST_WALL_SHAPE;
            case WEST -> WEST_WALL_SHAPE;
            case SOUTH -> SOUTH_WALL_SHAPE;
            case NORTH -> NORTH_WALL_SHAPE;
            default -> STANDING_SHAPE;
        };
    }

    @Override
    public int getLightEmission(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ReceptacleBlockEntity receptacle && receptacle.getElement() != null) {
            return super.getLightEmission(state, level, pos);
        }
        return 0;
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack itemStack, @NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        ItemStack stack = player.getItemInHand(hand);
        if (blockEntity instanceof ReceptacleBlockEntity receptacleBlockEntity) {
            ElementEnum currentElement = receptacleBlockEntity.getElement();
            if (currentElement == null) {
                ElementEnum element = stack.get(WizardryComponents.ELEMENT.get());
                if (stack.getItem() instanceof SpectralDustItem && element != null) {
                    receptacleBlockEntity.setElement(element);
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, WizardrySounds.BLOCK_RECEPTACLE_IGNITE.get(), SoundSource.BLOCKS, 0.7f, 0.7f);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ReceptacleBlockEntity receptacle) {
            ElementEnum currentElement = receptacle.getElement();
            if (currentElement != null) {
                if (!level.isClientSide()) {
                    ItemStack dust = new ItemStack(WizardryItems.SPECTRAL_DUST.get());
                    dust.set(WizardryComponents.ELEMENT.get(), currentElement);
                    receptacle.setElement(null);
                    ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                    if (heldItem.isEmpty()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, dust);
                        return InteractionResult.SUCCESS;
                    } else {
                        if (!player.getInventory().add(dust)) {
                            player.drop(dust, false);
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void animateTick(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull RandomSource rand) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ReceptacleBlockEntity receptacleBlockEntity) {
            ElementEnum element = receptacleBlockEntity.getElement();
            if (element != null) {
                Direction facing = state.getValue(FACING).getOpposite();
                Vec3 centre = GeometryUtils.getCentre(pos);
                if (facing.getAxis().isHorizontal()) {
                    centre = centre.add(facing.getUnitVec3().scale(WALL_PARTICLE_OFFSET)).add(0, 0.125, 0);
                }
                int[] colours = PARTICLE_COLOURS.get(element);
                ParticleBuilder.create(WizardryParticles.FLASH.get()).pos(centre).scale(0.35f).time(48).clr(colours[0]).spawn(level);
                double r = 0.12;
                for (int i = 0; i < 3; i++) {
                    double x = r * (rand.nextDouble() * 2 - 1);
                    double y = r * (rand.nextDouble() * 2 - 1);
                    double z = r * (rand.nextDouble() * 2 - 1);
                    ParticleBuilder.create(WizardryParticles.DUST.get()).pos(centre.x + x, centre.y + y, centre.z + z)
                            .vel(x * -0.03, 0.02, z * -0.03).time(24 + rand.nextInt(8)).clr(colours[1]).fade(colours[2]).spawn(level);
                }
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(@NonNull BlockPlaceContext context) {
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction clickedFace = context.getClickedFace();
        if (clickedFace != Direction.DOWN) {
            BlockState state = this.defaultBlockState().setValue(FACING, clickedFace);
            if (this.canSurvive(state, level, pos)) {
                return state;
            }
        }
        for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN) {
                BlockState state = this.defaultBlockState().setValue(FACING, direction);
                if (this.canSurvive(state, level, pos)) {
                    return state;
                }
            }
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NonNull Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @NonNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return WizardryBlockEntities.RECEPTACLE.get().create(blockPos, blockState);
    }
}
