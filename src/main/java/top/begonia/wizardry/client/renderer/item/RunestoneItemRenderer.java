package top.begonia.wizardry.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.ARGB;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.renderer.WizardryRenderTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class RunestoneItemRenderer implements SpecialModelRenderer<RunestoneItemRenderer.State> {
    public RunestoneItemRenderer() {
    }

    @Override
    public void submit(
            @Nullable State state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector collector,
            int lightCoords,
            int overlayCoords,
            boolean hasGlint,
            int outlineColor
    ) {
        if (state == null) {
            return;
        }
        if (state.baseQuads() != null && !state.baseQuads().isEmpty()) {
            RenderType doubleLayerBase = WizardryRenderTypes.doubleLayerBase(
                    state.baseQuads().getFirst().materialInfo().sprite().atlasLocation()
            );
            collector.order(0).submitCustomGeometry(poseStack, doubleLayerBase, (pose, consumer) -> {
                QuadInstance instance = new QuadInstance();
                instance.setLightCoords(lightCoords);
                instance.setOverlayCoords(overlayCoords);
                for (BakedQuad quad : state.baseQuads()) {
                    consumer.putBakedQuad(pose, quad, instance);
                }
            });
        }
        if (state.overlayQuads() != null && !state.overlayQuads().isEmpty()) {
            RenderType doubleLayerOverly = WizardryRenderTypes.doubleLayerOverly(
                    state.overlayQuads().getFirst().materialInfo().sprite().atlasLocation()
            );
            collector.order(1).submitCustomGeometry(poseStack, doubleLayerOverly, (pose, consumer) -> {
                QuadInstance instance = new QuadInstance();
                instance.setLightCoords(LightCoordsUtil.FULL_BRIGHT);
                instance.setColor(-1);
                // 调整顶点色的明度以调亮叠加层的颜色，参见assets/wizardry/shaders/core/item.fsh
                instance.scaleColor(1.2f);
                instance.setOverlayCoords(overlayCoords);
                for (BakedQuad quad : state.overlayQuads()) {
                    consumer.putBakedQuad(pose, quad, instance);
                }
            });
        }
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    @Override
    public @Nullable State extractArgument(@NonNull ItemStack itemStack) {
        return null;
    }

    public record State(
            List<BakedQuad> baseQuads,
            List<BakedQuad> overlayQuads,
            ItemDisplayContext displayContext
    ) {
    }
}
