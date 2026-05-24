package top.begonia.wizardry.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.render.entity.state.LecternRenderState;
import top.begonia.wizardry.core.entity.block.LecternBlockEntity;

import javax.annotation.Nullable;

public class LecternRender implements BlockEntityRenderer<LecternBlockEntity, LecternRenderState> {
    private static final Identifier BOOK_TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/lectern_book.png");
    private final BookModel modelBook;

    public LecternRender(BlockEntityRendererProvider.@NonNull Context context) {
        this.modelBook = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public @NonNull LecternRenderState createRenderState() {
        return new LecternRenderState();
    }

    @Override
    public void extractRenderState(
            @NonNull LecternBlockEntity blockEntity,
            @NonNull LecternRenderState state,
            float partialTicks,
            @NonNull Vec3 cameraPosition,
            @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.time = (float) blockEntity.ticksExisted + partialTicks;
        if (blockEntity.getLevel() != null) {
            state.angle = 90.0F - blockEntity.getLevel().getBlockState(blockEntity.getBlockPos()).getValue(HorizontalDirectionalBlock.FACING).toYRot();
        }
        state.spread = blockEntity.bookSpreadPrev + (blockEntity.bookSpread - blockEntity.bookSpreadPrev) * partialTicks;
        float leftPageFlipProgress = blockEntity.pageFlipPrev + (blockEntity.pageFlip - blockEntity.pageFlipPrev) * partialTicks + 0.25F;
        float rightPageFlipProgress = blockEntity.pageFlipPrev + (blockEntity.pageFlip - blockEntity.pageFlipPrev) * partialTicks + 0.75F;
        leftPageFlipProgress = (leftPageFlipProgress - (float) Mth.floor(leftPageFlipProgress)) * 1.6F - 0.3F;
        rightPageFlipProgress = (rightPageFlipProgress - (float) Mth.floor(rightPageFlipProgress)) * 1.6F - 0.3F;
        leftPageFlipProgress = Mth.clamp(leftPageFlipProgress, 0, 1);
        rightPageFlipProgress = Mth.clamp(rightPageFlipProgress, 0, 1);
        state.leftPageFlipProgress = leftPageFlipProgress;
        state.rightPageFlipProgress = rightPageFlipProgress;
    }

    @Override
    public void submit(
            @NonNull LecternRenderState state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            @NonNull CameraRenderState cameraRenderState
    ) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.angle));
        poseStack.translate(0.0F, 0.12F, 0.0F);
        if (state.spread > 0.3F) {
            poseStack.translate(0.0F, Mth.sin(state.time * 0.1F) * 0.01F, 0.0F);
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(112.5F));
        poseStack.translate(0.0F, 0.04F + (1.0F - state.spread) * 0.09F, (1.0F - state.spread) * -0.1875F);
        poseStack.mulPose(Axis.YP.rotationDegrees((1.0F - state.spread) * -90.0F));
        BookModel.State modelState = new BookModel.State(state.spread, state.leftPageFlipProgress, state.rightPageFlipProgress);
        submitNodeCollector.submitModel(
                this.modelBook,
                modelState,
                poseStack,
                BOOK_TEXTURE,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0,
                state.breakProgress
        );
        poseStack.popPose();
    }
}
