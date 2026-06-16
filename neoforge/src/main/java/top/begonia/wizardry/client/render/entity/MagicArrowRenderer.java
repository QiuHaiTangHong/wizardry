package top.begonia.wizardry.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.entity.projectile.MagicArrowEntity;

import javax.swing.*;

public class MagicArrowRenderer extends ArrowRenderer<MagicArrowEntity, ArrowRenderState> {
    private final Identifier texture;
    private final boolean blend;
    private final boolean renderEnds;
    private final double length;
    private final double width;
    private final int pixelsLong;
    private final int pixelsWide;

    public MagicArrowRenderer(
            EntityRendererProvider.Context context,
            Identifier texture,
            boolean blend,
            double length,
            double width,
            int pixelsLong,
            int pixelsWide,
            boolean renderEnds
    ) {
        super(context);
        this.texture = texture;
        this.blend = blend;
        this.length = length;
        this.width = width;
        this.pixelsLong = pixelsLong;
        this.pixelsWide = pixelsWide;
        this.renderEnds = renderEnds;
    }

    @Override
    public @NonNull ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }

    public void submit(@NonNull ArrowRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot));
        if (state.shake > 0.0F) {
            float shakeRot = -(float) Math.sin(state.shake * 3.0F) * state.shake;
            poseStack.mulPose(Axis.ZP.rotationDegrees(shakeRot));
        }
        RenderType targetRenderType = this.blend
                ? RenderTypes.entityTranslucentEmissive(this.texture)
                : RenderTypes.entityCutout(this.texture);
        int magicLight = 240 | 240 << 16;
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                targetRenderType,
                (pose, vertexConsumer) -> {
                    float sideMinU = 0.0F;
                    float sideMaxU = pixelsLong / 32.0F;
                    float sideMinV = 0.0F;
                    float sideMaxV = pixelsWide / 32.0F;
                    float endMinU = 0.0F;
                    float endMaxU = 0.15625F;
                    float endMinV = (float) 5 / 32.0F;
                    float endMaxV = (float) 10 / 32.0F;

                    if (renderEnds) {
                        addMagicVertex(vertexConsumer, pose, -7.0D, -width, -width, endMinU, endMinV, magicLight, 1.0F, 0.0F, 0.0F);
                        addMagicVertex(vertexConsumer, pose, -7.0D, -width, width, endMaxU, endMinV, magicLight, 1.0F, 0.0F, 0.0F);
                        addMagicVertex(vertexConsumer, pose, -7.0D, width, width, endMaxU, endMaxV, magicLight, 1.0F, 0.0F, 0.0F);
                        addMagicVertex(vertexConsumer, pose, -7.0D, width, -width, endMinU, endMaxV, magicLight, 1.0F, 0.0F, 0.0F);

                        addMagicVertex(vertexConsumer, pose, -7.0D, width, -width, endMinU, endMinV, magicLight, -1.0F, 0.0F, 0.0F);
                        addMagicVertex(vertexConsumer, pose, -7.0D, width, width, endMaxU, endMinV, magicLight, -1.0F, 0.0F, 0.0F);
                        addMagicVertex(vertexConsumer, pose, -7.0D, -width, width, endMaxU, endMaxV, magicLight, -1.0F, 0.0F, 0.0F);
                        addMagicVertex(vertexConsumer, pose, -7.0D, -width, -width, endMinU, endMaxV, magicLight, -1.0F, 0.0F, 0.0F);
                    }

                    for (int i = 0; i < 4; ++i) {
                        double angle = Math.toRadians(i * 90.0);
                        float sin = (float) Math.sin(angle);

                        float nx = 0.0F;
                        float nz = (float) Math.cos(angle);
                        float ny = -(float) Math.sin(angle);

                        double y1 = -width * nz;
                        double z1 = -width * sin;
                        double y2 = width * nz;
                        double z2 = width * sin;

                        addMagicVertex(vertexConsumer, pose, -length, y1, z1, sideMinU, sideMinV, magicLight, nx, ny, nz);
                        addMagicVertex(vertexConsumer, pose, length, y1, z1, sideMaxU, sideMinV, magicLight, nx, ny, nz);
                        addMagicVertex(vertexConsumer, pose, length, y2, z2, sideMaxU, sideMaxV, magicLight, nx, ny, nz);
                        addMagicVertex(vertexConsumer, pose, -length, y2, z2, sideMinU, sideMaxV, magicLight, nx, ny, nz);
                    }
                }
        );

        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    private void addMagicVertex(@NonNull VertexConsumer consumer, PoseStack.Pose pose, double x, double y, double z, float u, float v, int light, float nx, float ny, float nz) {
        consumer.addVertex(pose, (float) x, (float) y, (float) z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }

    @Override
    protected @NonNull Identifier getTextureLocation(@NonNull ArrowRenderState arrowRenderState) {
        return this.texture;
    }
}
