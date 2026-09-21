package top.begonia.wizardry.api.particle.renderer.buffer;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

public abstract class AbstractVertexAttributeBuffer {
    protected int capacity;
    protected float[] floatData;
    protected int[] intData;
    protected int vertexCount;
    protected final PrimitiveTopology primitiveTopology;

    @Contract(pure = true)
    public AbstractVertexAttributeBuffer(@NonNull PrimitiveTopology primitiveTopology, int capacity) {
        this.primitiveTopology = primitiveTopology;
        this.capacity = capacity;
        this.floatData = new float[this.capacity * this.getFloatSize() * primitiveTopology.primitiveLength];
        this.intData = new int[this.capacity * this.getIntSize() * primitiveTopology.primitiveLength];
    }

    public void clear() {
        this.vertexCount = 0;
    }

    void grow() {
        this.capacity *= 2;
        this.floatData = Arrays.copyOf(this.floatData, this.capacity * this.getFloatSize() * primitiveTopology.primitiveLength);
        this.intData = Arrays.copyOf(this.intData, this.capacity * this.getIntSize() * primitiveTopology.primitiveLength);
    }


    protected abstract int getFloatSize();

    protected abstract int getIntSize();

    public abstract void forEachVertex(VertexConsumer builder);
}
