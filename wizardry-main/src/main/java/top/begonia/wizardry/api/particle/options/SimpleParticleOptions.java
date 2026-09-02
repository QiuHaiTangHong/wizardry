package top.begonia.wizardry.api.particle.options;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class SimpleParticleOptions implements IParticleOptionsExtension {
    private final ParticleType<SimpleParticleOptions> type;
    private final Vec3 acceleration = new Vec3(0.0f, 0.0f, 0.0f);

    public SimpleParticleOptions(ParticleType<SimpleParticleOptions> type) {
        this.type = type;
    }

    @Override
    public double xa() {
        return acceleration.x;
    }

    @Override
    public double ya() {
        return acceleration.y;
    }

    @Override
    public double za() {
        return acceleration.z;
    }

    @Override
    public @NonNull ParticleType<SimpleParticleOptions> getType() {
        return this.type;
    }
}
