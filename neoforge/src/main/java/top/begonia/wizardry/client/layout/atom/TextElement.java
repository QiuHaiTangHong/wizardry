package top.begonia.wizardry.client.layout.atom;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.util.Context;


/**
 * 文本元素类
 * <p> 负责处理文本内容的格式化, 样式设置及渲染状态提取
 * <p> 该类实现了 AbstractAtomElement 接口, 提供文本显示的基本功能
 * <p> 主要功能包括:
 * <ul>
 *   <li> 设置文本颜色 </li>
 *   <li> 根据上下文格式化文本样式 (字体, 高度, 宽度等)</li>
 *   <li> 提取渲染状态供 GUI 渲染使用 </li>
 * </ul>
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @email mailto:2981263417@qq.com
 * @date 2026.05.03
 * @since 1.0.0
 */
public class TextElement extends AbstractAtomElement {
    private final FormattedCharSequence line;
    private int color;

    public TextElement(FormattedCharSequence line) {
        this.line = line;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void format(@NonNull Context context) {
        this.font = context.getFont();
        Font font = super.getFont();
        this.color = context.getColor("text");
        super.setHeight(font.lineHeight);
        super.setWidth(font.width(line));
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        guiGraphicsExtractor.text(this.getFont(), this.line, this.getXOffset(), this.getYOffset(), color, false);
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
