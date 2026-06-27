package top.begonia.wizardry.client.model.unbaked.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;
import org.jspecify.annotations.NonNull;

public record EmissionUnbakedItemModel(Identifier modelId) implements ItemModel.Unbaked {

    public static final MapCodec<EmissionUnbakedItemModel> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("model").forGetter(EmissionUnbakedItemModel::modelId)
            ).apply(instance, EmissionUnbakedItemModel::new)
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
        return new EmissionItemModel(
                bakedGeometry.getAll(),
                Suppliers.memoize(() -> CuboidItemModelWrapper.computeExtents(bakedGeometry.getAll())),
                properties,
                parentTransform
        );
    }
}
