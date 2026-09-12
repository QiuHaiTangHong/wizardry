package top.begonia.wizardry.client.particle.quad;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.impl.OneQuadParticle;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.api.particle.renderer.state.CompositeQuadParticleRenderState;

public class SparkleParticle extends OneQuadParticle {
    public SparkleParticle(ClientLevel level, @NonNull QuadParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
        this.lifetime = 48 + this.random.nextInt(12);
        this.quadSize *= 0.75f;
        this.gravity = 0;
        this.hasPhysics = false;
        this.shaded = false;
    }

    @Override
    protected void extractSurface(
            @NonNull CompositeQuadParticleRenderState state,
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
        this.alpha(1 - ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime);
        super.extractSurface(
                state,
                layer,
                camera,
                partialTick,
                lerpX, lerpY, lerpZ,
                rotation,
                scale,
                u0, u1,
                v0, v1,
                ARGB.colorFromFloat(this.alpha, this.currentRed, this.currentGreen, this.currentBlue), lightCoords
        );
    }
}
