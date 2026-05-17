package top.begonia.wizardry.client.layout.atom;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.layout.util.Context;

/**
 * 链接元素类, 负责处理文本链接的渲染和交互逻辑喵.
 * 该类继承自 AbstractAtomElement, 主要用于在 GUI 界面中显示可交互的文本链接,
 * 根据用户的鼠标悬停状态动态切换激活状态和未激活状态的文本颜色喵.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @email mailto:2981263417@qq.com
 * @date 2026.05.03
 * @since 1.0.0
 */
public class LinkElement extends AbstractAtomElement {
    private FormattedCharSequence activeFormattedCharSequence;
    private FormattedCharSequence inactiveFormattedCharSequence;
    private @Nullable LinkElement associatedElement;
    private String target;

    public LinkElement(FormattedCharSequence inactiveFormattedCharSequence, String target) {
        this.inactiveFormattedCharSequence = inactiveFormattedCharSequence;
        this.activeFormattedCharSequence = (sink) -> inactiveFormattedCharSequence.accept((index, style, codepoint) -> sink.accept(index, style.withColor(ARGB.color(255, 255, 0, 0)), codepoint));
        this.target = target;
    }

    public void setAssociatedElement(@Nullable LinkElement associatedElement) {
        this.associatedElement = associatedElement;
    }

    @Override
    public void format(@NonNull Context context) {
        this.font = context.getFont();
        this.setHeight(context.getFont().lineHeight - 1);
        this.setWidth(this.font.width(this.inactiveFormattedCharSequence));
        int hyperlinkWeight = context.getColor("hyperlink");
        int highlightWeight = context.getColor("highlight");
        final FormattedCharSequence baseSequence = this.inactiveFormattedCharSequence;
        this.inactiveFormattedCharSequence = (sink) -> baseSequence.accept((index, style, codepoint) -> sink.accept(index, style.withColor(hyperlinkWeight), codepoint));
        this.activeFormattedCharSequence = (sink) -> baseSequence.accept((index, style, codepoint) -> sink.accept(index, style.withColor(highlightWeight), codepoint));
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        if (this.isMouseOver(mouseX, mouseY) || (this.associatedElement != null && this.associatedElement.isMouseOver(mouseX, mouseY))) {
            guiGraphicsExtractor.text(this.getFont(), this.activeFormattedCharSequence, this.getXOffset(), this.getYOffset(), ARGB.color(255, 0, 255, 0), false);
        } else {
            guiGraphicsExtractor.text(this.getFont(), this.inactiveFormattedCharSequence, this.getXOffset(), this.getYOffset(), ARGB.color(255, 0, 255, 0), false);
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        return false;
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
