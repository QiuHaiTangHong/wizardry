package top.begonia.wizardry.api.particle.extension;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("UnusedReturnValue")
public interface ParticleInitAccessor {
    @NonNull ParticleInitAccessor scaleValue(float scale);

    ParticleInitAccessor time(int time);

    ParticleInitAccessor speed(double xd, double yd, double zd);

    default ParticleInitAccessor speed(@NonNull Vec3 vel){
        return this.speed(vel.x, vel.y, vel.z);
    }

    default ParticleInitAccessor color(int hex) {
        float r = ((hex & 0xFF0000) >> 16) / 255.0F;
        float g = ((hex & 0xFF00) >> 8) / 255.0F;
        float b = ((hex & 0xFF)) / 255.0F;
        this.startColor(r, g, b);
        this.currentColor(r, g, b);
        this.endColor(r, g, b);
        return this;
    }

    default ParticleInitAccessor color(float red, float green, float blue) {
        this.startColor(red, green, blue);
        this.currentColor(red, green, blue);
        this.endColor(red, green, blue);
        return this;
    }

    ParticleInitAccessor startColor(float red, float green, float blue);

    ParticleInitAccessor currentColor(float red, float green, float blue);

    ParticleInitAccessor endColor(float red, float green, float blue);

    default ParticleInitAccessor endColor(int hex) {
        float r = ((hex & 0xFF0000) >> 16) / 255.0F;
        float g = ((hex & 0xFF00) >> 8) / 255.0F;
        float b = ((hex & 0xFF)) / 255.0F;
        return this.endColor(r, g, b);
    }

    ParticleInitAccessor alpha(float alpha);

    ParticleInitAccessor shaded(boolean shaded);

    ParticleInitAccessor gravity(boolean gravity);

    ParticleInitAccessor spin(double radius, double speed);

    ParticleInitAccessor entity(Entity entity);

    ParticleInitAccessor facing(float yaw, float pitch);

    default ParticleInitAccessor facing(@NonNull Direction direction) {
        return this.facing(direction.toYRot(), direction.getAxis().isVertical() ? direction.getAxisDirection().getStep() * -90.0F : 0.0F);
    }

    ParticleInitAccessor targetPosition(double x, double y, double z);

    ParticleInitAccessor targetVelocity(double vx, double vy, double vz);

    ParticleInitAccessor targetEntity(Entity target);

    ParticleInitAccessor length(double length);

    ParticleInitAccessor physics(boolean hasPhysics);

    void spawn();
}
