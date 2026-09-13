package top.begonia.wizardry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.model.entity.HammerModel;
import top.begonia.wizardry.client.renderer.entity.state.HammerRenderState;
import top.begonia.wizardry.client.util.EntityLayerLocations;
import top.begonia.wizardry.core.entity.construct.HammerEntity;

public class HammerRenderer extends EntityRenderer<HammerEntity, HammerRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/lightning_hammer.png");
    private final HammerModel<HammerRenderState> model;

    public HammerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new HammerModel<>(context.bakeLayer(EntityLayerLocations.HAMMER_ENTITY));
    }

    @Override
    public void submit(
            @NonNull HammerRenderState state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            @NonNull CameraRenderState camera
    ) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 1.5F, 0.0F);
        poseStack.rotateAround(
                Axis.ZP.rotationDegrees(180.0F),
                0.0F, 0.0F, 0.0F
        );
        poseStack.rotateAround(
                Axis.YP.rotationDegrees(state.yaw),
                0.0F, 0.0F, 0.0F
        );
        poseStack.rotateAround(
                Axis.ZP.rotationDegrees(state.prevRotationPitch + (state.rotationPitch - state.prevRotationPitch) * state.partialTick),
                0.0F, 0.0F, 0.0F
        );
        RenderType renderType = this.model.renderType(TEXTURE);
        submitNodeCollector.submitModel(
                this.model,
                state,
                poseStack,
                renderType,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public @NonNull HammerRenderState createRenderState() {
        return new HammerRenderState();
    }

    @Override
    public void extractRenderState(
            @NonNull HammerEntity entity,
            @NonNull HammerRenderState state,
            float partialTicks
    ) {
        super.extractRenderState(entity, state, partialTicks);
        state.prevRotationPitch = entity.xRotO;
        state.rotationPitch = entity.getXRot();
        state.yaw = entity.getYRot();
    }
}
