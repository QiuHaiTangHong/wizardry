package top.begonia.wizardry.client.particle.ray;

import net.minecraft.client.multiplayer.ClientLevel;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.extension.extract.ExtractFlow;
import top.begonia.wizardry.api.particle.options.RayParticleOptions;

public class BeamParticle extends CompositeQuadParticle<RayParticleOptions> {
    private static final float THICKNESS = 0.1f;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public BeamParticle(
            ClientLevel level,
            RayParticleOptions options,
            double x, double y, double z
    ) {
        super(level, options, x, y, z);
        this.targetX = options.targetX();
        this.targetY = options.targetY();
        this.targetZ = options.targetZ();
        this.lifetime = 0;
        this.quadSize = 1.0f;
    }

    @Override
    protected void extractSurface(ExtractFlow extractFlow) {

    }
}
