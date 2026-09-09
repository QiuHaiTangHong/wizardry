package top.begonia.wizardry.api.particle.renderer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
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
import top.begonia.wizardry.api.particle.impl.WizardryQuadParticle;
import top.begonia.wizardry.api.particle.renderer.state.MultipleQuadParticleRenderState;

import java.util.*;

public class MultipleQuadParticleFeatureRenderer implements FeatureRenderer<MultipleQuadParticleFeatureRenderer.Submit> {
    public static final FeatureRendererType<MultipleQuadParticleFeatureRenderer.Submit> TYPE = FeatureRendererType.create("MultipleQuadParticle");
    private final List<MultipleQuadParticleFeatureRenderer.PreparedGroup> groups = new ArrayList<>();
    private @Nullable GpuBufferSlice dynamicTransforms;

    @Override
    public void prepareGroup(@NonNull FeatureFrameContext featureFrameContext, @NonNull List<Submit> submits, boolean strictlyOrdered) {
        if (!submits.isEmpty()) {
            StagedVertexBuffer stagedVertexBuffer = featureFrameContext.stagedVertexBuffer();
            Map<WizardryQuadParticle.Layer, StagedVertexBuffer.Draw> drawByLayer = new IdentityHashMap<>();

            for (MultipleQuadParticleFeatureRenderer.Submit submit : submits) {
                MultipleQuadParticleRenderState particles = submit.particles();
                if (!particles.isEmpty()) {
                    for (WizardryQuadParticle.Layer layer : particles.layers()) {
                        if (layer.translucent() == submit.translucent()) {
                            StagedVertexBuffer.Draw draw = drawByLayer.computeIfAbsent(layer, (var1) -> stagedVertexBuffer.appendDraw(DefaultVertexFormat.PARTICLE, PrimitiveTopology.QUADS, null));
                            particles.buildLayer(layer, stagedVertexBuffer.getVertexBuilder(draw));
                        }
                    }
                }
            }

            boolean translucent = submits.getFirst().translucent();
            this.groups.add(new MultipleQuadParticleFeatureRenderer.PreparedGroup(drawByLayer, translucent));
        }
    }

    @Override
    public void finishPrepare(@NonNull FeatureFrameContext context) {
        this.dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrixCopy());
    }

    @Override
    public void executeGroup(@NonNull FeatureFrameContext featureFrameContext, int groupIndex, @NonNull List<Submit> submits, boolean strictlyOrdered) {
        MultipleQuadParticleFeatureRenderer.PreparedGroup group = this.groups.get(groupIndex);
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

    private static void drawLayers(StagedVertexBuffer stagedBuffer, @NonNull Map<WizardryQuadParticle.Layer, StagedVertexBuffer.Draw> layers, RenderPass renderPass, TextureManager textureManager) {
        for (Map.Entry<WizardryQuadParticle.Layer, StagedVertexBuffer.Draw> entry : layers.entrySet()) {
            StagedVertexBuffer.ExecuteInfo executeInfo = stagedBuffer.getExecuteInfo(entry.getValue());
            if (executeInfo != null) {
                renderPass.setPipeline(entry.getKey().pipeline());
                renderPass.setVertexBuffer(0, executeInfo.vertexBuffer().slice());
                renderPass.setIndexBuffer(executeInfo.indexBuffer(), executeInfo.indexType());
                AbstractTexture texture = textureManager.getTexture(entry.getKey().textureAtlasLocation());
                renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
                renderPass.drawIndexed(executeInfo.indexCount(), 1, executeInfo.firstIndex(), executeInfo.baseVertex(), 0);
            }
        }
    }

    public void finishExecute(@NonNull FeatureFrameContext context) {
        this.groups.clear();
        this.dynamicTransforms = null;
    }

    private record PreparedGroup(Map<WizardryQuadParticle.Layer, StagedVertexBuffer.Draw> layers,
                                 boolean translucent) {
    }

    public record Submit(MultipleQuadParticleRenderState particles, boolean translucent) implements SubmitNode {
        public @NonNull FeatureRendererType<MultipleQuadParticleFeatureRenderer.Submit> featureType() {
            return MultipleQuadParticleFeatureRenderer.TYPE;
        }
    }
}
