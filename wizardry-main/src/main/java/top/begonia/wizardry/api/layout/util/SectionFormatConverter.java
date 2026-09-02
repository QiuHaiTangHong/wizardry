package top.begonia.wizardry.api.layout.util;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.data.definition.handbook.part.CentreConfigData;
import top.begonia.wizardry.client.data.definition.handbook.part.ContentsConfigData;
import top.begonia.wizardry.client.data.definition.handbook.part.SectionData;
import top.begonia.wizardry.api.layout.atom.IAtomElement;
import top.begonia.wizardry.api.layout.hybrid.CatalogueElement;
import top.begonia.wizardry.api.layout.hybrid.LineElement;
import top.begonia.wizardry.api.layout.hybrid.PageElement;
import top.begonia.wizardry.api.layout.hybrid.TitleElement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 章节格式化转换器
 * <p>负责将原始章节数据 ({@link SectionData}) 转换为一组格式化后的页面元素({@link PageElement}).
 * 支持对标题, 目录, 文本段落, 图像标记, 食谱标记等内容的解析与处理, 并根据中心对齐配置({@link CentreConfigData})
 * 进行最终的页面布局调整. 该类为内部工具类, 专注于数据转换与排版逻辑, 不涉及请求处理等基础设施关注点.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @since 1.0.0
 */
public final class SectionFormatConverter {
    private final String title;
    private final List<String> rawData;
    private final CentreConfigData centreConfigData;
    private List<PageElement> pageElements = new ArrayList<>();
    private final ContentsConfigData contentsConfigData;
    private int startPageIndex = 0;

    public SectionFormatConverter(@NonNull SectionData sectionData) {
        this.title = sectionData.title().orElse("");
        this.rawData = sectionData.text().orElse(new ArrayList<>());
        this.centreConfigData = sectionData.centre().orElse(new CentreConfigData(false, false));
        this.contentsConfigData = sectionData.contents().orElse(null);
    }

    public List<PageElement> getPageElements() {
        return this.pageElements;
    }

    public int getStartPageIndex() {
        return this.startPageIndex;
    }

    public void format(@NonNull Context context) {
        this.startPageIndex = context.getTotal();
        this.pageElements.clear();
        context.clear();
        Font font = context.getFont();
        List<IAtomElement> elementQueue = new ArrayList<>();
        if (this.title != null && !this.title.isEmpty()) {
            elementQueue.add(new TitleElement(this.title));
        }
        if (this.contentsConfigData != null) {
            Map<String, String> catalogueData = context.getCatalogueEntry().getOrDefault(this.contentsConfigData.id(), new LinkedHashMap<>());
            elementQueue.add(new CatalogueElement(catalogueData));
        }
        MutableComponent rootComponent = Component.empty();
        final Format formatUtil = Format.INSTANCE;
        for (String paragraph : rawData) {
            if (paragraph.startsWith(Format.Tags.FORMAT_MARKER.toString())) {
                String content = paragraph.substring(1).trim();
                if (content.startsWith(Format.Tags.IMAGE_TAG.toString())) {
                    formatUtil.createImage(content.substring(Format.Tags.IMAGE_TAG.toString().length()).trim(), context, elementQueue);
                } else if (content.startsWith(Format.Tags.RECIPE_TAG.toString())) {
                    formatUtil.createRecipe(content.substring(Format.Tags.RECIPE_TAG.toString().length()).trim(), context, elementQueue);
                } else {
                    rootComponent = formatUtil.createTextElement(context, font, elementQueue, rootComponent, paragraph);
                }
            } else {
                rootComponent = formatUtil.createTextElement(context, font, elementQueue, rootComponent, paragraph);
            }
        }
        for (IAtomElement element : elementQueue) {
            if (element instanceof TitleElement) {
                context.reserveSpace(element, 0, 0, false);
                context.reserveSpace(1, IAtomElement.EMPTY_ELEMENT, 0, 0, false);
            } else if (element instanceof LineElement) {
                context.reserveSpace(element, 1, 0, false);
            } else if (element == IAtomElement.EMPTY_ELEMENT) {
                context.reserveSpace(element, 0, 0, false);
            } else {
                context.reserveSpace(element, 0, 0, true);
                context.reserveSpace(IAtomElement.EMPTY_ELEMENT, 0, 0, false);
            }
        }
        this.pageElements = context.getPages();
        this.pageElements.forEach(pageElement -> pageElement.format(context, this.centreConfigData));
    }
}
