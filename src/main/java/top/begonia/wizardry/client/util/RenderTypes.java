package top.begonia.wizardry.client.util;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import top.begonia.wizardry.Wizardry;

import java.util.function.Function;

import static net.minecraft.client.renderer.RenderPipelines.FOG_SNIPPET;
import static net.minecraft.client.renderer.RenderPipelines.MATRICES_PROJECTION_SNIPPET;

public final class RenderTypes {
    public static final RenderPipeline BASE_ITEM_PIPELINE = Util.make(() -> RenderPipeline
            .builder(MATRICES_PROJECTION_SNIPPET, FOG_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("wizardry", "pipeline/bright_base"))
            .withUniform("Lighting", UniformType.UNIFORM_BUFFER)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .build()
    );

    private static final RenderPipeline ALWAYS_PASS_ITEM_PIPELINE = Util.make(() -> RenderPipeline
            .builder(MATRICES_PROJECTION_SNIPPET, FOG_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath("wizardry", "pipeline/bright_overlay"))
            .withUniform("Lighting", UniformType.UNIFORM_BUFFER)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withSampler("Sampler0")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .build()
    );

    public static final Function<Identifier, RenderType> OVERLY = Util.memoize((atlasLocation) -> {
        RenderSetup setup = RenderSetup.builder(ALWAYS_PASS_ITEM_PIPELINE)
                .withTexture("Sampler0", atlasLocation)
                .createRenderSetup();
        return RenderType.create("overly", setup);
    });

    public static final Function<Identifier, RenderType> BASE = Util.memoize((atlasLocation) -> {
        RenderSetup setup = RenderSetup.builder(BASE_ITEM_PIPELINE)
                .withTexture("Sampler0", atlasLocation)
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
