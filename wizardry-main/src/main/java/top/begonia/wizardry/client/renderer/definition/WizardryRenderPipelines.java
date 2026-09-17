package top.begonia.wizardry.client.renderer.definition;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public final class WizardryRenderPipelines {
    public static final RenderPipeline.Snippet POSITION_TEX_BASE_SNIPPET = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/position_tex_lightmap"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/position_tex_lightmap"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER2)
            .withVertexBinding(0, WizardryVertexFormat.POSITION_TEX_LIGHTMAP)
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
}
