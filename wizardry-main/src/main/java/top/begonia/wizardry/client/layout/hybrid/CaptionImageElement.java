package top.begonia.wizardry.client.layout.hybrid;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.layout.atom.ImageElement;
import top.begonia.wizardry.client.layout.atom.TextElement;
import top.begonia.wizardry.client.data.definition.handbook.part.ImageData;
import top.begonia.wizardry.client.layout.util.Context;

/**
 * 带标题的图像元素类
 * <p> 该类继承自 AbstractHybridElement, 主要用于处理带有标题说明的图像渲染逻辑. 它支持在图像下方添加文本标题, 并根据上下文样式进行格式化. 当图像没有标题时, 仅渲染图像本身. 当有标题时, 会计算图像和标题的最佳布局, 确保两者水平居中对齐, 并垂直排列.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @email mailto:2981263417@qq.com
 * @date 2026.05.03
 * @since 1.0.0
 */
public class CaptionImageElement extends AbstractHybridElement {
    private final ImageData rawData;
    private static final int CAPTION_OFFSET = 4;

    public CaptionImageElement(@NonNull ImageData imageData) {
        this.rawData = imageData;
    }

    @Override
    public void format(@NonNull Context context) {
        this.clearElements();
        Integer color = context.getColor("caption");
        ImageElement imageElement = new ImageElement(this.rawData);
        imageElement.format(context);
        if (!this.rawData.caption().isEmpty()) {
            FormattedCharSequence caption = FormattedCharSequence.forward(this.rawData.caption(), Style.EMPTY);
            TextElement textElement = new TextElement(caption);
            textElement.format(context);
            textElement.setYOffset(imageElement.getHeight() + CAPTION_OFFSET);
            textElement.setColor(color);
            this.setWidth(Math.max(imageElement.getWidth(), textElement.getWidth()));
            this.setHeight(imageElement.getHeight() + textElement.getHeight() + CAPTION_OFFSET);
            imageElement.setXOffset((this.getWidth() - imageElement.getWidth()) / 2);
            textElement.setXOffset((this.getWidth() - textElement.getWidth()) / 2);
            this.addElement(imageElement);
            this.addElement(textElement);
        } else {
            this.setWidth(imageElement.getWidth());
            this.setHeight(imageElement.getHeight());
            this.addElement(imageElement);
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
