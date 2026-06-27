package top.begonia.wizardry.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static top.begonia.wizardry.client.util.RenderTypes.getBaseRenderType;
import static top.begonia.wizardry.client.util.RenderTypes.getOverlyRenderType;

public class EmissionModelItemRenderer implements SpecialModelRenderer<EmissionModelItemRenderer.State> {

    private static final EmissionModelItemRenderer INSTANCE = new EmissionModelItemRenderer();

    private EmissionModelItemRenderer() {
    }

    public static EmissionModelItemRenderer getInstance() {
        return INSTANCE;
    }

    public record State(List<BakedQuad> baseQuads, List<BakedQuad> overlayQuads, ItemDisplayContext displayContext) {
    }

    @Override
    public void submit(@Nullable State state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasGlint, int outlineColor) {
        if (state == null) return;
        if (state.baseQuads() != null && !state.baseQuads().isEmpty()) {
            RenderType glowType = getBaseRenderType(
                    state.baseQuads().getFirst().materialInfo().sprite().atlasLocation()
            );
            collector.order(0).submitCustomGeometry(poseStack, glowType, (pose, consumer) -> {
                QuadInstance instance = new QuadInstance();
                instance.setLightCoords(lightCoords);
                instance.setOverlayCoords(overlayCoords);
                for (BakedQuad quad : state.baseQuads()) {
                    consumer.putBakedQuad(pose, quad, instance);
                }
            });
        }
        if (state.overlayQuads() != null && !state.overlayQuads().isEmpty()) {
            RenderType glowType = getOverlyRenderType(
                    state.overlayQuads().getFirst().materialInfo().sprite().atlasLocation()
            );
            collector.order(1).submitCustomGeometry(poseStack, glowType, (pose, consumer) -> {
                QuadInstance instance = new QuadInstance();
                instance.setLightCoords(LightCoordsUtil.FULL_BRIGHT);
                instance.setOverlayCoords(overlayCoords);
                for (BakedQuad quad : state.overlayQuads()) {
                    consumer.putBakedQuad(pose, quad, instance);
                }
            });
        }
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> var1) {
        var1.accept(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    @Override
    public @Nullable State extractArgument(@NonNull ItemStack var1) {
        return null;
    }
}
