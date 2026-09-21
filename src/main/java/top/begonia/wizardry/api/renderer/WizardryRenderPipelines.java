package top.begonia.wizardry.api.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.*;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.Wizardry;

public final class WizardryRenderPipelines {
    public static final RenderPipeline.Snippet NO_TEX_PARTICLE_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/no_tex_particle"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/no_tex_particle"))
            .withLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "pipeline/no_tex_particle"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER2)
            .buildSnippet();
    public static final RenderPipeline.Snippet NO_TEX_OID_PARTICLE_SNIPPET = RenderPipeline.builder()
            .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
            .withBindGroupLayout(BindGroupLayouts.FOG)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/no_tex_particle"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/no_tex_particle"))
            .withLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "pipeline/no_tex_particle"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER2)
            .buildSnippet();
    public static final RenderPipeline.Snippet ITEM_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
            .withVertexShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(Wizardry.MODID, "core/item"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2)
            .withVertexBinding(0, DefaultVertexFormat.ENTITY)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .buildSnippet();
    public static final RenderPipeline ITEM_CUTOUT = RenderPipeline.builder(WizardryRenderPipelines.ITEM_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "pipeline/item_cutout"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
            .build();
    public static final RenderPipeline EMISSION_ITEM_CUTOUT = RenderPipeline.builder(WizardryRenderPipelines.ITEM_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "pipeline/emission_item_cutout"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("EMISSION")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
            .build();
}
