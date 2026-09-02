package top.begonia.wizardry.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.WizardryParticle;
import top.begonia.wizardry.client.renderer.RendererUtils;
import top.begonia.wizardry.api.particle.options.BeamParticleOptions;

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
    public RenderType renderType() {
        return RenderTypes.lightning();
    }

    @Override
    public void extractRenderState(
            @NonNull VertexConsumer vertexConsumer,
            @NonNull final Camera camera,
            @NotNull final CameraRenderState cameraRenderState,
            @NotNull final PoseStack.Pose pose,
            float partialTick
    ) {
        Matrix4f poseMatrix = pose.pose();
        Vec3 camPos = camera.position();
        double currentX = this.xo + (this.x - this.xo) * partialTick - camPos.x();
        double currentY = this.yo + (this.y - this.yo) * partialTick - camPos.y();
        double currentZ = this.zo + (this.z - this.zo) * partialTick - camPos.z();
        double endX = this.targetX - camPos.x();
        double endY = this.targetY - camPos.y();
        double endZ = this.targetZ - camPos.z();
        float scale = 1.0f;
        if (this.lifetime > 0) {
            float ageFraction = (this.age + partialTick - 1.0f) / (float) this.lifetime;
            ageFraction = Math.clamp(ageFraction, 0.0f, 1.0f);
        }
        float currentThickness = THICKNESS * scale;
        RendererUtils.drawSegment(poseMatrix, vertexConsumer, currentX, currentY, currentZ, endX, endY, endZ,
                0.25f * currentThickness, 1.0f, 1.0f, 1.0f, 1.0f);

        // Layer 1: 中间层（过渡色）
        RendererUtils.drawSegment(poseMatrix, vertexConsumer, currentX, currentY, currentZ, endX, endY, endZ,
                0.6f * currentThickness, (1.0f + 1.0f) / 2.0f, (1.0f + 1.0f) / 2.0f, (1.0f + 1.0f) / 2.0f, 0.65f);

        // Layer 2: 外层（主色调，最粗，高透明）
        RendererUtils.drawSegment(poseMatrix, vertexConsumer, currentX, currentY, currentZ, endX, endY, endZ,
                currentThickness, 1.0f, 1.0f, 1.0f, 0.3f);
    }
}
