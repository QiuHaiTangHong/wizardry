package top.begonia.wizardry.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.render.entity.state.ImbuementAltarRenderState;
import top.begonia.wizardry.core.entity.block.ImbuementAltarBlockEntity;

import javax.annotation.Nullable;

public class ImbuementAltarRender implements BlockEntityRenderer<ImbuementAltarBlockEntity, ImbuementAltarRenderState> {
    private final ItemModelResolver itemModelResolver;

    public ImbuementAltarRender(BlockEntityRendererProvider.@NonNull Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public @NonNull ImbuementAltarRenderState createRenderState() {
        return new ImbuementAltarRenderState();
    }

    @Override
    public void extractRenderState(
            @NonNull ImbuementAltarBlockEntity blockEntity,
            @NonNull ImbuementAltarRenderState state,
            float partialTicks,
            @NonNull Vec3 cameraPosition,
            @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        if (blockEntity.getLevel() != null) {
            state.timer = (float) blockEntity.getLevel().getGameTime() + partialTicks;
        }
        ItemStack altarItem = blockEntity.getStack();
        if (!altarItem.isEmpty()) {
            this.itemModelResolver.updateForTopItem(
                    state.itemStackRenderState,
                    altarItem,
                    ItemDisplayContext.FIXED,
                    blockEntity.getLevel(),
                    null,
                    0
            );
        } else {
            state.itemStackRenderState.clear();
        }
    }

    @Override
    public void submit(
            @NonNull ImbuementAltarRenderState state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            @NonNull CameraRenderState cameraRenderState
    ) {
        if (state.itemStackRenderState.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.4F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        float floatOffset = 0.05F * Mth.sin(state.timer / 15.0F);
        poseStack.translate(0.0F, floatOffset, 0.0F);
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.timer));
        poseStack.scale(0.85F, 0.85F, 0.85F);
        state.itemStackRenderState.submit(
                poseStack,
                submitNodeCollector,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );
        poseStack.popPose();
        poseStack.popPose();
    }
}
