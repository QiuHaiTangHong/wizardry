package top.begonia.wizardry.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class LayerTiledOverlay<S extends LivingEntityRenderState, M extends EntityModel<S>> extends RenderLayer<S, M> {
    public LayerTiledOverlay(RenderLayerParent<S, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            @NonNull S state,
            float yRot,
            float xRot
    ) {
        poseStack.pushPose();
        poseStack.scale(1.0F, 1.0F, 1.0F);
        M model = this.getParentModel();
        RenderType renderType = model.renderType(Identifier.withDefaultNamespace("textures/block/diamond_block.png"));
        submitNodeCollector.submitModel(
                model,
                state,
                poseStack,
                renderType,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                -1,
                null,
                state.outlineColor,
                null
        );
        poseStack.popPose();
    }
}
