package top.begonia.wizardry.api.particle.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.extension.Layer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CompositeQuadParticleRenderState implements ParticleGroupRenderState {
    private static final int INITIAL_PARTICLE_CAPACITY = 1024;
    private static final int FLOATS_PER_PARTICLE = 20;
    private static final int INTS_PER_PARTICLE = 2;
    private final Map<Layer, QuadData> particles = new HashMap<>();
    private int quadCount;

    @Override
    public void submit(@NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        if (this.quadCount > 0 && submitNodeCollector.order(0) instanceof SubmitNodeCollection submitNodeCollection) {
            submitNodeCollection.solid.submit(new CompositeQuadParticleFeatureRenderer.Submit(this, false));
            submitNodeCollection.afterTerrain.submit(new CompositeQuadParticleFeatureRenderer.Submit(this, true));
        }
    }

    public void addQuad(
            Layer layer,
            float x0, float y0, float z0, float u0, float v0,
            float x1, float y1, float z1, float u1, float v1,
            float x2, float y2, float z2, float u2, float v2,
            float x3, float y3, float z3, float u3, float v3,
            int color,
            int lightCoords
    ) {
        this.particles.computeIfAbsent(layer, (ignored) -> new QuadData())
                .addQuad(
                        x0, y0, z0, u0, v0,
                        x1, y1, z1, u1, v1,
                        x2, y2, z2, u2, v2,
                        x3, y3, z3, u3, v3,
                        color, lightCoords
                );
        ++this.quadCount;
    }

    public boolean isEmpty() {
        return this.quadCount == 0;
    }

    @Override
    public void clear() {
        this.particles.values().forEach(CompositeQuadParticleRenderState.QuadData::clear);
        this.quadCount = 0;
    }

    public void buildLayer(Layer layer, VertexConsumer bufferBuilder) {
        QuadData data = this.particles.get(layer);
        if (data != null) {
            data.forEachQuad(bufferBuilder);
        }
    }

    protected void renderRotatedQuad(
            VertexConsumer builder,
            float x, float y, float z,
            float xRot, float yRot, float zRot, float wRot,
            float scale,
            float u0, float u1, float v0, float v1,
            int color,
            int lightCoords
    ) {
        Quaternionf rotation = new Quaternionf(xRot, yRot, zRot, wRot);
        this.renderVertex(builder, rotation, x, y, z, 1.0F, -1.0F, scale, u1, v1, color, lightCoords);
        this.renderVertex(builder, rotation, x, y, z, 1.0F, 1.0F, scale, u1, v0, color, lightCoords);
        this.renderVertex(builder, rotation, x, y, z, -1.0F, 1.0F, scale, u0, v0, color, lightCoords);
        this.renderVertex(builder, rotation, x, y, z, -1.0F, -1.0F, scale, u0, v1, color, lightCoords);
    }

    private void renderVertex(
            @NonNull VertexConsumer builder,
            Quaternionf rotation,
            float x, float y, float z,
            float nx, float ny,
            float scale,
            float u, float v,
            int color,
            int lightCoords
    ) {
        Vector3f scratch = (new Vector3f(nx, ny, 0.0F)).rotate(rotation).mul(scale).add(x, y, z);
        builder.addVertex(scratch.x(), scratch.y(), scratch.z()).setUv(u, v).setColor(color).setLight(lightCoords);
    }

    public Set<Layer> layers() {
        return this.particles.keySet();
    }

    private static class QuadData {
        private int capacity = INITIAL_PARTICLE_CAPACITY;
        private float[] floatData = new float[capacity * FLOATS_PER_PARTICLE];
        private int[] intData = new int[capacity * INTS_PER_PARTICLE];
        private int currentQuadIndex;

        private QuadData() {
        }

        public void addQuad(
                float x0, float y0, float z0, float u0, float v0,
                float x1, float y1, float z1, float u1, float v1,
                float x2, float y2, float z2, float u2, float v2,
                float x3, float y3, float z3, float u3, float v3,
                int color, int light
        ) {
            if (currentQuadIndex >= capacity) {
                grow();
            }

            int fIdx = currentQuadIndex * FLOATS_PER_PARTICLE;
            int iIdx = currentQuadIndex * INTS_PER_PARTICLE;

            // 顶点 0
            floatData[fIdx++] = x0;
            floatData[fIdx++] = y0;
            floatData[fIdx++] = z0;
            floatData[fIdx++] = u0;
            floatData[fIdx++] = v0;

            // 顶点 1
            floatData[fIdx++] = x1;
            floatData[fIdx++] = y1;
            floatData[fIdx++] = z1;
            floatData[fIdx++] = u1;
            floatData[fIdx++] = v1;

            // 顶点 2
            floatData[fIdx++] = x2;
            floatData[fIdx++] = y2;
            floatData[fIdx++] = z2;
            floatData[fIdx++] = u2;
            floatData[fIdx++] = v2;

            // 顶点 3
            floatData[fIdx++] = x3;
            floatData[fIdx++] = y3;
            floatData[fIdx++] = z3;
            floatData[fIdx++] = u3;
            floatData[fIdx] = v3;

            // 颜色和光照
            intData[iIdx++] = color;
            intData[iIdx] = light;

            ++currentQuadIndex;
        }

        public void forEachQuad(VertexConsumer builder) {
            for (int i = 0; i < currentQuadIndex; ++i) {
                int fIdx = i * FLOATS_PER_PARTICLE;
                int iIdx = i * INTS_PER_PARTICLE;

                int color = intData[iIdx];
                int light = intData[iIdx + 1];

                // 顶点 0
                builder.addVertex(floatData[fIdx], floatData[fIdx + 1], floatData[fIdx + 2])
                        .setUv(floatData[fIdx + 3], floatData[fIdx + 4])
                        .setColor(color)
                        .setLight(light);

                // 顶点 1
                builder.addVertex(floatData[fIdx + 5], floatData[fIdx + 6], floatData[fIdx + 7])
                        .setUv(floatData[fIdx + 8], floatData[fIdx + 9])
                        .setColor(color)
                        .setLight(light);

                // 顶点 2
                builder.addVertex(floatData[fIdx + 10], floatData[fIdx + 11], floatData[fIdx + 12])
                        .setUv(floatData[fIdx + 13], floatData[fIdx + 14])
                        .setColor(color)
                        .setLight(light);

                // 顶点 3
                builder.addVertex(floatData[fIdx + 15], floatData[fIdx + 16], floatData[fIdx + 17])
                        .setUv(floatData[fIdx + 18], floatData[fIdx + 19])
                        .setColor(color)
                        .setLight(light);
            }
        }

        public void clear() {
            this.currentQuadIndex = 0;
        }

        private void grow() {
            this.capacity *= 2;
            this.floatData = Arrays.copyOf(this.floatData, this.capacity * FLOATS_PER_PARTICLE);
            this.intData = Arrays.copyOf(this.intData, this.capacity * INTS_PER_PARTICLE);
        }

        public int count() {
            return this.currentQuadIndex;
        }
    }
}
