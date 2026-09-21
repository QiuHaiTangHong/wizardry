package top.begonia.wizardry.api.particle.renderer;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.renderer.buffer.AbstractVertexAttributeBuffer;
import top.begonia.wizardry.api.particle.renderer.buffer.NoTexVertexAttributeBuffer;
import top.begonia.wizardry.api.particle.renderer.buffer.VertexAttributeBuffer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompositeParticleRenderState implements ParticleGroupRenderState {
    public static final int INITIAL_PARTICLE_CAPACITY = 1024;
    private final Map<Layer, AbstractVertexAttributeBuffer> buffers = new HashMap<>();
    private int primitiveCount;

    @Override
    public void submit(@NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        if (this.primitiveCount > 0 && submitNodeCollector.order(0) instanceof SubmitNodeCollection submitNodeCollection) {
            submitNodeCollection.solid.submit(new CompositeParticleFeatureRenderer.Submit(this, false));
            submitNodeCollection.afterTerrain.submit(new CompositeParticleFeatureRenderer.Submit(this, true));
        }
    }

    @Override
    public void clear() {
        this.buffers.values().forEach(AbstractVertexAttributeBuffer::clear);
        this.primitiveCount = 0;
    }

    public void addQuad(
            @NonNull Layer layer,
            float x0, float y0, float z0, float u0, float v0,
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3,
            int color,
            int lightCoords
    ) {
        if (layer.primitiveTopology() != PrimitiveTopology.QUADS) {
            throw new IllegalArgumentException(
                    "Layer " + layer + " does not support QUADS primitive topology"
            );
        }
        VertexAttributeBuffer instance = (VertexAttributeBuffer) this.buffers.computeIfAbsent(layer, (layer1) -> new VertexAttributeBuffer(layer1.primitiveTopology(), INITIAL_PARTICLE_CAPACITY));
        instance.addVertex(x0, y0, z0).setUv(u0, v0).setVertexColor(color).setLight(lightCoords);
        instance.addVertex(x1, y1, z1).setUv(u1, v1).setVertexColor(color).setLight(lightCoords);
        instance.addVertex(x2, y2, z2).setUv(u2, v2).setVertexColor(color).setLight(lightCoords);
        instance.addVertex(x3, y3, z3).setUv(u3, v3).setVertexColor(color).setLight(lightCoords);
        ++this.primitiveCount;
    }

    public NoTexTriangleStripBuilder addNoTexTriangleStrip(
            @NonNull Layer layer,
            Vec3 vertex0,
            Vec3 vertex1,
            Vec3 vertex2,
            int color,
            int lightCoords
    ) {
        return this.addNoTexTriangleStrip(
                layer,
                (float) vertex0.x, (float) vertex0.y, (float) vertex0.z,
                (float) vertex1.x, (float) vertex1.y, (float) vertex1.z,
                (float) vertex2.x, (float) vertex2.y, (float) vertex2.z,
                color, lightCoords
        );
    }

    public NoTexTriangleStripBuilder addNoTexTriangleStrip(
            @NonNull Layer layer,
            float x0, float y0, float z0,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            int color,
            int lightCoords
    ) {
        if (layer.primitiveTopology() != PrimitiveTopology.TRIANGLE_STRIP) {
            throw new IllegalArgumentException(
                    "Layer " + layer + " does not support TRIANGLE_STRIP primitive topology"
            );
        }
        NoTexVertexAttributeBuffer instance = (NoTexVertexAttributeBuffer) this.buffers.computeIfAbsent(layer, (layer1) -> new NoTexVertexAttributeBuffer(layer1.primitiveTopology(), INITIAL_PARTICLE_CAPACITY));
        instance.addVertex(x0, y0, z0).setVertexColor(color).setLight(lightCoords);
        instance.addVertex(x1, y1, z1).setVertexColor(color).setLight(lightCoords);
        instance.addVertex(x2, y2, z2).setVertexColor(color).setLight(lightCoords);
        ++this.primitiveCount;
        return new NoTexTriangleStripBuilder(instance, this);
    }

    public boolean isEmpty() {
        return this.primitiveCount == 0;
    }

    public void buildLayer(Layer layer, VertexConsumer bufferBuilder) {
        AbstractVertexAttributeBuffer data = this.buffers.get(layer);
        if (data != null) {
            data.forEachVertex(bufferBuilder);
        }
    }

    public Set<Layer> layers() {
        return this.buffers.keySet();
    }

    public static class NoTexTriangleStripBuilder {
        private final NoTexVertexAttributeBuffer vertexAttributeBuffer;
        private final CompositeParticleRenderState state;

        public NoTexTriangleStripBuilder(NoTexVertexAttributeBuffer vertexAttributeBuffer, CompositeParticleRenderState state) {
            this.vertexAttributeBuffer = vertexAttributeBuffer;
            this.state = state;
        }

        public NoTexTriangleStripBuilder addNoTexVertexAttribute(float x, float y, float z, int color, int lightCoords) {
            vertexAttributeBuffer.addVertex(x, y, z).setVertexColor(color).setLight(lightCoords);
            this.state.primitiveCount++;
            return this;
        }

        public NoTexTriangleStripBuilder addNoTexVertexAttribute(@NonNull Vec3 vertex, int color, int lightCoords){
            return this.addNoTexVertexAttribute((float) vertex.x, (float) vertex.y, (float) vertex.z, color, lightCoords);
        }
    }
}
