package top.begonia.wizardry.client.layout.container.handbook;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.*;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.data.definition.handbook.part.ContentsConfigData;
import top.begonia.wizardry.client.layout.container.IContainerElement;
import top.begonia.wizardry.client.data.definition.handbook.part.CentreConfigData;
import top.begonia.wizardry.client.data.definition.handbook.part.SectionData;
import top.begonia.wizardry.client.layout.atom.IAtomElement;
import top.begonia.wizardry.client.layout.hybrid.CatalogueElement;
import top.begonia.wizardry.client.layout.hybrid.LineElement;
import top.begonia.wizardry.client.layout.hybrid.PageElement;
import top.begonia.wizardry.client.layout.hybrid.TitleElement;
import top.begonia.wizardry.client.layout.util.Context;
import top.begonia.wizardry.client.layout.util.Format;

import java.util.*;

/**
 * 章节 (部分) 元素类
 * <p>该类负责处理手册中的一个章节内容, 包括章节的布局计算, 页面渲染, 文本格式化, 链接处理以及鼠标交互等功能.
 * 它实现了 IContainerElement 接口, 用于管理原子元素列表, 并根据上下文信息动态生成章节的显示页面.
 * 支持的富文本特性包括颜色标签, 超链接, 图片, 配方说明以及特定的格式标记替换.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @email mailto:2981263417@qq.com
 * @date 2026.05.02
 * @since 1.0.0
 */
public class SectionElement implements IContainerElement {
    private final String title;
    private int xOffset, yOffset;
    private final List<String> rawData;
    private int pageCount;
    private final CentreConfigData centreConfigData;
    private List<PageElement> pageElements = new ArrayList<>();
    private final List<IAtomElement> displayPage = new ArrayList<>();
    private final ContentsConfigData contentsConfigData;
    private int currentPageIndex = 0;

    public enum PageState {
        HAS_TWO_PAGE(0),
        ONLY_LEFT_PAGE(-1),
        NOT_TWO_PAGE(1),
        ONLY_RIGHT_PAGE(2);
        private final int state;

        PageState(int state) {
            this.state = state;
        }

        @SuppressWarnings("unused")
        public int getState() {
            return state;
        }
    }

    public SectionElement(@NonNull SectionData sectionData) {
        this.title = sectionData.title().orElse("");
        this.rawData = sectionData.text().orElse(new ArrayList<>());
        this.centreConfigData = sectionData.centre().orElse(new CentreConfigData(false, false));
        this.contentsConfigData = sectionData.contents().orElse(null);
    }

    public PageState next() {
        if (this.pageElements.isEmpty() || this.currentPageIndex >= this.pageCount - 1) {
            this.displayPage.clear();
            return PageState.NOT_TWO_PAGE;
        }
        int remaining = this.pageCount - 1 - this.currentPageIndex;
        this.displayPage.clear();
        if (remaining >= 2) {
            this.displayPage.add(this.pageElements.get(this.currentPageIndex + 1));
            this.displayPage.add(this.pageElements.get(this.currentPageIndex + 2));
            this.currentPageIndex += 2;
            return PageState.HAS_TWO_PAGE;
        } else if (remaining == 1) {
            this.displayPage.add(IAtomElement.EMPTY_ELEMENT);
            this.displayPage.add(this.pageElements.get(this.currentPageIndex + 1));
            this.currentPageIndex += 1;
            return PageState.ONLY_RIGHT_PAGE;
        }
        return PageState.NOT_TWO_PAGE;
    }

    public PageState prev() {
        if (this.pageElements.isEmpty() || this.currentPageIndex == 0) {
            this.displayPage.clear();
            return PageState.NOT_TWO_PAGE;
        }
        int remaining;
        if (this.displayPage.getLast() != IAtomElement.EMPTY_ELEMENT) {
            remaining = this.currentPageIndex - 1;
            this.currentPageIndex -= 1;
        } else {
            remaining = this.currentPageIndex;
        }
        this.displayPage.clear();
        if (remaining >= 2) {
            this.displayPage.add(this.pageElements.get(this.currentPageIndex - 2));
            this.displayPage.add(this.pageElements.get(this.currentPageIndex - 1));
            this.currentPageIndex -= 1;
            return PageState.HAS_TWO_PAGE;
        } else if (remaining == 1) {
            this.displayPage.add(this.pageElements.getFirst());
            this.displayPage.add(IAtomElement.EMPTY_ELEMENT);
            this.currentPageIndex = 0;
            return PageState.ONLY_LEFT_PAGE;
        }
        return PageState.NOT_TWO_PAGE;
    }

    public void fillViewForward(int startIndex, int pageSize) {
        this.displayPage.clear();
        this.currentPageIndex = Math.clamp(startIndex, 0, this.pageCount - 1);
        for (int i = 0; i < pageSize; i++) {
            int target = startIndex + i;
            if (target < this.pageCount) {
                this.displayPage.add(this.pageElements.get(target));
                this.currentPageIndex = target;
            } else {
                this.displayPage.add(IAtomElement.EMPTY_ELEMENT);
            }
        }
        for (int i = 0; i < 2 - this.displayPage.size(); i++) {
            this.displayPage.add(IAtomElement.EMPTY_ELEMENT);
        }
    }

    public void fillViewBackward(int endIndex, int pageSize) {
        this.displayPage.clear();
        this.currentPageIndex = Math.clamp(endIndex, 0, this.pageCount - 1);
        int leftMostIndex = endIndex - pageSize + 1;
        for (int i = 0; i < pageSize; i++) {
            int target = leftMostIndex + i;
            if (target >= 0 && target < this.pageCount) {
                this.displayPage.add(this.pageElements.get(target));
            } else {
                this.displayPage.add(IAtomElement.EMPTY_ELEMENT);
            }
        }
        for (int i = 0; i < 2 - this.displayPage.size(); i++) {
            this.displayPage.add(IAtomElement.EMPTY_ELEMENT);
        }
    }

    public int getCurrentPageIndex() {
        return this.currentPageIndex;
    }

    public int getRemainingPage() {
        return this.pageCount - this.currentPageIndex;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    @Override
    public void format(@NonNull Context context) {
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
        this.pageCount = this.pageElements.size();
        if (!this.pageElements.isEmpty()) {
            this.displayPage.add(IAtomElement.EMPTY_ELEMENT);
            this.displayPage.add(this.pageElements.getFirst());
        }
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        if (this.displayPage.size() == 2) {
            this.displayPage.getFirst().extractRenderState(
                    guiGraphicsExtractor,
                    mouseX, mouseY,
                    partialTick
            );
            this.displayPage.getLast().extractRenderState(
                    guiGraphicsExtractor,
                    mouseX, mouseY,
                    partialTick
            );
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        for (IAtomElement iHandbookElement : this.displayPage) {
            if (iHandbookElement.mouseClicked(event, doubleClick)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        for (IAtomElement iHandbookElement : this.displayPage) {
            if (iHandbookElement.isMouseOver(mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
        for (int i = 0; i < this.displayPage.size(); i++) {
            this.displayPage.get(i).setXOffset(xOffset + ((i + 1) % 2 == 0 ? 150 : 16));
        }
    }

    @Override
    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
        this.displayPage.forEach(iAtomElement -> iAtomElement.setYOffset(yOffset + 16));
    }

    @Override
    public int getXOffset() {
        return this.xOffset;
    }

    @Override
    public int getYOffset() {
        return this.yOffset;
    }
}
