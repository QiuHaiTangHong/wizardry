package top.begonia.wizardry.client.model.unbaked.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.render.item.EmissionModelItemRenderer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record EmissionItemModel(
        List<BakedQuad> quads,
        Supplier<Vector3fc[]> extents,
        ModelRenderProperties properties,
        Matrix4fc transform
) implements ItemModel {

    @Override
    public void update(@NonNull ItemStackRenderState state, @NonNull ItemStack stack, @NonNull ItemModelResolver resolver, @NonNull ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        state.appendModelIdentityElement(this);
        ItemStackRenderState.LayerRenderState layerState = state.newLayer();
        this.properties.applyToLayer(layerState, displayContext);
        List<BakedQuad> baseQuads = new ArrayList<>();
        List<BakedQuad> overlayQuads = new ArrayList<>();
        this.quads.forEach(bakedQuad -> {
            BakedQuad.MaterialInfo oldMaterialInfo = bakedQuad.materialInfo();
            Identifier textureIdentifier = oldMaterialInfo.sprite().contents().name();
            if (textureIdentifier.getPath().contains("overlay")) {
                overlayQuads.add(bakedQuad);
            } else {
                baseQuads.add(bakedQuad);
            }
        });
        layerState.setExtents(this.extents);
        layerState.setLocalTransform(this.transform);
        layerState.setUsesBlockLight(false);
        layerState.setupSpecialModel(
                EmissionModelItemRenderer.getInstance(),
                new EmissionModelItemRenderer.State(baseQuads, overlayQuads, displayContext)
        );
    }
}
