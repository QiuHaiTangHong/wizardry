package top.begonia.wizardry.api.renderer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public final class WizardryRenderPipelines {
    public static final RenderPipeline.Snippet POSITION_TEX_BASE_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/position_tex_lightmap"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/position_tex_lightmap"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER2)
            .withVertexBinding(0, WizardryVertexFormat.POSITION_TEX_LIGHTMAP)
            .buildSnippet();
    public static final RenderPipeline.Snippet NO_TEX_PARTICLE_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/no_tex_particle"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/no_tex_particle"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER2)
            .buildSnippet();

    public static @NonNull RenderPipeline positionTexLightmap(
            PrimitiveTopology primitiveTopology,
            ColorTargetState colorTargetState,
            boolean emissive,
            boolean noCardinalLighting
    ){
        RenderPipeline.Builder pipeline = RenderPipeline
                .builder(POSITION_TEX_BASE_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "pipeline/position_tex_lightmap"))
                .withPrimitiveTopology(primitiveTopology)
                .withColorTargetState(colorTargetState);
        if (emissive){
            pipeline.withShaderDefine("EMISSIVE");
        }
        if (noCardinalLighting){
            pipeline.withShaderDefine("NO_CARDINAL_LIGHTING");
        }
        return pipeline.build();
    }

    public static @NonNull RenderPipeline noTexParticle(
            PrimitiveTopology primitiveTopology,
            ColorTargetState colorTargetState,
            DepthStencilState depthStencilState,
            VertexFormat vertexFormat,
            boolean cull
    ){
        return RenderPipeline
                .builder(NO_TEX_PARTICLE_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "pipeline/no_tex_particle"))
                .withPrimitiveTopology(primitiveTopology)
                .withColorTargetState(colorTargetState)
                .withDepthStencilState(depthStencilState)
                .withVertexBinding(0, WizardryVertexFormat.NO_TEX_PARTICLE)
                .withCull(cull)
                .build();
    }
}
