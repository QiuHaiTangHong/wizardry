package top.begonia.wizardry.client.particle.ray;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.options.RayParticleOptions;
import top.begonia.wizardry.api.particle.renderer.state.CompositeQuadParticleRenderState;
import top.begonia.wizardry.api.particle.extension.Layer;

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
    protected void extractSurface(
            CompositeQuadParticleRenderState state,
            Layer layer,
            @NonNull Camera camera,
            float partialTick,
            float lerpX, float lerpY, float lerpZ,
            Quaternionf rotation,
            float scale,
            float u0, float u1,
            float v0, float v1,
            int color, int lightCoords
    ) {
    }
}
