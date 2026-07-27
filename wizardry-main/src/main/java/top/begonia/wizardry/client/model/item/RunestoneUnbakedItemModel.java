package top.begonia.wizardry.client.model.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public record RunestoneUnbakedItemModel(Identifier modelId) implements ItemModel.Unbaked {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "runestone_item_model");
    public static final MapCodec<RunestoneUnbakedItemModel> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("model").forGetter(RunestoneUnbakedItemModel::modelId)
            ).apply(instance, RunestoneUnbakedItemModel::new)
    );

    @Override
    public @NonNull MapCodec<? extends ItemModel.Unbaked> type() {
        return MAP_CODEC;
    }

    @Override
    public @NonNull ItemModel bake(ItemModel.@NonNull BakingContext context, @NonNull Matrix4fc parentTransform) {
        ModelBaker baker = context.blockModelBaker();
        ResolvedModel resolvedModel = baker.getModel(this.modelId);
        TextureSlots slots = resolvedModel.getTopTextureSlots();
        QuadCollection bakedGeometry = resolvedModel.bakeTopGeometry(slots, baker, BlockModelRotation.IDENTITY);
        ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, slots);
        return new RunestoneItemModel(
                bakedGeometry.getAll(),
                Suppliers.memoize(() -> CuboidItemModelWrapper.computeExtents(bakedGeometry.getAll())),
                properties,
                parentTransform
        );
    }

    @Override
    public void resolveDependencies(ResolvableModel.@NonNull Resolver resolver) {
        resolver.markDependency(this.modelId);
    }
}
