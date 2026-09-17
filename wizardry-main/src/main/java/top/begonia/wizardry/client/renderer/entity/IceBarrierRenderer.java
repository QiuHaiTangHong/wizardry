package top.begonia.wizardry.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.model.entity.IceBarrierModel;
import top.begonia.wizardry.client.renderer.entity.state.IceBarrierRenderState;
import top.begonia.wizardry.client.util.EntityLayerLocations;
import top.begonia.wizardry.core.entity.construct.scaled.impl.IceBarrierEntity;

public class IceBarrierRenderer extends EntityRenderer<IceBarrierEntity, IceBarrierRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/ice_barrier.png");
    private final IceBarrierModel<IceBarrierRenderState> model;

    public IceBarrierRenderer(EntityRendererProvider.Context context, IceBarrierModel<IceBarrierRenderState> model) {
        super(context);
        this.model = model;
    }

    public IceBarrierRenderer(EntityRendererProvider.Context context) {
        this(context, new IceBarrierModel<>(context.bakeLayer(EntityLayerLocations.ICE_BARRIER_ENTITY)));
    }

    @Override
    public void extractRenderState(
            @NonNull IceBarrierEntity entity,
            @NonNull IceBarrierRenderState state,
            float partialTicks
    ) {
        super.extractRenderState(entity, state, partialTicks);
        state.sizeMultiplier = entity.getSizeMultiplier();
        state.yaw = entity.getYRot();
    }

    @Override
    public void submit(
            @NonNull IceBarrierRenderState state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            @NonNull CameraRenderState camera
    ) {
        poseStack.pushPose();
        poseStack.translate(0, state.boundingBoxHeight / 2, 0);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(180), 0F, 0F, 0F);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(state.yaw), 0F, 0F, 0F);
        poseStack.translate(0, -state.boundingBoxHeight / 2 - 0.3, 0);

        float scale = state.sizeMultiplier;
        poseStack.scale(scale, scale, scale);
        submitNodeCollector.submitModel(
                this.model,
                state,
                poseStack,
                RenderTypes.entityCutout(TEXTURE),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );
        poseStack.popPose();
    }

    @Override
    public @NonNull IceBarrierRenderState createRenderState() {
        return new IceBarrierRenderState();
    }
}
