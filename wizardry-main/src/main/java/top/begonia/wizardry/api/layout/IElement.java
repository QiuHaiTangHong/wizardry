package top.begonia.wizardry.api.layout;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.layout.atom.IAtomElement;
import top.begonia.wizardry.api.layout.util.Context;

/**
 * IElement 接口
 * <p> 定义了 GUI 元素的核心行为和属性规范, 整合了事件监听, 辅助功能和渲染能力
 * <p> 作为系统内部使用的抽象层, 封装了元素的坐标偏移管理, 可见性控制和格式化逻辑,
 * 所有实现类必须遵循统一的交互和展示协议
 * <p> 主要职责:
 * <ul>
 *   <li> 提供元素在画布上的绝对坐标偏移能力 </li>
 *   <li> 管理元素的可见性状态 </li>
 *   <li> 支持基于上下文的格式化操作 </li>
 *   <li> 通过继承接口统一事件响应, 旁白播报和渲染行为 </li>
 * </ul>
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @since 1.0.0
 */
public interface IElement extends GuiEventListener, NarratableEntry, Renderable {
    void format(Context context);

    boolean isVisible();

    void setXOffset(int x);

    void setYOffset(int y);

    int getXOffset();

    int getYOffset();

    IElement EMPTY_ELEMENT = new IAtomElement() {

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

    @Override
    default void setFocused(boolean b) {

    }

    @Override
    default void updateNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    default @NonNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    default boolean isFocused() {
        return false;
    }
}
