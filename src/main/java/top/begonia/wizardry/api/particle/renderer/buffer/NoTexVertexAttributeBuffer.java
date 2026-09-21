package top.begonia.wizardry.api.particle.renderer.buffer;

import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.blaze3d.vertex.VertexConsumer;
import top.begonia.wizardry.api.particle.renderer.recorder.ILightRecorder;
import top.begonia.wizardry.api.particle.renderer.recorder.INoTexVertexRecorder;
import top.begonia.wizardry.api.particle.renderer.recorder.IVertexColorRecorder;

public class NoTexVertexAttributeBuffer extends AbstractVertexAttributeBuffer implements
        INoTexVertexRecorder,
        IVertexColorRecorder,
        ILightRecorder {

    public NoTexVertexAttributeBuffer(PrimitiveTopology primitiveTopology, int capacity) {
        super(primitiveTopology, capacity);
    }

    @Override
    protected int getFloatSize() {
        return 3;
    }

    @Override
    protected int getIntSize() {
        return 2;
    }

    @Override
    public void forEachVertex(VertexConsumer builder) {
        int vertexCount = this.vertexCount;
        float[] floatData = this.floatData;
        int[] intData = this.intData;
        for (int i = 0; i < vertexCount; i++) {
            int floatIndex = i * this.getFloatSize();
            int intIndex = i * this.getIntSize();

            float x = floatData[floatIndex];
            float y = floatData[floatIndex + 1];
            float z = floatData[floatIndex + 2];

            int color = intData[intIndex];
            int light = intData[intIndex + 1];

            builder.addVertex(x, y, z).setColor(color).setLight(light);
        }
    }

    @Override
    public void setLight(int light) {
        int intIndex = this.vertexCount * this.getIntSize();
        intData[intIndex + 1] = light;
        this.vertexCount++;
    }

    @Override
    public ILightRecorder setVertexColor(int color) {
        int[] intData = this.intData;
        int intIndex = this.vertexCount * this.getIntSize();
        intData[intIndex] = color;
        return this;
    }

    @Override
    public IVertexColorRecorder addVertex(float x, float y, float z) {
        if (this.vertexCount >= this.capacity * this.primitiveTopology.primitiveLength) {
            this.grow();
        }
        int floatIndex = this.vertexCount * this.getFloatSize();
        float[] floatData = this.floatData;

        floatData[floatIndex] = x;
        floatData[floatIndex + 1] = y;
        floatData[floatIndex + 2] = z;

        return this;
    }
}
