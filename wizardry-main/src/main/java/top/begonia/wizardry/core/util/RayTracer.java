package top.begonia.wizardry.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.entity.ICustomHitbox;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class RayTracer {

    private RayTracer() {
    }

    public static @NonNull BlockHitResult standardBlockRayTrace(
            @NonNull Level level, LivingEntity entity,
            double range, boolean hitLiquids,
            boolean ignoreUncountables, boolean returnLastUndecidable
    ) {
        Vec3 origin = entity.getEyePosition(1.0F);
        Vec3 lookVec = entity.getLookAngle();
        Vec3 endpoint = origin.add(lookVec.scale(range));

        ClipContext.Block blockMode = ignoreUncountables
                ? ClipContext.Block.COLLIDER
                : ClipContext.Block.OUTLINE;
        ClipContext.Fluid fluidMode = hitLiquids
                ? ClipContext.Fluid.ANY
                : ClipContext.Fluid.NONE;
        ClipContext context = new ClipContext(origin, endpoint, blockMode, fluidMode, entity);
        BlockHitResult result = level.clip(context);
        if (returnLastUndecidable && result.getType() == HitResult.Type.MISS) {
            Vec3 currentPos = origin;
            BlockPos lastUndecidablePos = null;
            double step = 0.5;
            int steps = (int) (range / step);
            for (int i = 0; i < steps; i++) {
                currentPos = currentPos.add(lookVec.scale(step));
                BlockPos pos = BlockPos.containing(currentPos);
                if (lastUndecidablePos != null && lastUndecidablePos.equals(pos)) continue;
                BlockState state = level.getBlockState(pos);
                if (state.getCollisionShape(level, pos).isEmpty()) {
                    lastUndecidablePos = pos;
                } else {
                    break;
                }
            }
            if (lastUndecidablePos != null) {
                return new BlockHitResult(
                        Vec3.atCenterOf(lastUndecidablePos),
                        Direction.UP,
                        lastUndecidablePos,
                        false
                );
            }
        }
        return result;
    }

    @Contract(pure = true)
    public static @NonNull Predicate<Entity> ignoreEntityFilter(Entity entity) {
        return e -> e == entity || (e instanceof LivingEntity livingEntity && livingEntity.deathTime > 0);
    }

    public static @NonNull HitResult rayTrace(
            @NonNull Level level,
            Entity owner,
            Vec3 origin,
            Vec3 endpoint,
            float aimAssist,
            ClipContext.Block blockMode,
            ClipContext.Fluid fluidMode,
            Class<? extends Entity> entityType,
            Predicate<? super Entity> filter
    ) {
        BlockHitResult blockHitResult = level.clip(
                new ClipContext(
                        origin,
                        endpoint,
                        blockMode,
                        fluidMode,
                        owner
                )
        );
        if (blockHitResult.getType() != HitResult.Type.MISS) {
            endpoint = blockHitResult.getLocation();
        }
        float borderSize = 1 + aimAssist;
        AABB searchVolume = new AABB(origin, endpoint).inflate(borderSize);
        List<Entity> entities = level.getEntities(
                EntityTypeTest.forClass(Entity.class),
                searchVolume,
                entity -> entity != owner
                        && !entity.isSpectator()
                        && entityType.isInstance(entity)
                        && !filter.test(entity)
        );
        Entity closestHitEntity = null;
        Vec3 closestHitPosition = endpoint;
        AABB entityBounds;
        Vec3 intercept;
        double closestHitDistanceSqr = origin.distanceToSqr(endpoint);
        for (Entity entity : entities) {
            intercept = null;
            float fuzziness = entity instanceof LivingEntity ? aimAssist : 0;
            if (entity instanceof ICustomHitbox customHitbox) {
                intercept = customHitbox.calculateIntercept(origin, endpoint, fuzziness);
            } else {
                entityBounds = entity.getBoundingBox();
                float entityPickRadius = entity.getPickRadius();
                if (entityPickRadius != 0) {
                    entityBounds = entityBounds.inflate(entityPickRadius);
                }
                Optional<Vec3> hitResult = entityBounds.clip(origin, endpoint);
                if (hitResult.isPresent()) {
                    intercept = hitResult.get();
                }
            }
            if (intercept != null) {
                double currentHitDistanceSqr = intercept.distanceToSqr(origin);
                if (currentHitDistanceSqr < closestHitDistanceSqr) {
                    closestHitEntity = entity;
                    closestHitPosition = intercept;
                }
            }
        }
        if (closestHitEntity != null) {
            return new EntityHitResult(closestHitEntity, closestHitPosition);
        }
        return blockHitResult;
    }
}
