package top.begonia.wizardry.client.particle.target;

import net.minecraft.client.multiplayer.ClientLevel;
import top.begonia.wizardry.api.particle.extension.extract.ExtractFlow;
import top.begonia.wizardry.api.particle.impl.CompositeTargetedParticle;
import top.begonia.wizardry.api.particle.options.impl.TargetParticleOptions;

public class BeamParticle extends CompositeTargetedParticle<TargetParticleOptions> {
    private static final float THICKNESS = 0.1f;

    public BeamParticle(
            ClientLevel level,
            TargetParticleOptions options,
            double x, double y, double z
    ) {
        super(level, options, x, y, z);
        this.lifetime = 0;
        this.quadSize = 1.0f;
    }

    @Override
    protected void extractSurface(ExtractFlow extractFlow) {
    }
}
