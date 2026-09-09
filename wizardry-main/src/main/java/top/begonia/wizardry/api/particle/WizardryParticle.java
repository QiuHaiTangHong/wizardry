package top.begonia.wizardry.api.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleOptions;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.renderer.state.MultipleQuadParticleRenderState;

public abstract class WizardryParticle<T extends IParticleOptionsExtension> extends Particle {
    public static final ParticleRenderType CUSTOM = new ParticleRenderType("wizardry:custom", "WC");
    public ParticleOptions options;

    public WizardryParticle(
            ClientLevel level,
            @NonNull T options,
            double x, double y, double z
    ) {
        super(level, x, y, z, options.xa(), options.ya(), options.za());
    }

    public abstract void extract(MultipleQuadParticleRenderState quadParticleRenderState, Camera camera, float partialTickTime);

    @Override
    public @NonNull ParticleRenderType getGroup() {
        return WizardryParticle.CUSTOM;
    }
}
