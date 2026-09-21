package top.begonia.wizardry.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.util.GeometryUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class BlockUtils {
    private BlockUtils() {
    }

    public static @NonNull Stream<BlockPos> getBlockSphereStream(@NonNull BlockPos centre, double radius) {
        double radiusSqr = radius * radius;
        int intRadius = (int) radius;
        int cx = centre.getX();
        int cy = centre.getY();
        int cz = centre.getZ();

        return Stream.iterate(-intRadius, i -> i <= intRadius, i -> i + 1)
                .flatMap(i -> {
                    int iSqr = i * i;
                    double remainingSqr1 = radiusSqr - iSqr;
                    if (remainingSqr1 < 0) return Stream.empty();
                    int r1 = (int) Math.sqrt(remainingSqr1);

                    return Stream.iterate(-r1, j -> j <= r1, j -> j + 1)
                            .flatMap(j -> {
                                int jSqr = j * j;
                                double remainingSqr2 = remainingSqr1 - jSqr;
                                if (remainingSqr2 < 0) return Stream.empty();
                                int r2 = (int) Math.sqrt(remainingSqr2);
                                return Stream.iterate(-r2, k -> k <= r2, k -> k + 1)
                                        .map(k -> new BlockPos(cx + i, cy + j, cz + k));
                            });
                });
    }

    @Nullable
    public static BlockPos findNearbyFloorSpace(Entity entity, int horizontalRange, int verticalRange) {

        Level level = entity.level();
        BlockPos origin = entity.blockPosition();
        return findNearbyFloorSpace(level, origin, horizontalRange, verticalRange);
    }

    @Nullable
    public static BlockPos findNearbyFloorSpace(Level level, BlockPos origin, int horizontalRange, int verticalRange) {
        return findNearbyFloorSpace(level, origin, horizontalRange, verticalRange, true);
    }

    @Nullable
    public static BlockPos findNearbyFloorSpace(Level level, BlockPos origin, int horizontalRange, int verticalRange, boolean lineOfSight) {
        List<BlockPos> possibleLocations = new ArrayList<>();
        final Vec3 centre = GeometryUtils.getCentre(origin);
        for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int z = -horizontalRange; z <= horizontalRange; z++) {

                Integer y = getNearestFloor(level, origin.offset(x, 0, z), verticalRange);

                if (y != null) {
                    BlockPos location = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    if (lineOfSight) {
                        ClipContext context = new ClipContext(centre, Vec3.atBottomCenterOf(location),
                                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, CollisionContext.empty());
                        BlockHitResult hitResult = level.clip(context);
                        if (hitResult.getType() == HitResult.Type.BLOCK) {
                            continue;
                        }
                    }
                    possibleLocations.add(location);
                }
            }
        }

        if (possibleLocations.isEmpty()) {
            return null;
        } else {
            return possibleLocations.get(level.getRandom().nextInt(possibleLocations.size()));
        }
    }

    @Nullable
    public static Integer getNearestFloor(Level level, BlockPos pos, int range) {
        return getNearestSurface(level, pos, Direction.UP, range, true, SurfaceCriteria.COLLIDABLE);
    }

    @Nullable
    public static Integer getNearestSurface(
            Level world, BlockPos pos,
            Direction direction, int range,
            boolean doubleSided, SurfaceCriteria criteria
    ) {
        Integer surface = null;
        int currentBest = Integer.MAX_VALUE;
        for (int i = doubleSided ? -range : 0; i <= range && i < currentBest; i++) { // Now short-circuits for efficiency
            BlockPos testPos = pos.relative(direction, i);
            if (criteria.test(world, testPos, direction)) {
                surface = (int) GeometryUtils.component(GeometryUtils.getFaceCentre(testPos, direction), direction.getAxis());
                currentBest = Math.abs(i);
            }
        }

        return surface;
    }

    @FunctionalInterface
    public interface SurfaceCriteria {
        SurfaceCriteria COLLIDABLE = basedOn(state -> !state.canBeReplaced());

        SurfaceCriteria BUILDABLE = (level, pos, side) -> {
            boolean isSolid = level.getBlockState(pos).isFaceSturdy(level, pos, side);
            boolean isReplaceable = level.getBlockState(pos.relative(side)).canBeReplaced();
            return isSolid && isReplaceable;
        };

        SurfaceCriteria SOLID_LIQUID_TO_AIR = (level, pos, side) -> {
            BlockState state = level.getBlockState(pos);
            FluidState fluidState = state.getFluidState();
            boolean isLiquid = !fluidState.isEmpty();
            boolean isSolid = state.isFaceSturdy(level, pos, side);
            boolean isAirAbove = level.getBlockState(pos.relative(side)).isAir();
            return (isLiquid || isSolid) && isAirAbove;
        };

        SurfaceCriteria NOT_AIR_TO_AIR = basedOn((level, pos) -> !level.getBlockState(pos).isAir()).flip();

        SurfaceCriteria COLLIDABLE_IGNORING_TREES = basedOn((level, pos) -> {
            BlockState state = level.getBlockState(pos);
            boolean blocksMovement = !state.canBeReplaced();
            boolean isTree = state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
            return blocksMovement && !isTree;
        });

        static SurfaceCriteria basedOn(BiPredicate<Level, BlockPos> condition) {
            return (level, pos, side) -> condition.test(level, pos) && !condition.test(level, pos.relative(side));
        }

        static SurfaceCriteria basedOn(Predicate<BlockState> condition) {
            return (level, pos, side) -> condition.test(level.getBlockState(pos)) && !condition.test(level.getBlockState(pos.relative(side)));
        }

        default SurfaceCriteria flip() {
            return (level, pos, side) -> this.test(level, pos.relative(side), side.getOpposite());
        }

        boolean test(Level level, BlockPos pos, Direction side);

    }
}
