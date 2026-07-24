package top.begonia.wizardry.client.model.item;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public record SpellBookUnbakedItemModel(Identifier modelId) implements ItemModel.Unbaked {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "spell_book_item_model");
    public static final MapCodec<SpellBookUnbakedItemModel> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("model").forGetter(SpellBookUnbakedItemModel::modelId)
            ).apply(instance, SpellBookUnbakedItemModel::new)
    );

    @Override
    public @NonNull MapCodec<? extends ItemModel.Unbaked> type() {
        return MAP_CODEC;
    }

    @Override
    public @NonNull ItemModel bake(ItemModel.@NonNull BakingContext bakingContext, @NonNull Matrix4fc matrix4fc) {
        ModelBaker baker = bakingContext.blockModelBaker();
        ResolvedModel resolvedModel = baker.getModel(this.modelId);
        TextureSlots slots = resolvedModel.getTopTextureSlots();
        QuadCollection bakedGeometry = resolvedModel.bakeTopGeometry(slots, baker, BlockModelRotation.IDENTITY);
        ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, slots);
        return new SpellBookItemModel(
                bakedGeometry.getAll(),
                Suppliers.memoize(() -> CuboidItemModelWrapper.computeExtents(bakedGeometry.getAll())),
                properties,
                matrix4fc
        );
    }

    @Override
    public void resolveDependencies(@NonNull Resolver resolver) {
        resolver.markDependency(this.modelId);
    }
}
