package top.begonia.wizardry.client.renderer.definition;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public final class WizardryRenderTypes {
    private static final Function<PositionTexLightmapKey, RenderType> POSITION_TEX_LIGHTMAP =
            Util.memoize(key -> {
                RenderSetup state = RenderSetup.builder(
                                WizardryRenderPipelines.positionTexLightmap(
                                        key.primitiveTopology(),
                                        key.colorTargetState(),
                                        key.emissive(),
                                        key.noCardinalLighting()
                                )
                        )
                        .withTexture("Sampler0", key.identifier())
                        .useLightmap()
                        .createRenderSetup();

                return RenderType.create("position_tex_lightmap", state);
            });

    private record PositionTexLightmapKey(
            Identifier identifier,
            PrimitiveTopology primitiveTopology,
            ColorTargetState colorTargetState,
            boolean emissive,
            boolean noCardinalLighting
    ) {
    }

    public static @NonNull RenderType positionTexLightmap(
            Identifier identifier,
            PrimitiveTopology primitiveTopology,
            ColorTargetState colorTargetState,
            boolean emissive,
            boolean noCardinalLighting
    ) {
        return POSITION_TEX_LIGHTMAP.apply(
                new PositionTexLightmapKey(
                        identifier,
                        primitiveTopology,
                        colorTargetState,
                        emissive,
                        noCardinalLighting
                )
        );
    }
}
