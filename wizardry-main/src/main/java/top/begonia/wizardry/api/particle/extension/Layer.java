package top.begonia.wizardry.api.particle.extension;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.renderer.WizardryRenderPipelines;
import top.begonia.wizardry.api.renderer.WizardryVertexFormat;

import java.util.Optional;

/**
 * 表示粒子的渲染层配置。
 * <p>
 * 该记录封装了渲染粒子几何体所需的所有必要参数，
 * 包括是否将其视为半透明或不透明、使用哪个纹理图集、顶点格式结构、
 * 图元拓扑结构以及应用的具体 {@link RenderPipeline}（渲染管线）。
 * </p>
 *
 * @param translucent          该层是否需要处理半透明效果（例如混合模式）。
 * @param textureAtlasLocation 可选的纹理图集位置。如果为空，则该层不需要纹理采样。
 * @param primitiveTopology    指示顶点如何分组的 {@link PrimitiveTopology}。
 */
public record Layer(
        boolean translucent,
        Optional<Identifier> textureAtlasLocation,
        PrimitiveTopology primitiveTopology,
        VertexFormat vertexFormat,
        RenderPipeline.Snippet pipelineSnippet
) {
    /**
     * 默认的半透明四边形渲染层, 使用位置颜色顶点格式, 不包含纹理图集
     */
    public static final Layer DEFAULT_QUAD = new Layer(
            true,
            Optional.empty(),
            PrimitiveTopology.QUADS,
            DefaultVertexFormat.PARTICLE,
            RenderPipelines.PARTICLE_SNIPPET
    );
    public static final Layer DEFAULT_TRIANGLE = new Layer(
            true,
            Optional.empty(),
            PrimitiveTopology.TRIANGLE_STRIP,
            DefaultVertexFormat.PARTICLE,
            RenderPipelines.PARTICLE_SNIPPET
    );
    public static final Layer OPAQUE_QUAD = new Layer(
            false,
            Optional.of(TextureAtlas.LOCATION_PARTICLES),
            PrimitiveTopology.QUADS,
            DefaultVertexFormat.PARTICLE,
            RenderPipelines.PARTICLE_SNIPPET
    );
    public static final Layer OPAQUE_TRIANGLE = new Layer(
            false,
            Optional.of(TextureAtlas.LOCATION_PARTICLES),
            PrimitiveTopology.TRIANGLE_STRIP,
            DefaultVertexFormat.PARTICLE,
            RenderPipelines.PARTICLE_SNIPPET
    );
    public static final Layer TRANSLUCENT_QUAD = new Layer(
            true,
            Optional.of(TextureAtlas.LOCATION_PARTICLES),
            PrimitiveTopology.QUADS,
            DefaultVertexFormat.PARTICLE,
            RenderPipelines.PARTICLE_SNIPPET
    );
    public static final Layer TRANSLUCENT_TRIANGLE = new Layer(
            true,
            Optional.of(TextureAtlas.LOCATION_PARTICLES),
            PrimitiveTopology.TRIANGLE_STRIP,
            DefaultVertexFormat.PARTICLE,
            RenderPipelines.PARTICLE_SNIPPET
    );
    public static final Layer NO_TEX_TRANSLUCENT_TRIANGLE = new Layer(
            true,
            Optional.empty(),
            PrimitiveTopology.TRIANGLE_STRIP,
            WizardryVertexFormat.NO_TEX_PARTICLE,
            WizardryRenderPipelines.NO_TEX_PARTICLE_SNIPPET
    );

    public @NonNull RenderPipeline getRenderPipeline() {
        if (this.translucent) {
            return RenderPipeline.builder(pipelineSnippet)
                    .withLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "pipeline/translucent_particle"))
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
                    .withPrimitiveTopology(this.primitiveTopology)
                    .withVertexBinding(0, this.vertexFormat)
                    .withCull(false)
                    .build();
        } else {
            return RenderPipeline.builder(pipelineSnippet)
                    .withLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "pipeline/opaque_particle"))
                    .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
                    .withPrimitiveTopology(this.primitiveTopology)
                    .withVertexBinding(0, this.vertexFormat)
                    .withCull(false)
                    .build();
        }
    }

    public static Layer bySprite(@NonNull TextureAtlasSprite sprite) {
        boolean translucent = sprite.transparency().hasTranslucent();
        return translucent ? TRANSLUCENT_QUAD : OPAQUE_QUAD;
    }
}
