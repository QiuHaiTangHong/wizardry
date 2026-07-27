package top.begonia.wizardry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.renderer.entity.state.DecayRenderState;
import top.begonia.wizardry.client.util.DrawingUtils;
import top.begonia.wizardry.core.entity.construct.DecayEntity;

/**
 * 衰变实体的渲染器
 * <p> 负责将 {@link DecayEntity} 实例渲染为游戏中的平面纹理效果.
 * 该渲染器在初始化时预加载一组纹理资源, 并在每帧根据实体的生命周期和计时状态,
 * 动态绘制一个带有平滑缩放动画的四边形平面.
 * <p> 此类属于领域层内部实现, 封装了衰变特效的视觉表现逻辑,
 * 不直接处理网络请求或持久化等基础设施关注点.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @since 1.0.0
 */
public class DecayRender extends EntityRenderer<DecayEntity, DecayRenderState> {
    private static final Identifier[] TEXTURES = new Identifier[10];

    public DecayRender(EntityRendererProvider.Context context) {
        super(context);
        for (int i = 0; i < 10; i++) {
            TEXTURES[i] = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/decay/decay_" + i + ".png");
        }
        this.shadowRadius = 0.0F;
    }

    @Override
    public void submit(@NonNull DecayRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        Identifier texture = TEXTURES[state.textureIndex];
        float fullSize = 1.0F;
        float halfHeight = 0.5F;
        float halfWidth = 0.5F;
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucent(texture), (pose, vertexConsumer) -> {
            pose.rotate(new Quaternionf().rotationX((float) (Math.PI / 2)));
            float scale = 2 * DrawingUtils.smoothScaleFactor(state.lifetime, state.tickCount, state.partialTick, 10, 50);
            pose.scale(scale, scale, scale);
            vertexConsumer.addVertex(pose, 0.0F - halfHeight, 0.0F - halfWidth, -0.01F)
                    .setUv(0, 1)
                    .setColor(255, 255, 255, 255)
                    .setLight(state.lightCoords)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, fullSize - halfHeight, 0.0F - halfWidth, -0.01F)
                    .setUv(1, 1)
                    .setColor(255, 255, 255, 255)
                    .setLight(state.lightCoords)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, fullSize - halfHeight, 1.0F - halfWidth, -0.01F)
                    .setUv(1, 0)
                    .setColor(255, 255, 255, 255)
                    .setLight(state.lightCoords)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
            vertexConsumer.addVertex(pose, 0.0F - halfHeight, 1.0F - halfWidth, -0.01F)
                    .setUv(0, 0)
                    .setColor(255, 255, 255, 255)
                    .setLight(state.lightCoords)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setNormal(0.0F, 0.0F, 1.0F);
        });
    }

    @Override
    public @NonNull DecayRenderState createRenderState() {
        return new DecayRenderState();
    }

    @Override
    public void extractRenderState(@NonNull DecayEntity entity, @NonNull DecayRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.textureIndex = entity.textureIndex;
        state.lifetime = entity.getLifetime();
        state.tickCount = entity.tickCount;
    }
}
