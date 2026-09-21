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
        double length = extractFlow.getAdditionalData("length", Double.class);
        Vec3 startPos = extractFlow.getVec3Position();
        Vec3 targetDirectionVec = extractFlow.getAdditionalData("targetDirectionVec", Vec3.class);
        Vec3 endPos = targetDirectionVec.scale(length).add(startPos);
        for (int layer = 0; layer < 3; layer++) {
            drawSegment(extractFlow, layer,
                    startPos,
                    endPos,
                    scale * THICKNESS
            );
        }
    }

    private void drawSegment(
            @NonNull ExtractFlow extractFlow,
            int layer,
            Vec3 startPos,
            Vec3 endPos,
            float thickness
    ) {
        float red = extractFlow.getRed();
        float green = extractFlow.getGreen();
        float blue = extractFlow.getBlue();
        switch (layer) {
            case 0:
                drawShearedBox(extractFlow, startPos, endPos, 0.25f * thickness, 1, 1, 1, 1);
                break;
            case 1:
                drawShearedBox(extractFlow, startPos, endPos, 0.6f * thickness, (red + 1) / 2, (green + 1) / 2,
                        (blue + 1) / 2, 0.65f);
                break;
            case 2:
                drawShearedBox(extractFlow, startPos, endPos, thickness, red, green, blue, 0.3f);
                break;
        }
    }

    private void drawShearedBox(
            @NonNull ExtractFlow extractFlow,
            Vec3 startPos,
            Vec3 endPos,
            float width,
            float r, float g, float b, float a
    ) {
        int color = ARGB.colorFromFloat(a, r, g, b);
        int light = LightCoordsUtil.FULL_BRIGHT;
        Vec3 direction = endPos.subtract(startPos).normalize();
        Vec3 up = new Vec3(0, 1, 0);
        if (Math.abs(direction.dot(up)) > 0.999) {
            up = new Vec3(1, 0, 0);
        }
        Vec3 right = direction.cross(up).normalize();
        up = right.cross(direction).normalize();
        Vec3 s1 = startPos
                .subtract(right.scale(width))
                .subtract(up.scale(width));
        Vec3 e1 = endPos
                .subtract(right.scale(width))
                .subtract(up.scale(width));
        Vec3 s2 = startPos
                .subtract(right.scale(width))
                .add(up.scale(width));
        Vec3 e2 = endPos
                .subtract(right.scale(width))
                .add(up.scale(width));
        Vec3 s3 = startPos
                .add(right.scale(width))
                .add(up.scale(width));
        Vec3 e3 = endPos
                .add(right.scale(width))
                .add(up.scale(width));
        Vec3 s4 = startPos
                .add(right.scale(width))
                .subtract(up.scale(width));
        Vec3 e4 = endPos
                .add(right.scale(width))
                .subtract(up.scale(width));
        CompositeParticleRenderState state = extractFlow.getState();
        state.addNoTexTriangleStrip(
                        Layer.NO_TEX_TRANSLUCENT_TRIANGLE,
                        s1, e1, s2,
                        color,
                        light
                )
                .addNoTexVertexAttribute(e2, color, light)
                .addNoTexVertexAttribute(s3, color, light)
                .addNoTexVertexAttribute(e3, color, light)
                .addNoTexVertexAttribute(s4, color, light)
                .addNoTexVertexAttribute(e4, color, light)
                .addNoTexVertexAttribute(s1, color, light)
                .addNoTexVertexAttribute(e1, color, light);
    }
}
