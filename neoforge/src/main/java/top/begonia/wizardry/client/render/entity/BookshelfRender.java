package top.begonia.wizardry.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.client.data.definition.bookshelf.BookshelfBookSettings;
import top.begonia.wizardry.client.data.definition.model.OnlyModelQuads;
import top.begonia.wizardry.client.render.entity.state.BookshelfRenderState;
import top.begonia.wizardry.core.entity.block.BookshelfBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class BookshelfRender implements BlockEntityRenderer<BookshelfBlockEntity, BookshelfRenderState> {

    public BookshelfRender(BlockEntityRendererProvider.@NonNull Context ignoredContext) {
    }

    @Override
    public @NonNull BookshelfRenderState createRenderState() {
        return new BookshelfRenderState();
    }

    @Override
    public void extractRenderState(@NonNull BookshelfBlockEntity blockEntity, @NonNull BookshelfRenderState state, float partialTicks, @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.clean();
        state.blockPos = blockEntity.getBlockPos();
        state.blockState = blockEntity.getBlockState();
        BookshelfBookSettings settings = WizardryClientDataManager.getInstance().getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "bookshelf_book_settings"), BookshelfBookSettings.class).orElse(null);
        BlockState blockState = blockEntity.getBlockState();
        Direction facing = Direction.NORTH;
        if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        } else if (blockState.hasProperty(BlockStateProperties.FACING)) {
            facing = blockState.getValue(BlockStateProperties.FACING);
        }
        state.facing = facing;
        for (int i = 0; i < state.displayTextures.size(); i++) {
            ItemStack itemStack = blockEntity.getInventory().getStack(i);
            if (!itemStack.isEmpty()) {
                state.bindTextureInSlot(itemStack, i, settings);
                OnlyModelQuads onlyModelQuads = WizardryClientDataManager.getInstance().getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "books" + i), OnlyModelQuads.class).orElse(null);
                state.bindBlockStateModelPartInSlot(onlyModelQuads, i);
            }
        }
    }

    @Override
    public void submit(@NonNull BookshelfRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState cameraRenderState) {
        if (state.blockState == null || state.blockPos == null) {
            return;
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        float yRot = state.facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        for (int i = 0; i < BookshelfBlockEntity.SLOT_COUNT; i++) {
            Identifier textureId = state.displayTextures.get(i);
            List<OnlyModelQuads.QuadGeometry> geometries = state.displayGeometries.get(i);
            if (textureId == null || geometries.isEmpty()) {
                continue;
            }
            RenderType renderType = RenderTypes.entityCutout(textureId);
            collector.submitCustomGeometry(poseStack, renderType, (pose, consumer) -> {
                Matrix4f matrix = pose.pose();
                for (OnlyModelQuads.QuadGeometry geometry : geometries) {
                    addVertexWithLongUV(consumer, matrix, geometry.position0(), geometry.packedUV0(), geometry.direction(), state.lightCoords);
                    addVertexWithLongUV(consumer, matrix, geometry.position1(), geometry.packedUV1(), geometry.direction(), state.lightCoords);
                    addVertexWithLongUV(consumer, matrix, geometry.position2(), geometry.packedUV2(), geometry.direction(), state.lightCoords);
                    addVertexWithLongUV(consumer, matrix, geometry.position3(), geometry.packedUV3(), geometry.direction(), state.lightCoords);
                }
            });
        }
        poseStack.popPose();
    }

    private void addVertexWithLongUV(
            @NonNull VertexConsumer consumer,
            Matrix4f matrix,
            @NonNull Vector3fc pos,
            long packedUV,
            @NonNull Direction dir,
            int light
    ) {
        float u = Float.intBitsToFloat((int) (packedUV >> 32));
        float v = Float.intBitsToFloat((int) (packedUV & 0xFFFFFFFFL));
        int nx = dir.getStepX();
        int ny = dir.getStepY();
        int nz = dir.getStepZ();
        consumer.addVertex(matrix, pos.x(), pos.y(), pos.z())
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(nx, ny, nz);
    }
}
