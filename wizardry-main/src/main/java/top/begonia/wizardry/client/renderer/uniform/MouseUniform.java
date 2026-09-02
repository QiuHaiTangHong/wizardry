package top.begonia.wizardry.client.renderer.uniform;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class MouseUniform implements AutoCloseable {
    public static final int UBO_SIZE = new Std140SizeCalculator().putVec4().get();
    private final GpuBuffer buffer;

    public MouseUniform() {
        this.buffer = RenderSystem.getDevice().createBuffer(() -> "Mouse UBO", 136, UBO_SIZE);
    }

    public void update(float mouseX, float mouseY, float w, float h) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer data = Std140Builder.onStack(stack, UBO_SIZE)
                    .putVec4(mouseX, mouseY, w, h)
                    .get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), data);
        }
    }

    public GpuBufferSlice slice() {
        return this.buffer.slice();
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}
