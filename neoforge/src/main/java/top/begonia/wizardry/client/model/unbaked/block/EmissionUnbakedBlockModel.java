package top.begonia.wizardry.client.model.unbaked.block;

import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.util.context.ContextMap;
import net.neoforged.neoforge.client.model.DelegateUnbakedModel;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class EmissionUnbakedBlockModel extends DelegateUnbakedModel {
    public EmissionUnbakedBlockModel(UnbakedModel delegate) {
        super(delegate);
    }

    @Nullable
    @Override
    public UnbakedGeometry geometry() {
        UnbakedGeometry originalGeometry = this.delegate.geometry();
        if (originalGeometry == null) return null;
        return new UnbakedGeometry() {
            @Deprecated
            @Override
            public @NonNull QuadCollection bake(@NonNull TextureSlots textureSlots, @NonNull ModelBaker modelBaker, @NonNull ModelState modelState, @NonNull ModelDebugName modelDebugName) {
                return Objects.requireNonNull(EmissionUnbakedBlockModel.super.geometry()).bake(textureSlots, modelBaker, modelState, modelDebugName);
            }

            @Override
            public @NonNull QuadCollection bake(@NonNull TextureSlots textureSlots, @NonNull ModelBaker baker, @NonNull ModelState state, @NonNull ModelDebugName debugName, @NonNull ContextMap additionalProperties) {
                QuadCollection originalQuads = originalGeometry.bake(textureSlots, baker, state, debugName, additionalProperties);
                QuadCollection.Builder builder = new QuadCollection.Builder();
                for (Direction dir : Direction.values()) {
                    processAndAddQuads(originalQuads.getQuads(dir), dir, builder);
                }
                processAndAddQuads(originalQuads.getQuads(null), null, builder);
                return builder.build();
            }
        };
    }

    private void processAndAddQuads(@NonNull List<BakedQuad> quads, @Nullable Direction dir, QuadCollection.Builder builder) {
        for (BakedQuad quad : quads) {
            BakedQuad.MaterialInfo oldMaterial = quad.materialInfo();
            String texturePath = oldMaterial.sprite().contents().name().getPath();
            if (texturePath.contains("overlay")) {
                BakedQuad glowingQuad = transformToFullBright(quad);
                if (dir != null) {
                    builder.addCulledFace(dir, glowingQuad);
                } else {
                    builder.addUnculledFace(glowingQuad);
                }
            } else {
                if (dir != null) {
                    builder.addCulledFace(dir, quad);
                } else {
                    builder.addUnculledFace(quad);
                }
            }
        }
    }

    @Contract("_ -> new")
    private @NonNull BakedQuad transformToFullBright(@NonNull BakedQuad source) {
        BakedQuad.MaterialInfo oldMaterial = source.materialInfo();
        BakedQuad.MaterialInfo newMaterial = new BakedQuad.MaterialInfo(
                oldMaterial.sprite(),
                oldMaterial.layer(),
                oldMaterial.itemRenderType(),
                oldMaterial.tintIndex(),
                false,
                15,
                oldMaterial.ambientOcclusion()
        );
        return new BakedQuad(
                source.position0(), source.position1(), source.position2(), source.position3(),
                source.packedUV0(), source.packedUV1(), source.packedUV2(), source.packedUV3(),
                source.direction(),
                newMaterial,
                source.bakedNormals(),
                source.bakedColors()
        );
    }
}
