package top.begonia.wizardry.client.particle.quad;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.impl.OneQuadParticle;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.api.particle.renderer.state.CompositeQuadParticleRenderState;

public class FlashParticle extends OneQuadParticle {
    public FlashParticle(ClientLevel level, @NonNull QuadParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
        this.currentColor(1.0f, 1.0f, 1.0f);
        this.startColor(1.0f, 1.0f, 1.0f);
        this.endColor(1.0f, 1.0f, 1.0f);
        this.quadSize = 0.6f;
        this.lifetime = 6;
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
        this.setAlpha(0.6F - ((float) this.age + partialTick - 1.0F) / this.lifetime * 0.5F);
        int finalColor = ARGB.colorFromFloat(this.alpha, this.currentRed, this.currentGreen, this.currentBlue);
        float finalScale = scale * Mth.sin(((float) this.age + partialTick - 1.0F) / this.lifetime * (float) Math.PI);
        super.extractSurface(
                state,
                layer,
                camera,
                partialTick,
                lerpX, lerpY, lerpZ,
                rotation,
                finalScale,
                u0, u1,
                v0, v1,
                finalColor, lightCoords
        );
    }
}
