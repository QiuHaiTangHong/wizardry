package top.begonia.wizardry.api.client.config.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.client.config.utils.BuiltInConfig;
import top.begonia.wizardry.api.client.data.BuiltInData;
import top.begonia.wizardry.client.data.definition.hub.SkinsData;
import top.begonia.wizardry.core.registry.WizardrySpells;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SkinSelectionList extends AbstractContainerWidget {
    private static final Identifier MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/menu_list_background.png");
    private static final Identifier INWORLD_MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
    private static final int SEPARATOR_HEIGHT = 2;
    private static final int SEPARATOR_WIDTH = 2;
    protected final Minecraft minecraft;
    protected final int defaultEntryHeight;
    private final List<Entry> children = new ArrayList<>();
    private @Nullable Entry selected;
    private @Nullable Entry hovered;
    protected final SelectHUDSkinScreen containerScreen;

    public SkinSelectionList(
            Minecraft minecraft,
            SelectHUDSkinScreen containerScreen,
            int width, int height, int defaultEntryHeight
    ) {
        super(
                0, 0,
                width, height,
                CommonComponents.EMPTY,
                AbstractScrollArea.defaultSettings(defaultEntryHeight / 2)
        );
        this.minecraft = minecraft;
        this.defaultEntryHeight = defaultEntryHeight;
        this.containerScreen = containerScreen;
        ModConfigSpec.ConfigValue<String> value = this.containerScreen.skinValue;
        for (Map.Entry<String, SkinsData.Skin> stringSkinEntry : BuiltInData.getSkins().skins().entrySet()) {
            Entry entry = new Entry(this.minecraft.font, List.of(), stringSkinEntry);
            this.addEntry(entry);
            if (entry.getKey().equals(value.get())) {
                this.setFocused(entry);
            }
        }
    }

    //region 组件相关方法
    public void onChange() {
        String currentValue = this.selected != null ? this.selected.skinEntry.getKey() : null;
        ModConfigSpec.ConfigValue<String> skinValue = this.containerScreen.skinValue;
        if (currentValue != null) {
            this.containerScreen.undoManager.add(
                    skinValue::set,
                    currentValue,
                    skinValue::set,
                    skinValue.getDefault()
            );
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        boolean scrolling = this.updateScrolling(event);
        if (scrolling) {
            return true;
        }
        Optional<GuiEventListener> child = this.getChildAt(event.x(), event.y());
        if (child.isEmpty()) {
            return false;
        } else {
            GuiEventListener widget = child.get();
            if (widget.mouseClicked(event, doubleClick) && widget.shouldTakeFocusAfterInteraction()) {
                this.setFocused(widget);
                this.onChange();
                if (event.button() == 1) {
                    this.setDragging(true);
                }
            }

            return true;
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        Entry oldFocus = this.getFocused();
        if (oldFocus != focused && oldFocus instanceof ContainerEventHandler oldFocusContainer) {
            oldFocusContainer.setFocused(null);
        }
        this.selected = null;
        if (focused instanceof Entry entry) {
            int index = this.children.indexOf(entry);
            if (index >= 0) {
                this.selected = entry;
                boolean topClipped = selected.getContentY() < this.getY();
                boolean bottomClipped = selected.getContentBottom() > this.getBottom();
                if (this.minecraft.getLastInputType().isKeyboard() || topClipped || bottomClipped) {
                    this.scrollToEntry(selected);
                }
            }
        }
        super.setFocused(focused);
    }

    public @Nullable Entry getSelected() {
        return this.selected;
    }

    @Override
    public @Nullable Entry getFocused() {
        return (Entry) (super.getFocused());
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(@NonNull FocusNavigationEvent navigationEvent) {
        if (this.getItemCount() == 0) {
            return null;
        } else if (this.isFocused() && navigationEvent instanceof FocusNavigationEvent.ArrowNavigation arrowNavigation) {
            Entry entry = this.nextEntry(arrowNavigation.direction());
            if (entry != null) {
                return ComponentPath.path(this, ComponentPath.leaf(entry));
            } else {
                this.setFocused(null);
                return null;
            }
        } else if (!this.isFocused()) {
            Entry entry = this.getSelected();
            if (entry == null) {
                entry = this.nextEntry(navigationEvent.getVerticalDirectionForInitialFocus());
            }

            return entry == null ? null : ComponentPath.path(this, ComponentPath.leaf(entry));
        } else {
            return null;
        }
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public @NonNull List<? extends GuiEventListener> children() {
        return ImmutableList.copyOf(this.children);
    }
    //endregion

    //region 滚动条相关方法
    protected void scrollToEntry(@NonNull Entry entry) {
        int topDelta = entry.getY() - this.getY() - SEPARATOR_HEIGHT;
        if (topDelta < 0) {
            this.scroll(topDelta);
        }

        int bottomDelta = this.getBottom() - entry.getY() - entry.getHeight() - SEPARATOR_HEIGHT;
        if (bottomDelta < 0) {
            this.scroll(-bottomDelta);
        }
    }

    protected void scroll(int amount) {
        this.setScrollAmount(this.scrollAmount() + (double) amount);
    }

    public void setScrollAmount(double scrollAmount) {
        super.setScrollAmount(scrollAmount);
        this.repositionEntries();
    }
    //endregion

    //region Entry相关方法
    protected @Nullable Entry nextEntry(ScreenDirection dir) {
        return this.nextEntry(dir, (_) -> true, this.getSelected());
    }

    protected @Nullable Entry nextEntry(
            @NonNull ScreenDirection dir,
            Predicate<Entry> canSelect,
            @Nullable Entry startEntry
    ) {
        int delta = switch (dir) {
            case RIGHT, LEFT -> 0;
            case UP -> -1;
            case DOWN -> 1;
        };
        if (!this.children.isEmpty() && delta != 0) {
            int index;
            if (startEntry == null) {
                index = delta > 0 ? 0 : this.children.size() - 1;
            } else {
                index = this.children.indexOf(startEntry) + delta;
            }

            for (int i = index; i >= 0 && i < this.children.size(); i += delta) {
                Entry selected = this.children.get(i);
                if (canSelect.test(selected)) {
                    return selected;
                }
            }
        }

        return null;
    }

    private void repositionEntries() {
        int y = this.getFirstEntryY() - (int) this.scrollAmount();

        for (Entry child : this.children) {
            child.setY(y);
            y += child.getHeight();
            child.setX(this.getRowLeft());
            child.setWidth(this.getListWidth());
        }
    }

    private int getFirstEntryY() {
        return this.getY() + SEPARATOR_HEIGHT;
    }

    public int getNextY() {
        int y = this.getFirstEntryY() - (int) this.scrollAmount();

        for (Entry child : this.children) {
            y += child.getHeight();
        }

        return y;
    }

    protected int addEntry(Entry entry) {
        return this.addEntry(entry, this.defaultEntryHeight);
    }

    protected int addEntry(@NonNull Entry entry, int height) {
        entry.setX(this.getRowLeft());
        entry.setWidth(this.getListWidth());
        entry.setY(this.getNextY());
        entry.setHeight(height);
        this.children.add(entry);
        return this.children.size() - 1;
    }

    protected int getItemCount() {
        return this.children().size();
    }

    protected boolean entriesCanBeSelected() {
        return true;
    }

    protected final @Nullable Entry getEntryAtPosition(double posX, double posY) {
        for (Entry child : this.children) {
            if (child.isMouseOver(posX, posY)) {
                return child;
            }
        }
        return null;
    }
    //endregion

    //region 大小和位置相关方法
    public int getRowLeft() {
        return this.getX() + this.width / 2 - this.getRowWidth() / 2;
    }

    public int getRowWidth() {
        return 350;
    }

    public int getListWidth() {
        return (int) (this.getRowWidth() * 0.3);
    }

    protected int getListRight() {
        return this.getRowLeft() + this.getListWidth();
    }

    @Override
    protected int scrollBarX() {
        return this.getListRight() + this.scrollbarWidth() + SEPARATOR_WIDTH;
    }

    protected int scrollBarRight() {
        return this.scrollBarX() + this.scrollbarWidth();
    }

    protected int getImageContentWidth() {
        return this.width - this.scrollBarRight() - 2 * SEPARATOR_WIDTH;
    }

    protected int getImageContentHeight() {
        return this.height - 2 * SEPARATOR_HEIGHT;
    }

    protected int getImageContentXMiddle() {
        return this.scrollBarRight() + SEPARATOR_WIDTH + this.getImageContentWidth() / 2;
    }

    protected int getImageContentYMiddle() {
        return this.getY() + SEPARATOR_HEIGHT + this.getImageContentHeight() / 2;
    }

    public void updateSize(
            int width,
            @NonNull HeaderAndFooterLayout layout
    ) {
        this.updateSizeAndPosition(
                width,
                layout.getContentHeight(),
                layout.getHeaderHeight()
        );
    }

    public void updateSizeAndPosition(
            int width, int height,
            int y
    ) {
        this.updateSizeAndPosition(width, height, 0, y);
    }

    public void updateSizeAndPosition(
            int width, int height,
            int x, int y
    ) {
        this.setSize(width, height);
        this.setPosition(x, y);
        this.repositionEntries();
        if (this.getSelected() != null) {
            this.scrollToEntry(this.getSelected());
        }
        this.refreshScrollAmount();
    }

    @Override
    protected int contentHeight() {
        int totalHeight = 0;

        for (Entry child : this.children) {
            totalHeight += child.getHeight();
        }

        return totalHeight + 2 * SEPARATOR_HEIGHT;
    }
    //endregion

    //region 渲染相关方法
    @Override
    public @NonNull ScreenRectangle getRectangle() {
        if (hovered != null) {
            return new ScreenRectangle(
                    this.hovered.getX(),
                    this.hovered.getY(),
                    this.hovered.getWidth(),
                    this.hovered.getHeight()
            );
        }
        return super.getRectangle();
    }

    @Override
    public void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.hovered = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        this.setTooltip(hovered != null ? hovered.getTooltip() : null);
        this.extractListBackground(graphics);
        this.enableScissor(graphics);
        this.extractListItems(graphics);
        this.extractSelectedImage(graphics);
        graphics.disableScissor();
        this.extractListSeparators(graphics);
        this.extractScrollbar(graphics, mouseX, mouseY);
    }

    protected void extractSelection(
            @NonNull GuiGraphicsExtractor graphics,
            @NonNull Entry entry,
            int outlineColor
    ) {
        int outlineX0 = entry.getX();
        int outlineY0 = entry.getY();
        int outlineX1 = outlineX0 + entry.getWidth();
        int outlineY1 = outlineY0 + entry.getHeight();
        graphics.fill(outlineX0, outlineY0, outlineX1, outlineY1, outlineColor);
        graphics.fill(outlineX0 + 1, outlineY0 + 1, outlineX1 - 1, outlineY1 - 1, -16777216);
    }

    protected void extractListBackground(@NonNull GuiGraphicsExtractor graphics) {
        Identifier menuListBackground = this.minecraft.level == null ? MENU_LIST_BACKGROUND : INWORLD_MENU_LIST_BACKGROUND;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                menuListBackground,
                this.getX(), this.getY(),
                (float) this.getRight(),
                (float) (this.getBottom() + (int) this.scrollAmount()),
                this.getWidth(), this.getHeight(),
                32, 32
        );
    }

    protected void extractItem(GuiGraphicsExtractor graphics, Entry entry) {
        if (this.entriesCanBeSelected() && this.getSelected() == entry) {
            int outlineColor = this.isFocused() ? -1 : -8355712;
            this.extractSelection(graphics, entry, outlineColor);
        }

        entry.extractContent(graphics);
    }

    protected void extractListItems(GuiGraphicsExtractor graphics) {
        for (Entry child : this.children) {
            if (child.getY() + child.getHeight() >= this.getY() && child.getY() <= this.getBottom()) {
                this.extractItem(graphics, child);
            }
        }
    }

    protected void extractSelectedImage(GuiGraphicsExtractor graphics) {
        if (this.selected != null) {
            SkinsData.Skin skin = this.selected.skinEntry.getValue();
            int previewBorder = 10;
            int previewLeft = this.scrollBarRight() + SEPARATOR_WIDTH;
            int previewRight = previewLeft + this.getImageContentWidth() - previewBorder;
            int previewTop = this.getY() + previewBorder;
            int previewBottom = previewTop + this.getImageContentHeight() - previewBorder;

            float scale = Math.min((previewRight - previewLeft - 2 * previewBorder) / (float) skin.width(),
                    (previewBottom - previewTop - 2 * previewBorder) / (float) skin.height());
            float x = (previewLeft + previewRight) / 2f - (skin.width() * scale) / 2;
            float y = (previewBottom + previewTop) / 2f + (skin.height() * scale) / 2;
            Matrix3x2fStack pose = graphics.pose();
            pose.pushMatrix();
            pose.scale(scale, scale);
            skin.extractBackground(
                    graphics,
                    (int) (x / scale),
                    (int) (y / scale),
                    false, false,
                    WizardrySpells.MAGIC_MISSILE.get().getIcon(),
                    0.6f,
                    false, false
            );
            skin.extractText(
                    graphics,
                    (int) (x / scale),
                    (int) (y / scale),
                    false, false,
                    WizardrySpells.NONE.get().getDisplayNameWithFormatting(),
                    WizardrySpells.MAGIC_MISSILE.get().getDisplayNameWithFormatting(),
                    WizardrySpells.NONE.get().getDisplayNameWithFormatting(),
                    0
            );
            pose.popMatrix();
        }
    }

    protected void extractListSeparators(@NonNull GuiGraphicsExtractor graphics) {
        Identifier headerSeparator = this.minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
        Identifier footerSeparator = this.minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                headerSeparator,
                this.getX(), this.getY() - 2,
                0.0F, 0.0F,
                this.getWidth(), 2,
                32, 2
        );
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                footerSeparator,
                this.getX(), this.getBottom(),
                0.0F, 0.0F,
                this.getWidth(), 2,
                32, 2
        );
    }

    protected void enableScissor(@NonNull GuiGraphicsExtractor graphics) {
        graphics.enableScissor(Mth.clamp(this.getX(), 0, graphics.guiWidth()), Mth.clamp(this.getY(), 0, graphics.guiHeight()), Mth.clamp(this.getRight(), 0, graphics.guiWidth()), Mth.clamp(this.getBottom(), 0, graphics.guiHeight()));
    }
    //endregion

    public static class Entry implements LayoutElement, GuiEventListener {
        public static final int CONTENT_PADDING = 2;
        private int x = 0, y = 0;
        private int width = 0, height;
        private final Map.Entry<String, SkinsData.Skin> skinEntry;
        private final Font font;
        private final Component name;
        private Tooltip tooltip;

        protected Entry(
                Font font,
                List<String> path,
                Map.@NonNull Entry<String, SkinsData.Skin> skinEntry
        ) {
            this.font = font;
            this.skinEntry = skinEntry;
            this.name = Component.literal(BuiltInConfig.toTitleWords(skinEntry.getValue().name()));
            MutableComponent tooltip = this.name.copy().withStyle(ChatFormatting.GREEN);
            MutableComponent description = Component.literal(skinEntry.getValue().description()).withStyle(ChatFormatting.YELLOW);
            tooltip.append(BuiltInConfig.EMPTY_LINE).append(description);
            this.tooltip = Tooltip.create(tooltip);
        }

        public void setTooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
        }

        public void setCommon(Component common) {

        }

        public void extractContent(@NonNull GuiGraphicsExtractor graphics) {
            Component text = this.getEntryDisplayText();
            int textX = this.getContentXMiddle() - this.font.width(text) / 2;
            int textY = this.getContentYMiddle() - this.font.lineHeight / 2;
            graphics.text(
                    this.font,
                    text,
                    textX, textY,
                    -1
            );
        }

        public Tooltip getTooltip() {
            return this.tooltip;
        }

        public Component getEntryDisplayText() {
            return this.name;
        }

        public String getKey() {
            return this.skinEntry.getKey();
        }

        public SkinsData.Skin getSkin() {
            return this.skinEntry.getValue();
        }

        public int getContentPadding() {
            return CONTENT_PADDING;
        }

        public int getContentX() {
            return this.getContentPadding() + this.getX();
        }

        public int getContentY() {
            return this.getContentPadding() + this.getY();
        }

        public int getContentHeight() {
            return this.getHeight() - 2 * this.getContentPadding();
        }

        public int getContentYMiddle() {
            return this.getContentY() + this.getContentHeight() / 2;
        }

        public int getContentBottom() {
            return this.getContentY() + this.getContentHeight();
        }

        public int getContentWidth() {
            return this.getWidth() - 2 * this.getContentPadding();
        }

        public int getContentXMiddle() {
            return this.getContentX() + this.getContentWidth() / 2;
        }

        @Override
        public void setFocused(boolean focused) {
        }

        @Override
        public boolean isFocused() {
            return false;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.getRectangle().containsPoint((int) mouseX, (int) mouseY);
        }

        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
            return true;
        }

        @Override
        public void setX(int x) {
            this.x = x;
        }

        @Override
        public void setY(int y) {
            this.y = y;
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public @NonNull ScreenRectangle getRectangle() {
            return LayoutElement.super.getRectangle();
        }

        @Override
        public void visitWidgets(@NonNull Consumer<AbstractWidget> consumer) {

        }
    }
}
