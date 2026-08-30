package top.begonia.wizardry.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jspecify.annotations.NonNull;

public class RendererUtils {
    public static void drawShearedBox(VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float width, float r, float g, float b, float a, int light) {
        addVertex(buffer, x1 - width, y1 - width, z1, r, g, b, a, light);
        addVertex(buffer, x2 - width, y2 - width, z2, r, g, b, a, light);
        addVertex(buffer, x1 - width, y1 + width, z1, r, g, b, a, light);
        addVertex(buffer, x2 - width, y2 + width, z2, r, g, b, a, light);
        addVertex(buffer, x1 + width, y1 + width, z1, r, g, b, a, light);
        addVertex(buffer, x2 + width, y2 + width, z2, r, g, b, a, light);
        addVertex(buffer, x1 + width, y1 - width, z1, r, g, b, a, light);
        addVertex(buffer, x2 + width, y2 - width, z2, r, g, b, a, light);
        addVertex(buffer, x1 - width, y1 - width, z1, r, g, b, a, light);
        addVertex(buffer, x2 - width, y2 - width, z2, r, g, b, a, light);
    }

    public static void addVertex(@NonNull VertexConsumer buffer, float x, float y, float z, float r, float g, float b, float a, int light) {
        buffer.addVertex(x, y, z)
                .setColor(r, g, b, a)
                .setUv1(light & 0xFFFF, (light >> 16) & 0xFFFF);
    }
}
