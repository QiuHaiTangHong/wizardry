package top.begonia.wizardry.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.render.state.ArcaneWorkbenchRenderState;
import top.begonia.wizardry.common.entity.block.ArcaneWorkbenchBlockEntity;
import top.begonia.wizardry.common.inventory.menu.ArcaneWorkbenchMenu;

import javax.annotation.Nullable;

public class ArcaneWorkbenchRender implements BlockEntityRenderer<ArcaneWorkbenchBlockEntity, ArcaneWorkbenchRenderState> {

    private static final Identifier RUNE_TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/arcane_workbench_rune.png");
    private final ItemModelResolver itemModelResolver;

    public ArcaneWorkbenchRender(BlockEntityRendererProvider.@NonNull Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public @NonNull ArcaneWorkbenchRenderState createRenderState() {
        return new ArcaneWorkbenchRenderState();
    }

    @Override
    public void extractRenderState(@NonNull ArcaneWorkbenchBlockEntity blockEntity, @NonNull ArcaneWorkbenchRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.timer = blockEntity.timer + partialTicks;
        state.blockPos = blockEntity.getBlockPos();
        ItemStack stack = blockEntity.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        if (!stack.isEmpty()) {
            this.itemModelResolver.updateForTopItem(
                    state.wandItemRenderState,
                    stack,
                    ItemDisplayContext.FIXED,
                    blockEntity.getLevel(),
                    null,
                    0
            );
        } else {
            state.wandItemRenderState.clear();
        }
    }

    @Override
    public void submit(@NonNull ArcaneWorkbenchRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        if (state.wandItemRenderState.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        double dx = cameraRenderState.pos.x - (state.blockPos.getX() + 0.5D);
        double dz = cameraRenderState.pos.z - (state.blockPos.getZ() + 0.5D);
        float viewAngle = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI));
        this.renderEffect(state, poseStack, submitNodeCollector);
        this.renderWand(state, viewAngle, poseStack, submitNodeCollector);
        poseStack.popPose();
    }

    private void renderEffect(ArcaneWorkbenchRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.wandItemRenderState.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.timer));
        poseStack.translate(0.0f, 0.65f, 0.0f);
        RenderType renderType = RenderTypes.entityTranslucentEmissive(RUNE_TEXTURE);
        collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) -> {
            Matrix4f matrix = pose.pose();
            float s = 0.5f;
            consumer.addVertex(matrix, -s, 0, -s)
                    .setColor(255, 255, 255, 255)
                    .setUv(0, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(15728880)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, s, 0, -s)
                    .setColor(255, 255, 255, 255)
                    .setUv(1, 0)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(15728880)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, s, 0, s)
                    .setColor(255, 255, 255, 255)
                    .setUv(1, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(15728880)
                    .setNormal(0, 1, 0);
            consumer.addVertex(matrix, -s, 0, s)
                    .setColor(255, 255, 255, 255)
                    .setUv(0, 1)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(15728880)
                    .setNormal(0, 1, 0);
        });
        poseStack.popPose();
    }

    private void renderWand(@NonNull ArcaneWorkbenchRenderState state, float viewAngle, @NonNull PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.wandItemRenderState.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(viewAngle - 90.0F));
        float floatOffset = 0.05F * Mth.sin(state.timer / 15.0F);
        poseStack.translate(0.0F, 0.0F, 0.56F + floatOffset);
        poseStack.scale(0.75F, 0.75F, 0.75F);
        state.wandItemRenderState.submit(
                poseStack,
                collector,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );
        poseStack.popPose();
    }
}
