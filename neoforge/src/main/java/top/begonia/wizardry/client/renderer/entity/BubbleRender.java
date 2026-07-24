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
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.renderer.entity.state.BubbleRenderState;
import top.begonia.wizardry.client.util.DrawingUtils;
import top.begonia.wizardry.core.entity.construct.BubbleEntity;

public class BubbleRender extends EntityRenderer<BubbleEntity, BubbleRenderState> {
    private static final Identifier PARTICLE_TEXTURES = Identifier.withDefaultNamespace("textures/particle/bubble.png");
    private static final Identifier ENTRAPMENT_TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/entrapment.png");

    public BubbleRender(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void submit(@NonNull BubbleRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        Identifier texture = state.isDarkOrb ? ENTRAPMENT_TEXTURE : PARTICLE_TEXTURES;
        float yaw = state.thirdPersonFront ? camera.xRot : -camera.xRot;
        int lightmapCoords = state.isDarkOrb ? LightCoordsUtil.FULL_BRIGHT : state.lightCoords;
        float scale = 3 * DrawingUtils.smoothScaleFactor(state.isDarkOrb ? state.lifetime : -1, state.tickCount, state.partialTick, 10, 10);
        float halfWidth = 0.5F;
        float halfHeight = 0.5F;
        float fullSize = 1.0F;
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(texture), (pose, vertexConsumer) -> {
            pose.translate(0, state.yOffset, 0);
            pose.rotate(Axis.YP.rotationDegrees(180.0F - camera.yRot));
            pose.rotate(Axis.XP.rotationDegrees(yaw));
            pose.scale(scale, scale, scale);
            vertexConsumer.addVertex(pose, 0.0F - halfHeight, 0.0F - halfWidth, 0.0F)
                    .setUv(0, 1)
                    .setColor(255, 255, 255, 255)
                    .setLight(lightmapCoords)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, fullSize - halfHeight, 0.0F - halfWidth, 0.0F)
                    .setUv(1, 1)
                    .setColor(255, 255, 255, 255)
                    .setLight(lightmapCoords)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, fullSize - halfHeight, 1.0F - halfWidth, 0.0F)
                    .setUv(1, 0)
                    .setColor(255, 255, 255, 255)
                    .setLight(lightmapCoords)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, 0.0F - halfHeight, 1.0F - halfWidth, 0.0F)
                    .setUv(0, 0)
                    .setColor(255, 255, 255, 255)
                    .setLight(lightmapCoords)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
        });
    }

    @Override
    public @NonNull BubbleRenderState createRenderState() {
        return new BubbleRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BubbleEntity entity, @NonNull BubbleRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isDarkOrb = entity.isDarkOrb();
        Entity firstPassengerEntity = entity.getFirstPassenger();
        if (firstPassengerEntity != null) {
            state.yOffset = firstPassengerEntity.getBbHeight() / 2;
        } else {
            state.yOffset = 0;
        }
        state.thirdPersonFront = Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT;
        state.lifetime = entity.getLifetime();
        state.tickCount = entity.tickCount;
    }
}
