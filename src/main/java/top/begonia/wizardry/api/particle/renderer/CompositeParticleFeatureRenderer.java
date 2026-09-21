package top.begonia.wizardry.api.particle.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.textures.FilterMode;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.oit.OitStage;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.particle.extension.Layer;

import java.util.*;

public class CompositeParticleFeatureRenderer implements FeatureRenderer<CompositeParticleFeatureRenderer.Submit> {
    public static final FeatureRendererType<CompositeParticleFeatureRenderer.Submit> TYPE = FeatureRendererType.create("MultipleQuadParticle");
    private final List<CompositeParticleFeatureRenderer.PreparedGroup> groups = new ArrayList<>();
    private @Nullable GpuBufferSlice dynamicTransforms;

    @Override
    public void prepareGroup(
            @NonNull FeatureFrameContext context,
            @NonNull List<CompositeParticleFeatureRenderer.Submit> submits,
            boolean strictlyOrdered) {
        if (!submits.isEmpty()) {
            StagedVertexBuffer stagedVertexBuffer = context.stagedVertexBuffer();
            Map<Layer, StagedVertexBuffer.Draw> drawByLayer = new IdentityHashMap<>();
            Map<Layer, AbstractTexture> textures = new IdentityHashMap<>();

            for (CompositeParticleFeatureRenderer.Submit submit : submits) {
                CompositeParticleRenderState particles = submit.particles();
                if (!particles.isEmpty()) {
                    for (Layer layer : particles.layers()) {
                        if (layer.translucent() == submit.translucent()) {
                            StagedVertexBuffer.Draw draw = drawByLayer.computeIfAbsent(layer, (tempLayer) -> stagedVertexBuffer.appendDraw(tempLayer.vertexFormat(), tempLayer.primitiveTopology(), null));
                            particles.buildLayer(layer, stagedVertexBuffer.getVertexBuilder(draw));
                            Optional<Identifier> textureAtlasOptional = layer.textureAtlasLocation();
                            textureAtlasOptional.ifPresent(identifier -> textures.put(layer, context.textureManager().getTexture(identifier)));
                            stagedVertexBuffer.requestIndexCount(draw);
                        }
                    }
                }
            }

            boolean translucent = submits.getFirst().translucent();
            this.groups.add(new CompositeParticleFeatureRenderer.PreparedGroup(drawByLayer, textures, translucent));
        }

    }

    @Override
    public void finishPrepare(@NonNull FeatureFrameContext context) {
        this.dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
    }

    @Override
    public void executeGroup(
            @NonNull FeatureFrameContext featureFrameContext,
            @Nullable OitStage oitStage,
            @NonNull RenderPass renderPass,
            int groupIndex,
            @NonNull List<Submit> submits,
            boolean strictlyOrdered
    ) {
        CompositeParticleFeatureRenderer.PreparedGroup group = this.groups.get(groupIndex);
        renderPass.pushDebugGroup(() -> "Particles - " + (group.translucent ? "Translucent" : "Solid"));
        RenderSystem.bindDefaultUniforms(renderPass);
        renderPass.setUniform("DynamicTransforms", Objects.requireNonNull(this.dynamicTransforms));
        renderPass.setUniform("Sampler2", featureFrameContext.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
        drawLayers(featureFrameContext.stagedVertexBuffer(), group, renderPass, oitStage);
        renderPass.popDebugGroup();
    }

    private static void drawLayers(
            StagedVertexBuffer stagedBuffer,
            CompositeParticleFeatureRenderer.@NonNull PreparedGroup group,
            RenderPass renderPass,
            @Nullable OitStage stage
    ) {
        for (Map.Entry<Layer, StagedVertexBuffer.Draw> entry : group.layers.entrySet()) {
            StagedVertexBuffer.ExecuteInfo executeInfo = stagedBuffer.getExecuteInfo(entry.getValue());
            if (executeInfo != null) {
                renderPass.setPipeline(RenderSystem.getCompiledPipeline(stage != null ? getOitPipeline(stage, entry.getKey()) : entry.getKey().getRenderPipeline()));
                renderPass.setVertexBuffer(0, executeInfo.vertexBuffer().slice());
                renderPass.setIndexBuffer(executeInfo.indexBuffer(), executeInfo.indexType());
                Optional<Identifier> textureOptional = entry.getKey().textureAtlasLocation();
                if (textureOptional.isPresent()) {
                    AbstractTexture texture = group.textures.get(entry.getKey());
                    renderPass.setUniform("Sampler0", texture.getTextureView(), texture.getSampler());
                }
                renderPass.drawIndexed(executeInfo.indexCount(), 1, executeInfo.firstIndex(), executeInfo.baseVertex(), 0);
            }
        }

    }

    private static @NonNull RenderPipeline getOitPipeline(OitStage stage, @NonNull Layer layer) {
        if (layer.getOitPipelineSet() == null) {
            throw new IllegalStateException("OIT pipeline set for particle layer not specified.");
        } else {
            return layer.getOitPipelineSet().getPipeline(stage);
        }
    }

    @Override
    public void finishExecute(@NonNull FeatureFrameContext context) {
        this.groups.clear();
        this.dynamicTransforms = null;
    }

    private record PreparedGroup(
            Map<Layer, StagedVertexBuffer.Draw> layers,
            Map<Layer, AbstractTexture> textures,
            boolean translucent
    ) {
    }

    public record Submit(
            CompositeParticleRenderState particles,
            boolean translucent
    ) implements SubmitNode {
        public @NonNull FeatureRendererType<CompositeParticleFeatureRenderer.Submit> featureType() {
            return CompositeParticleFeatureRenderer.TYPE;
        }
    }
}
