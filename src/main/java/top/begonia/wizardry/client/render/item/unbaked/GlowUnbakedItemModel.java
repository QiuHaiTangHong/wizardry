package top.begonia.wizardry.client.render.item.unbaked;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.render.item.SpecialItemRenderer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record GlowUnbakedItemModel(Identifier modelId) implements ItemModel.Unbaked {

    public static final MapCodec<GlowUnbakedItemModel> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("model").forGetter(GlowUnbakedItemModel::modelId)
            ).apply(instance, GlowUnbakedItemModel::new)
    );

    @Override
    public @NonNull MapCodec<? extends ItemModel.Unbaked> type() {
        return MAP_CODEC;
    }

    @Override
    public void resolveDependencies(ResolvableModel.@NonNull Resolver resolver) {
        resolver.markDependency(this.modelId);
    }

    @Override
    public @NonNull ItemModel bake(ItemModel.@NonNull BakingContext context, @NonNull Matrix4fc parentTransform) {
        ModelBaker baker = context.blockModelBaker();
        ResolvedModel resolvedModel = baker.getModel(this.modelId);
        TextureSlots slots = resolvedModel.getTopTextureSlots();
        QuadCollection bakedGeometry = resolvedModel.bakeTopGeometry(slots, baker, BlockModelRotation.IDENTITY);
        ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, slots);
        return new SpecialItemModel(bakedGeometry, properties, parentTransform);
    }

    public static class SpecialItemModel implements ItemModel {
        private final QuadCollection bakedModel;
        private final List<BakedQuad> quads;
        private final Supplier<Vector3fc[]> extents; // 🟢 使用与 MissingItemModel 一模一样的惰性加载 Supplier 包装
        private final ModelRenderProperties properties;
        private final Matrix4fc transform;

        public SpecialItemModel(@NonNull QuadCollection bakedModel, ModelRenderProperties properties, Matrix4fc transform) {
            this.bakedModel = bakedModel;
            this.quads = bakedModel.getAll();
            this.extents = Suppliers.memoize(() -> CuboidItemModelWrapper.computeExtents(this.quads));
            this.properties = properties;
            this.transform = transform;
        }

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
                    SpecialItemRenderer.INSTANCE,
                    new SpecialItemRenderer.State(baseQuads, overlayQuads, displayContext)
            );
        }
    }
}
