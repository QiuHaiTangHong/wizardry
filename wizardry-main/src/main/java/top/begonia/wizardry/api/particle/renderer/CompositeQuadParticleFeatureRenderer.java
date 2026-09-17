package top.begonia.wizardry.api.particle.renderer;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderer;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.particle.extension.Layer;

import java.util.*;

public class CompositeQuadParticleFeatureRenderer implements FeatureRenderer<CompositeQuadParticleFeatureRenderer.Submit> {
    public static final FeatureRendererType<CompositeQuadParticleFeatureRenderer.Submit> TYPE = FeatureRendererType.create("MultipleQuadParticle");
    private final List<CompositeQuadParticleFeatureRenderer.PreparedGroup> groups = new ArrayList<>();
    private @Nullable GpuBufferSlice dynamicTransforms;

    @Override
    public void prepareGroup(@NonNull FeatureFrameContext featureFrameContext, @NonNull List<Submit> submits, boolean strictlyOrdered) {
        if (!submits.isEmpty()) {
            StagedVertexBuffer stagedVertexBuffer = featureFrameContext.stagedVertexBuffer();
            Map<Layer, StagedVertexBuffer.Draw> drawByLayer = new IdentityHashMap<>();

            for (CompositeQuadParticleFeatureRenderer.Submit submit : submits) {
                CompositeQuadParticleRenderState particles = submit.particles();
                if (!particles.isEmpty()) {
                    for (Layer layer : particles.layers()) {
                        if (layer.translucent() == submit.translucent()) {
                            StagedVertexBuffer.Draw draw = drawByLayer.computeIfAbsent(layer, (tempLayer) -> stagedVertexBuffer.appendDraw(tempLayer.vertexFormat(), tempLayer.primitiveTopology(), null));
                            particles.buildLayer(layer, stagedVertexBuffer.getVertexBuilder(draw));
                        }
                    }
                }
            }

            boolean translucent = submits.getFirst().translucent();
            this.groups.add(new CompositeQuadParticleFeatureRenderer.PreparedGroup(drawByLayer, translucent));
        }
    }

    @Override
    public void finishPrepare(@NonNull FeatureFrameContext context) {
        this.dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
    }

    @Override
    public void executeGroup(@NonNull FeatureFrameContext featureFrameContext, int groupIndex, @NonNull List<Submit> submits, boolean strictlyOrdered) {
        CompositeQuadParticleFeatureRenderer.PreparedGroup group = this.groups.get(groupIndex);
        GpuDevice device = RenderSystem.getDevice();
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.gameRenderer.mainRenderTarget();
        RenderTarget particleTarget = minecraft.levelRenderer.particlesTarget();
        boolean useParticleTarget = particleTarget != null && group.translucent;
        GpuTextureView colorTextureView = useParticleTarget ? particleTarget.getColorTextureView() : mainTarget.getColorTextureView();
        GpuTextureView depthTextureView = useParticleTarget ? particleTarget.getDepthTextureView() : mainTarget.getDepthTextureView();

        if (colorTextureView != null) {
            try (RenderPass renderPass = device.createCommandEncoder().createRenderPass(() -> "Particles - " + (group.translucent ? "Translucent" : "Solid"), colorTextureView, Optional.empty(), depthTextureView, OptionalDouble.empty())) {
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", Objects.requireNonNull(this.dynamicTransforms));
                renderPass.bindTexture("Sampler2", featureFrameContext.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
                drawLayers(featureFrameContext.stagedVertexBuffer(), group.layers, renderPass, featureFrameContext.textureManager());
            }
        }
    }

    private static void drawLayers(StagedVertexBuffer stagedBuffer, @NonNull Map<Layer, StagedVertexBuffer.Draw> layers, RenderPass renderPass, TextureManager textureManager) {
        for (Map.Entry<Layer, StagedVertexBuffer.Draw> entry : layers.entrySet()) {
            StagedVertexBuffer.ExecuteInfo executeInfo = stagedBuffer.getExecuteInfo(entry.getValue());
            if (executeInfo != null) {
                renderPass.setPipeline(entry.getKey().pipeline());
                renderPass.setVertexBuffer(0, executeInfo.vertexBuffer().slice());
                renderPass.setIndexBuffer(executeInfo.indexBuffer(), executeInfo.indexType());
                entry.getKey().textureAtlasLocation().ifPresent(textureAtlasLocation -> {
                    AbstractTexture texture = textureManager.getTexture(textureAtlasLocation);
                    renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
                });
                renderPass.drawIndexed(executeInfo.indexCount(), 1, executeInfo.firstIndex(), executeInfo.baseVertex(), 0);
            }
        }
    }

    public void finishExecute(@NonNull FeatureFrameContext context) {
        this.groups.clear();
        this.dynamicTransforms = null;
    }

    private record PreparedGroup(Map<Layer, StagedVertexBuffer.Draw> layers,
                                 boolean translucent) {
    }

    public record Submit(CompositeQuadParticleRenderState particles, boolean translucent) implements SubmitNode {
        public @NonNull FeatureRendererType<CompositeQuadParticleFeatureRenderer.Submit> featureType() {
            return CompositeQuadParticleFeatureRenderer.TYPE;
        }
    }
}
