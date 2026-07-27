package top.begonia.wizardry.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.config.ServerConfig;

import java.util.ArrayList;
import java.util.List;

public final class EntityUtils {
    private EntityUtils() {
    }

    @Contract(pure = true)
    public static int getDefaultAimingError(@NonNull Difficulty difficulty) {
        return switch (difficulty) {
            case NORMAL -> 6;
            case HARD -> 2;
            default -> 10;
        };
    }

    public static boolean attackEntityWithoutKnockback(ServerLevel serverLevel, @NonNull Entity entity, DamageSource source, float amount) {
        Vec3 prevDeltaMove = entity.getDeltaMovement();
        boolean succeeded = entity.hurtServer(serverLevel, source, amount);
        entity.setDeltaMovement(prevDeltaMove);
        return succeeded;
    }

    public static @NonNull List<LivingEntity> getLivingWithinRadius(double radius, double x, double y, double z, Level level) {
        return getEntitiesWithinRadius(radius, x, y, z, level, LivingEntity.class);
    }

    public static <T extends Entity> @NonNull List<T> getEntitiesWithinRadius(double radius, double x, double y, double z, @NonNull Level level, Class<T> entityType) {
        AABB aabb = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        List<T> entityList = new ArrayList<>(level.getEntitiesOfClass(entityType, aabb));
        double radiusSqr = radius * radius;
        entityList.removeIf(entity -> entity.distanceToSqr(x, y, z) > radiusSqr);

        return entityList;
    }

    public static boolean canDamageBlocks(@Nullable Entity entity, ServerLevel serverLevel) {
        if (entity == null) {
            return ServerConfig.dispenserBlockDamage;
        } else if (entity instanceof Player player) {
            return player.mayBuild() && ServerConfig.playerBlockDamage;
        }
        return EventHooks.canEntityGrief(serverLevel, entity);
    }

    public static boolean isBlockUnbreakable(@NonNull Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && state.getDestroySpeed(level, pos) < 0;
    }

    public static void undoGravity(@NonNull Entity entity) {
        if (!entity.isNoGravity()) {
            double gravity = 0.04;
            switch (entity) {
                case ThrowableProjectile _ -> gravity = 0.03;
                case AbstractArrow _ -> gravity = 0.05;
                case LivingEntity _ -> gravity = 0.08;
                default -> {
                }
            }
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x, motion.y + gravity, motion.z);
        }
    }
}
