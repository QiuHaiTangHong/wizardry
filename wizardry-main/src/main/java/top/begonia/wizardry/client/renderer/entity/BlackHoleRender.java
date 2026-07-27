package top.begonia.wizardry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.renderer.entity.state.BlackHoleRenderState;
import top.begonia.wizardry.client.util.DrawingUtils;
import top.begonia.wizardry.core.entity.construct.BlackHoleEntity;

import java.util.ArrayList;
import java.util.Collections;

public class BlackHoleRender extends EntityRenderer<BlackHoleEntity, BlackHoleRenderState> {
    private static final Identifier RAY_TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID,
            "textures/entity/black_hole/ray.png");
    private static final Identifier CENTRE_TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID,
            "textures/entity/black_hole/centre.png");

    public BlackHoleRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(@NonNull BlackHoleRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        float scale = DrawingUtils.smoothScaleFactor(state.lifetime, state.ticksCount, state.partialTick, 10, 10);
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(RAY_TEXTURE), (pose, vertexConsumer) -> {
            pose.scale(scale, scale, scale);
            for (RayData ray : state.rays) {
                vertexConsumer.addVertex(pose, 0, 0, 0)
                        .setUv(0, 0)
                        .setColor(255, 255, 255, 255)
                        .setLight(LightCoordsUtil.FULL_BRIGHT)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setNormal(0.0F, 0.0F, 1.0F);
                vertexConsumer.addVertex(pose, 0, 0, 0)
                        .setUv(0, 1)
                        .setColor(255, 255, 255, 255)
                        .setLight(LightCoordsUtil.FULL_BRIGHT)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setNormal(0.0F, 0.0F, 1.0F);
                vertexConsumer.addVertex(pose, (float) ray.x1, (float) ray.y1, (float) ray.z1)
                        .setUv(1, 0)
                        .setColor(255, 255, 255, 255)
                        .setLight(LightCoordsUtil.FULL_BRIGHT)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setNormal(0.0F, 0.0F, 1.0F);
                vertexConsumer.addVertex(pose, (float) ray.x2, (float) ray.y2, (float) ray.z2)
                        .setUv(1, 1)
                        .setColor(255, 255, 255, 255)
                        .setLight(LightCoordsUtil.FULL_BRIGHT)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setNormal(0.0F, 0.0F, 1.0F);
            }
        });
        float yaw = state.thirdPersonFront ? camera.xRot : -camera.xRot;
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(CENTRE_TEXTURE), (pose, vertexConsumer) -> {
            pose.rotate(Axis.YP.rotationDegrees(180.0F - camera.yRot));
            pose.rotate(Axis.XP.rotationDegrees(yaw));
            vertexConsumer.addVertex(pose, -0.4F, 0.4F, 0.0F)
                    .setUv(0, 0)
                    .setColor(255, 255, 255, 255)
                    .setLight(LightCoordsUtil.FULL_BRIGHT)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, -0.4F, -0.4F, 0.0F)
                    .setUv(0, 1)
                    .setColor(255, 255, 255, 255)
                    .setLight(LightCoordsUtil.FULL_BRIGHT)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, 0.4F, 0.4F, 0.0F)
                    .setUv(1, 0)
                    .setColor(255, 255, 255, 255)
                    .setLight(LightCoordsUtil.FULL_BRIGHT)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, 0.4F, -0.4F, 0.0F)
                    .setUv(1, 1)
                    .setColor(255, 255, 255, 255)
                    .setLight(LightCoordsUtil.FULL_BRIGHT)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
        });
    }

    @Override
    public @NonNull BlackHoleRenderState createRenderState() {
        return new BlackHoleRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BlackHoleEntity entity, @NonNull BlackHoleRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.randomiser = entity.randomiser;
        state.randomiser2 = entity.randomiser2;
        state.ticksCount = entity.tickCount;
        state.lifetime = entity.getLifetime();
        state.thirdPersonFront = Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT;
        ArrayList<RayData> rays = new ArrayList<>(1);
        for (int j = 0; j < 30; j++) {
            float radius = 3.0f * entity.getEntitySizeMultiplier();
            int a = entity.randomiser[j];
            int b = entity.randomiser2[j];

            int sliceAngle = 20 + a;

            double x1 = radius * Mth.sin((entity.tickCount + 40 * j) * ((float) Math.PI / 180f));
            double z1 = radius * Mth.cos((entity.tickCount + 40 * j) * ((float) Math.PI / 180));

            double x2 = radius * Mth.sin((entity.tickCount + 40 * j - sliceAngle) * ((float) Math.PI / 180));
            double z2 = radius * Mth.cos((entity.tickCount + 40 * j - sliceAngle) * ((float) Math.PI / 180));

            double absoluteX = x1 * Mth.cos(31 * b);
            double absoluteY = z1 * Mth.sin(31 * a) + x1 * Mth.cos(31 * a) * Mth.sin(31 * b);
            double absoluteZ = z1 * Mth.cos(31 * a);

            double absoluteX2 = x2 * Mth.cos(31 * b);
            double absoluteY2 = z2 * Mth.sin(31 * a) + x2 * Mth.cos(31 * a) * Mth.sin(31 * b);
            double absoluteZ2 = z2 * Mth.cos(31 * a);
            rays.add(new RayData(j, absoluteX, absoluteY, absoluteZ, absoluteX2, absoluteY2, absoluteZ2, entity.getX(), entity.getY(), entity.getZ()));

        }
        Collections.sort(rays);
        state.rays = rays;
    }

    public record RayData(
            int ordinal,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            double offsetX, double offsetY, double offsetZ
    ) implements Comparable<RayData> {
        public double getDistanceFromViewpoint() {
            double midX = (x1 + x2) / 2;
            double midY = (y1 + y2) / 2;
            double midZ = (z1 + z2) / 2;

            double absoluteX = offsetX + midX;
            double absoluteY = offsetY + midY;
            double absoluteZ = offsetZ + midZ;

            return Math.sqrt(absoluteX * absoluteX + absoluteY * absoluteY + absoluteZ * absoluteZ);
        }

        @Override
        public int compareTo(@NonNull RayData other) {
            return Double.compare(other.getDistanceFromViewpoint(), this.getDistanceFromViewpoint());
        }
    }
}
