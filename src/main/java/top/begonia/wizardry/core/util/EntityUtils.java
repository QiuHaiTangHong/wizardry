package top.begonia.wizardry.core.util;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public final class EntityUtils {
    private EntityUtils() {
    }

    public static int getDefaultAimingError(Difficulty difficulty) {
        return switch (difficulty) {
            case NORMAL -> 6;
            case HARD -> 2;
            default -> 10;
        };
    }

    public static <T extends Entity> List<T> getEntitiesWithinRadius(double radius, double x, double y, double z, Level level, Class<T> entityType) {
        AABB aabb = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        List<T> entityList = new ArrayList<>(level.getEntitiesOfClass(entityType, aabb));
        double radiusSqr = radius * radius;
        entityList.removeIf(entity -> entity.distanceToSqr(x, y, z) > radiusSqr);

        return entityList;
    }

    public static List<LivingEntity> getLivingWithinRadius(double radius, double x, double y, double z, Level level) {
        return getEntitiesWithinRadius(radius, x, y, z, level, LivingEntity.class);
    }
}
