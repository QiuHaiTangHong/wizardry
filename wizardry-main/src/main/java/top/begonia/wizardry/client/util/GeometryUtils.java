package top.begonia.wizardry.client.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public final class GeometryUtils {
    private GeometryUtils() {
    }

    public static final double ANTI_Z_FIGHTING_OFFSET = 0.005;

    @Contract("_ -> new")
    public static @NonNull Vec3 getCentre(BlockPos pos) {
        return Vec3.atCenterOf(pos);
    }

    public static @NonNull Vec3 getCentre(@NonNull AABB box) {
        return box.getCenter();
    }

    @Contract("_ -> new")
    public static @NonNull Vec3 getCentre(@NonNull Entity entity) {
        return new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2.0D, entity.getZ());
    }

    public static @NonNull Vec3 getFaceCentre(BlockPos pos, @NonNull Direction face) {
        return getCentre(pos).add(
                face.getStepX() * 0.5D,
                face.getStepY() * 0.5D,
                face.getStepZ() * 0.5D
        );
    }

    public static double component(@NonNull Vec3 vec, Direction.@NonNull Axis axis) {
        return axis.choose(vec.x, vec.y, vec.z);
    }

    public static int component(@NonNull Vec3i vec, Direction.@NonNull Axis axis) {
        return axis.choose(vec.getX(), vec.getY(), vec.getZ());
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NonNull Vec3 replaceComponent(Vec3 vec, Direction.Axis axis, double newValue) {
        double x = (axis == Direction.Axis.X) ? newValue : vec.x;
        double y = (axis == Direction.Axis.Y) ? newValue : vec.y;
        double z = (axis == Direction.Axis.Z) ? newValue : vec.z;
        return new Vec3(x, y, z);
    }

    @Contract("_, _, _ -> new")
    public static @NonNull Vec3i replaceComponent(Vec3i vec, Direction.Axis axis, int newValue) {
        int x = (axis == Direction.Axis.X) ? newValue : vec.getX();
        int y = (axis == Direction.Axis.Y) ? newValue : vec.getY();
        int z = (axis == Direction.Axis.Z) ? newValue : vec.getZ();
        return new Vec3i(x, y, z);
    }

    public static @NonNull Vec3 horizontal(Vec3 vec) {
        return replaceComponent(vec, Direction.Axis.Y, 0).normalize();
    }

    @Contract("_ -> new")
    public static Vec3 @NonNull [] getVertices(@NonNull AABB box) {
        return new Vec3[]{
                new Vec3(box.minX, box.minY, box.minZ),
                new Vec3(box.maxX, box.minY, box.minZ),
                new Vec3(box.maxX, box.minY, box.maxZ),
                new Vec3(box.minX, box.minY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.maxY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.maxZ)
        };
    }

    public static Vec3 @NonNull [] getVertices(@NonNull Level level, BlockPos pos) {
        AABB box = level.getBlockState(pos).getShape(level, pos).bounds().move(pos);
        return getVertices(box);
    }

    public static float getPitch(Direction facing) {
        return facing == Direction.UP ? 90.0F : facing == Direction.DOWN ? -90.0F : 0.0F;
    }
}
