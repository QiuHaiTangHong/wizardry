package top.begonia.wizardry.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import top.begonia.wizardry.api.particle.WizardryParticle;
import top.begonia.wizardry.api.particle.options.BeamParticleOptions;
import top.begonia.wizardry.api.particle.renderer.state.MultipleQuadParticleRenderState;

public class BeamParticle extends WizardryParticle<BeamParticleOptions> {
    private static final float THICKNESS = 0.1f;
    private final double targetX;
    private final double targetY;
    private final double targetZ;

    public BeamParticle(
            ClientLevel level,
            BeamParticleOptions options,
            double x, double y, double z
    ) {
        super(level, options, x, y, z);
        this.targetX = options.targetX();
        this.targetY = options.targetY();
        this.targetZ = options.targetZ();
        this.lifetime = 10000;
    }

    @Override
    public void extract(MultipleQuadParticleRenderState quadParticleRenderState, Camera camera, float partialTickTime) {

    }
}
