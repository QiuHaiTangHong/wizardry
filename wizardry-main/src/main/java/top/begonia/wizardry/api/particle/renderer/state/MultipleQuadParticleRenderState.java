package top.begonia.wizardry.api.particle.renderer.state;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.ParticleGroupRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.impl.WizardryQuadParticle;
import top.begonia.wizardry.api.particle.renderer.MultipleQuadParticleFeatureRenderer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultipleQuadParticleRenderState implements ParticleGroupRenderState {
    private static final int INITIAL_PARTICLE_CAPACITY = 1024;
    private static final int FLOATS_PER_PARTICLE = 12;
    private static final int INTS_PER_PARTICLE = 2;
    private final Map<WizardryQuadParticle.Layer, Storage> particles = new HashMap<>();
    private int quadCount;

    @Override
    public void submit(@NonNull SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState cameraRenderState) {
        if (this.quadCount > 0 && submitNodeCollector.order(0) instanceof SubmitNodeCollection submitNodeCollection) {
            submitNodeCollection.solid.submit(new MultipleQuadParticleFeatureRenderer.Submit(this, false));
            submitNodeCollection.afterTerrain.submit(new MultipleQuadParticleFeatureRenderer.Submit(this, true));
        }
    }

    public void addQuad(
            WizardryQuadParticle.Layer layer,
            float x, float y, float z,
            float xRot, float yRot, float zRot, float wRot,
            float scale,
            float u0, float u1, float v0, float v1,
            int color,
            int lightCoords
    ) {
        this.particles.computeIfAbsent(layer, (ignored) -> new Storage())
                .addQuad(
                        x, y, z,
                        xRot, yRot, zRot, wRot,
                        scale,
                        u0, u1, v0, v1,
                        color,
                        lightCoords
                );
        ++this.quadCount;
    }

    public boolean isEmpty() {
        return this.quadCount == 0;
    }

    @Override
    public void clear() {
        this.particles.values().forEach(MultipleQuadParticleRenderState.Storage::clear);
        this.quadCount = 0;
    }

    public void buildLayer(WizardryQuadParticle.Layer layer, VertexConsumer bufferBuilder) {
        MultipleQuadParticleRenderState.Storage storage = this.particles.get(layer);
        if (storage != null) {
            storage.forEachParticle((
                            x, y, z,
                            xRot, yRot, zRot, wRot,
                            scale,
                            u0, u1, v0, v1,
                            color,
                            lightCoords
                    ) -> this.renderRotatedQuad(bufferBuilder, x, y, z, xRot, yRot, zRot, wRot, scale, u0, u1, v0, v1, color, lightCoords)
            );
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

    public Set<WizardryQuadParticle.Layer> layers() {
        return this.particles.keySet();
    }

    private static class Storage {
        private int capacity = INITIAL_PARTICLE_CAPACITY;
        private float[] floatValues = new float[12288];
        private int[] intValues = new int[2048];
        private int currentParticleIndex;

        private Storage() {
        }

        public void addQuad(
                float x, float y, float z,
                float xRot, float yRot, float zRot, float wRot,
                float scale,
                float u0, float u1, float v0, float v1,
                int color,
                int lightCoords
        ) {
            if (this.currentParticleIndex >= this.capacity) {
                this.grow();
            }

            int index = this.currentParticleIndex * FLOATS_PER_PARTICLE;
            this.floatValues[index++] = x;
            this.floatValues[index++] = y;
            this.floatValues[index++] = z;
            this.floatValues[index++] = xRot;
            this.floatValues[index++] = yRot;
            this.floatValues[index++] = zRot;
            this.floatValues[index++] = wRot;
            this.floatValues[index++] = scale;
            this.floatValues[index++] = u0;
            this.floatValues[index++] = u1;
            this.floatValues[index++] = v0;
            this.floatValues[index] = v1;
            index = this.currentParticleIndex * INTS_PER_PARTICLE;
            this.intValues[index++] = color;
            this.intValues[index] = lightCoords;
            ++this.currentParticleIndex;
        }

        public void forEachParticle(ParticleConsumer consumer) {
            for (int particleIndex = 0; particleIndex < this.currentParticleIndex; ++particleIndex) {
                int floatIndex = particleIndex * FLOATS_PER_PARTICLE;
                int intIndex = particleIndex * INTS_PER_PARTICLE;
                consumer.consume(
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex++],
                        this.floatValues[floatIndex],
                        this.intValues[intIndex++],
                        this.intValues[intIndex]
                );
            }

        }

        public void clear() {
            this.currentParticleIndex = 0;
        }

        private void grow() {
            this.capacity *= INTS_PER_PARTICLE;
            this.floatValues = Arrays.copyOf(this.floatValues, this.capacity * FLOATS_PER_PARTICLE);
            this.intValues = Arrays.copyOf(this.intValues, this.capacity * INTS_PER_PARTICLE);
        }

        public int count() {
            return this.currentParticleIndex;
        }
    }

    @FunctionalInterface
    public interface ParticleConsumer {
        void consume(
                float x, float y, float z,
                float xRot, float yRot, float wRot, float zRot,
                float scale,
                float u0, float u1, float v0, float v1,
                int color,
                int lightCoords
        );
    }
}
