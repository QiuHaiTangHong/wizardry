package top.begonia.wizardry.client.particle.target;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.extension.extract.ExtractFlow;
import top.begonia.wizardry.api.particle.impl.CompositeTargetedParticle;
import top.begonia.wizardry.api.particle.options.impl.TargetParticleOptions;
import top.begonia.wizardry.api.particle.renderer.CompositeParticleRenderState;

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
        this.length = 5f;
    }

    @Override
    protected void extractSurface(@NonNull ExtractFlow extractFlow) {
        float scale = extractFlow.getScale();
        if (extractFlow.getLifetime() > 0) {
            float ageFraction = (extractFlow.getAge() + extractFlow.getPartialTick() - 1) / extractFlow.getLifetime();
            // Squaring this makes it look smoother than a linear shrinking effect
            scale = scale * (1 - ageFraction * ageFraction);
        }
        Vec3 pos = extractFlow.getLinkEntity().getLookAngle();
        for (int layer = 0; layer < 3; layer++) {
            drawSegment(extractFlow, layer,
                    (float) pos.x, (float) pos.y, (float) pos.z,
                    (float) pos.x, (float) pos.y, (float) this.length,
                    scale * THICKNESS
            );
        }
    }

    private void drawSegment(
            @NonNull ExtractFlow extractFlow,
            int layer,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float thickness
    ) {
        float red = extractFlow.getRed();
        float green = extractFlow.getGreen();
        float blue = extractFlow.getBlue();
        switch (layer) {
            case 0:
                drawShearedBox(extractFlow, x1, y1, z1, x2, y2, z2, 0.25f * thickness, 1, 1, 1, 1);
                break;
            case 1:
                drawShearedBox(extractFlow, x1, y1, z1, x2, y2, z2, 0.6f * thickness, (red + 1) / 2, (green + 1) / 2,
                        (blue + 1) / 2, 0.65f);
                break;
            case 2:
                drawShearedBox(extractFlow, x1, y1, z1, x2, y2, z2, thickness, red, green, blue, 0.3f);
                break;
        }
    }

    private void drawShearedBox(
            @NonNull ExtractFlow extractFlow,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float width,
            float r, float g, float b, float a
    ) {
        int color = ARGB.colorFromFloat(a, r, g, b);
        int light = LightCoordsUtil.FULL_BRIGHT;
        CompositeParticleRenderState state = extractFlow.getState();
        state.addNoTexTriangleStrip(Layer.NO_TEX_TRANSLUCENT_TRIANGLE,
                        x1 - width, y1 - width, z1,
                        x2 - width, y2 - width, z2,
                        x1 - width, y1 + width, z1,
                        color,
                        light
                )
                .addNoTexVertexAttribute(x2 - width, y2 + width, z2, color, light)
                .addNoTexVertexAttribute(x1 + width, y1 + width, z1, color, light)
                .addNoTexVertexAttribute(x2 + width, y2 + width, z2, color, light)
                .addNoTexVertexAttribute(x1 + width, y1 - width, z1, color, light)
                .addNoTexVertexAttribute(x2 + width, y2 - width, z2, color, light)
                .addNoTexVertexAttribute(x1 - width, y1 - width, z1, color, light)
                .addNoTexVertexAttribute(x2 - width, y2 - width, z2, color, light);
    }
}
