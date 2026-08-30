package top.begonia.wizardry.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import org.jspecify.annotations.NonNull;

public class QuadMeshParticle extends Particle {
    public QuadMeshParticle(
            ClientLevel level,
            double x, double y, double z,
            double xa, double ya, double za
    ) {
        super(level, x, y, z, xa, ya, za);
    }

    public QuadMeshParticle(
            ClientLevel level,
            double x, double y, double z
    ) {
        super(level, x, y, z);
    }

    @Override
    public @NonNull ParticleRenderType getGroup() {
        return null;
    }
}
