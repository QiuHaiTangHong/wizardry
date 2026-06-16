package top.begonia.wizardry.client.render.entity.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.data.definition.bookshelf.BookshelfBookSettings;
import top.begonia.wizardry.client.data.definition.model.OnlyModelQuads;
import top.begonia.wizardry.core.entity.block.BookshelfBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class BookshelfRenderState extends BlockEntityRenderState {
    public final List<Identifier> displayTextures = new ArrayList<>(BookshelfBlockEntity.SLOT_COUNT);
    public final List<List<OnlyModelQuads.QuadGeometry>> displayGeometries = new ArrayList<>(BookshelfBlockEntity.SLOT_COUNT);
    public Direction facing;
    public BlockPos blockPos;
    public BlockState blockState;

    public BookshelfRenderState() {
        for (int i = 0; i < BookshelfBlockEntity.SLOT_COUNT; i++) {
            this.displayTextures.add(null);
            this.displayGeometries.add(new ArrayList<>());
        }
    }

    public void bindTextureInSlot(@NonNull ItemStack itemStack, int index, BookshelfBookSettings settings) {
        if (itemStack.isEmpty() || settings == null) {
            this.displayTextures.set(index, Identifier.withDefaultNamespace("textures/block/missing_no.png"));
            return;
        }
        Item item = itemStack.getItem();
        Identifier itemIdentifier = BuiltInRegistries.ITEM.getKey(item);
        Identifier textureIdentifier = settings.textureMapping().get(itemIdentifier);
        if (textureIdentifier != null) {
            String path = textureIdentifier.getPath();
            if (!path.startsWith("textures/")) {
                textureIdentifier = textureIdentifier.withPrefix("textures/");
            }
            if (!path.endsWith(".png")) {
                textureIdentifier = textureIdentifier.withSuffix(".png");
            }
            this.displayTextures.set(index, textureIdentifier);
        } else {
            this.displayTextures.set(index, Identifier.withDefaultNamespace("textures/block/missing_no.png"));
        }
    }

    public void bindBlockStateModelPartInSlot(OnlyModelQuads modelAsset, int index) {
        List<OnlyModelQuads.QuadGeometry> slotParts = this.displayGeometries.get(index);
        slotParts.clear();

        if (modelAsset != null && modelAsset.quads() != null) {
            slotParts.addAll(modelAsset.quads());
        }
    }

    public void clean() {
        for (int i = 0; i < BookshelfBlockEntity.SLOT_COUNT; i++) {
            this.displayTextures.set(i, null);
            this.displayGeometries.get(i).clear();
        }
    }
}
