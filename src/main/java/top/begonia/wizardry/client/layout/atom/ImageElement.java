package top.begonia.wizardry.client.layout.atom;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.data.definition.handbook.part.ImageData;
import top.begonia.wizardry.client.layout.util.Context;

/**
 * 图像元素类, 用于渲染具有纹理的原子元素, 支持边框绘制和纹理映射
 * <p>
 * 该类封装了图像数据的处理逻辑, 负责在图形上下文中绘制带有纹理的元素. 它通过 {@code ImageData} 获取纹理坐标, 尺寸和边框状态,
 * 并支持根据是否开启边框选项进行四向边框的绘制. 此外, 它实现了 {@code AbstractAtomElement} 接口, 支持格式化和渲染状态提取.
 * </p>
 * <p>
 * 主要功能包括:
 * <ul>
 * <li> 纹理元素的格式化与尺寸计算 </li>
 * <li> 根据翻转参数进行纹理的坐标映射 </li>
 * <li> 四向边框的绘制与渲染 </li>
 * <li> 图形提取与状态维护 </li>
 * </ul>
 * </p>
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @email mailto:2981263417@qq.com
 * @date 2026.05.03
 * @since 1.0.0
 */
public class ImageElement extends AbstractAtomElement {
    private final Identifier identifier;
    private final int u;
    private final int v;
    private final int textureDisplayWidth;
    private final int textureDisplayHeight;
    private final int textureWidth;
    private final int textureHeight;
    private final boolean border;
    private static final int TEXTURE_INSET_X = 180;
    private static final int BORDER = 1;

    public ImageElement(@NonNull ImageData imageData) {
        this.identifier = imageData.location();
        this.u = imageData.u();
        this.v = imageData.v();
        this.textureDisplayWidth = imageData.width();
        this.textureDisplayHeight = imageData.height();
        this.textureWidth = imageData.textureWidth().orElse(this.textureDisplayWidth);
        this.textureHeight = imageData.textureHeight().orElse(this.textureDisplayHeight);
        this.border = imageData.border().orElse(true);
    }

    private void drawFourWayBorder(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y) {
        int leftW = this.textureDisplayWidth / 2;
        int rightW = this.textureDisplayWidth - leftW;
        int topH = this.textureDisplayHeight / 2;
        int bottomH = this.textureDisplayHeight - topH;
        // 左上 (不翻转)
        renderCorner(guiGraphicsExtractor, x - BORDER, y - BORDER, leftW + BORDER, topH + BORDER, false, false);
        // 右上 (水平翻转)
        renderCorner(guiGraphicsExtractor, x + leftW, y - BORDER, rightW + BORDER, topH + BORDER, true, false);
        // 左下 (垂直翻转)
        renderCorner(guiGraphicsExtractor, x - BORDER, y + topH, leftW + BORDER, bottomH + BORDER, false, true);
        // 右下 (双向翻转)
        renderCorner(guiGraphicsExtractor, x + leftW, y + topH, rightW + BORDER, bottomH + BORDER, true, true);
    }

    private void renderCorner(@NonNull GuiGraphicsExtractor gui, int x, int y, int w, int h, boolean flipX, boolean flipY) {
        int tx = ImageElement.TEXTURE_INSET_X;
        int ty = ImageElement.TEXTURE_INSET_X;

        float uStart = (float) tx / Context.TEXTURE_WIDTH;
        float uEnd = (float) (tx + w) / Context.TEXTURE_WIDTH;
        float vStart = (float) ty / Context.TEXTURE_HEIGHT;
        float vEnd = (float) (ty + h) / Context.TEXTURE_HEIGHT;

        float u0 = flipX ? uEnd : uStart;
        float u1 = flipX ? uStart : uEnd;
        float v0 = flipY ? vEnd : vStart;
        float v1 = flipY ? vStart : vEnd;

        gui.blit(
                Context.TEXTURE,
                x, y,
                x + w, y + h,
                u0, u1,
                v0, v1
        );
    }

    @Override
    public void format(@NonNull Context context) {
        this.font = context.getFont();
        this.setHeight(this.textureDisplayHeight);
        this.setWidth(this.textureDisplayWidth);
        if (border) {
            this.setHeight(this.getHeight() + BORDER * 2);
            this.setWidth(this.getWidth() + BORDER * 2);
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        int offset = border ? BORDER : 0;
        guiGraphicsExtractor.blit(
                RenderPipelines.GUI_TEXTURED,
                this.identifier,
                this.getXOffset() + offset, this.getYOffset() + offset,
                this.u, this.v,
                this.textureDisplayWidth, this.textureDisplayHeight,
                this.textureWidth, this.textureHeight
        );
        if (this.border) {
            drawFourWayBorder(guiGraphicsExtractor, this.getXOffset() + BORDER, this.getYOffset() + BORDER);
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
