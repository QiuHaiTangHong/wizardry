package top.begonia.wizardry.client.layout.atom;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.IElement;
import top.begonia.wizardry.client.layout.util.Context;

/**
 * 原子元素接口
 * <p>定义了 UI 组件中基础原子元素的行为规范, 包括渲染, 可读性, 事件监听和布局定位等核心功能.
 * <p>该接口继承自 GuiEventListener, NarratableEntry 和 Renderable, 旨在为 Minecraft GUI 系统中的简单图形元素 (如文本, 图标等) 提供统一的行为标准.
 * <p>主要功能包括:
 * <ul>
 *   <li><b>渲染与布局</b>: 通过 {@code extractRenderState} 处理渲染状态, 通过 {@code format} 调整元素布局, 并提供宽高设置与偏移量管理.</li>
 *   <li><b>交互与状态</b>: 支持焦点状态的切换 ({@code setFocused}, {@code isFocused}) 和 Narration 可读性输出({@code updateNarration}).</li>
 *   <li><b>可见性控制</b>: 通过 {@code isVisible} 方法控制元素是否在界面上显示.</li>
 * </ul>
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @email mailto:2981263417@qq.com
 * @date 2026.05.02
 * @since 1.0.0
 */
public interface IAtomElement extends IElement {
    int getHeight();

    int getWidth();

    void setHeight(int height);

    void setWidth(int width);

    default void drawBorder(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int color) {
        int x1 = this.getXOffset();
        int y1 = this.getYOffset();
        int x2 = x1 + this.getWidth();
        int y2 = y1 + this.getHeight();

        if (this.getWidth() >= 1 && this.getHeight() >= 1) {
            // 上
            guiGraphicsExtractor.fill(x1, y1, x2, y1 + 1, color);
            // 下
            guiGraphicsExtractor.fill(x1, y2 - 1, x2, y2, color);
            // 左
            guiGraphicsExtractor.fill(x1, y1 + 1, x1 + 1, y2 - 1, color);
            // 右
            guiGraphicsExtractor.fill(x2 - 1, y1 + 1, x2, y2 - 1, color);
        }
    }

    IAtomElement EMPTY_ELEMENT = new IAtomElement() {

        @Override
        public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {

        }

        @Override
        public void format(Context context) {
        }

        @Override
        public boolean isVisible() {
            return true;
        }

        @Override
        public int getHeight() {
            return 9;
        }

        @Override
        public int getWidth() {
            return 0;
        }

        @Override
        public void setHeight(int height) {

        }

        @Override
        public void setWidth(int width) {

        }

        @Override
        public void setXOffset(int x) {

        }

        @Override
        public void setYOffset(int y) {

        }

        @Override
        public int getXOffset() {
            return 0;
        }

        @Override
        public int getYOffset() {
            return 0;
        }
    };
}
