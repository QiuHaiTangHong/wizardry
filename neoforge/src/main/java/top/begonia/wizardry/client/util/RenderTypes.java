package top.begonia.wizardry.client.util;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import top.begonia.wizardry.Wizardry;

import java.util.function.Function;

public final class RenderTypes {
    private static final RenderPipeline BASE_ITEM_PIPELINE = Util.make(() -> RenderPipeline
            .builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("wizardry", "pipeline/bright_base"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
            .withVertexBinding(0, DefaultVertexFormat.ENTITY)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build()
    );

    private static final RenderPipeline ALWAYS_PASS_ITEM_PIPELINE = Util.make(() -> RenderPipeline
            .builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("wizardry", "pipeline/bright_overlay"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("EMISSION")
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
            .withVertexBinding(0, DefaultVertexFormat.ENTITY)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .build()
    );

    public static final Function<Identifier, RenderType> OVERLY = Util.memoize((atlasLocation) -> {
        RenderSetup setup = RenderSetup.builder(ALWAYS_PASS_ITEM_PIPELINE)
                .withTexture("Sampler0", atlasLocation)
                .useOverlay()
                .useLightmap()
                .createRenderSetup();
        return RenderType.create("overly", setup);
    });

    public static final Function<Identifier, RenderType> BASE = Util.memoize((atlasLocation) -> {
        RenderSetup setup = RenderSetup.builder(BASE_ITEM_PIPELINE)
                .withTexture("Sampler0", atlasLocation)
                .useOverlay()
                .useLightmap()
                .createRenderSetup();
        return RenderType.create("base", setup);
    });

    public static RenderType getOverlyRenderType(Identifier atlasLocation) {
        return OVERLY.apply(atlasLocation);
    }

    public static RenderType getBaseRenderType(Identifier atlasLocation) {
        return BASE.apply(atlasLocation);
    }
}
