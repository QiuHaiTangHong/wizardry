package top.begonia.wizardry.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.LightCoordsUtil;
import org.joml.Matrix4f;
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

    /**
     * 绘制一个连接 (x1, y1, z1) 到 (x2, y2, z2) 的四角剪切柱体
     */
    public static void drawSegment(
            Matrix4f matrix, VertexConsumer buffer,
            double x1, double y1, double z1,
            double x2, double y2, double z2,
            float width, float r, float g, float b, float a
    ) {
        float x1f = (float) x1, y1f = (float) y1, z1f = (float) z1;
        float x2f = (float) x2, y2f = (float) y2, z2f = (float) z2;

        // 面 1 (前/左)
        RendererUtils.addVertex(matrix, buffer, x1f - width, y1f - width, z1f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x2f - width, y2f - width, z2f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x2f - width, y2f + width, z2f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x1f - width, y1f + width, z1f, r, g, b, a);

        // 面 2 (顶)
        RendererUtils.addVertex(matrix, buffer, x1f - width, y1f + width, z1f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x2f - width, y2f + width, z2f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x2f + width, y2f + width, z2f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x1f + width, y1f + width, z1f, r, g, b, a);

        // 面 3 (后/右)
        RendererUtils.addVertex(matrix, buffer, x1f + width, y1f + width, z1f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x2f + width, y2f + width, z2f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x2f + width, y2f - width, z2f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x1f + width, y1f - width, z1f, r, g, b, a);

        // 面 4 (底)
        RendererUtils.addVertex(matrix, buffer, x1f + width, y1f - width, z1f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x2f + width, y2f - width, z2f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x2f - width, y2f - width, z2f, r, g, b, a);
        RendererUtils.addVertex(matrix, buffer, x1f - width, y1f - width, z1f, r, g, b, a);
    }

    public static void addVertex(
            Matrix4f matrix, @NonNull VertexConsumer buffer,
            float x, float y, float z,
            float r, float g, float b, float a
    ) {
        // 使用全亮光照 (FULL_BRIGHT = LightTexture.FULL_BRIGHT = 0xF000F0 / 240, 240)
        buffer.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setUv(0, 0)
                .setUv2(LightCoordsUtil.FULL_BRIGHT, LightCoordsUtil.FULL_BRIGHT)
                .setNormal(0, 1, 0);
    }
}
