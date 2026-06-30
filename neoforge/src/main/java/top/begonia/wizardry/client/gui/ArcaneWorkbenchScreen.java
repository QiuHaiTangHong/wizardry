package top.begonia.wizardry.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.client.util.DrawingUtils;
import top.begonia.wizardry.client.util.GlyphGenerator;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.client.gui.widget.SpellSortButton;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.inventory.menu.ArcaneWorkbenchMenu;
import top.begonia.wizardry.client.util.ISpellSortable;
import top.begonia.wizardry.core.item.IManaStoringItem;
import top.begonia.wizardry.core.item.ISpellCastingItem;
import top.begonia.wizardry.core.item.IWorkbenchItem;
import top.begonia.wizardry.core.item.impl.SpellBookItem;
import top.begonia.wizardry.core.item.impl.WandItem;
import top.begonia.wizardry.core.network.data.ControlInputPayload;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.WandHelper;

import java.util.ArrayList;
import java.util.List;

public class ArcaneWorkbenchScreen extends AbstractContainerScreen<ArcaneWorkbenchMenu> {
    public static final Identifier texture = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/container/arcane_workbench.png");
    public static final int BOOKSHELF_UI_WIDTH = ArcaneWorkbenchMenu.BOOKSHELF_UI_WIDTH;
    public static final int MAIN_GUI_WIDTH = 176;
    private static final int TOOLTIP_WIDTH = 144;
    private static final int TOOLTIP_BORDER = 6;
    private static final int LINE_SPACING_WIDE = 5;
    private static final int LINE_SPACING_NARROW = 1;
    private static final int RUNE_LEFT = 38;
    private static final int RUNE_TOP = 22;
    private static final int RUNE_WIDTH = 100;
    private static final int RUNE_HEIGHT = 100;

    private static final int SCROLL_BAR_LEFT = 102;
    private static final int SCROLL_BAR_TOP = 34;
    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 178;
    private static final int SCROLL_HANDLE_HEIGHT = 15;

    private static final int HALO_DIAMETER = 156;

    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 512;
    private static final int ANIMATION_DURATION = 20;
    private static final int SEARCH_TOOLTIP_HOVER_TIME = 20;
    private static final Style TOOLTIP_SYNTAX = Style.EMPTY.withColor(ChatFormatting.YELLOW);
    private static final Style TOOLTIP_BODY = Style.EMPTY.withColor(ChatFormatting.WHITE);
    private final Button[] sortButtons = new Button[3];
    public int bookshelfLabelX;
    public int bookshelfLabelY;
    @SuppressWarnings("FieldMayBeFinal")
    private Component bookshelfTitle;
    private Button applyBtn;
    private Button clearBtn;
    private EditBox searchEditBox;
    private int animationTimer = 0;
    private float scroll = 0;
    private boolean scrolling = false;
    private final List<TooltipElement> tooltipElements = new ArrayList<>();

    public ArcaneWorkbenchScreen(ArcaneWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, MAIN_GUI_WIDTH, 220);
        this.bookshelfLabelX = 8 - BOOKSHELF_UI_WIDTH;
        this.bookshelfLabelY = 6;
        this.bookshelfTitle = Component.translatable("container." + Wizardry.MODID + ".bookshelf");

    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(this.clearBtn = new SimpleButton(
                this.leftPos + 152, this.topPos + 94,
                16,
                Component.translatable("container." + Wizardry.MODID + ".arcane_workbench.apply"),
                _ -> {
                    ControlInputPayload payload = new ControlInputPayload(ControlInputPayload.ControlType.CLEAR_BUTTON);
                    ClientPacketDistributor.sendToServer(payload);
                    Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(WizardrySounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 0.8f)
                    );
                    this.animationTimer = 20;
                }
        ));
        this.addRenderableWidget(this.applyBtn = new SimpleButton(
                this.leftPos + 152, this.topPos + 113,
                0,
                Component.translatable("container." + Wizardry.MODID + ".arcane_workbench.clear"),
                _ -> {
                    ControlInputPayload payload = new ControlInputPayload(ControlInputPayload.ControlType.APPLY_BUTTON);
                    ClientPacketDistributor.sendToServer(payload);
                    Minecraft.getInstance().getSoundManager().play(
                            SimpleSoundInstance.forUI(WizardrySounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 0.8f)
                    );
                    this.animationTimer = 20;
                }
        ));
        this.addRenderableWidget(sortButtons[0] = new SpellSortButton(
                this.leftPos + 78 - BOOKSHELF_UI_WIDTH, this.topPos + 8,
                ISpellSortable.SortType.TIER,
                this.menu,
                this,
                button -> {
                }
        ));
        this.addRenderableWidget(sortButtons[1] = new SpellSortButton(
                this.leftPos + 91 - BOOKSHELF_UI_WIDTH, this.topPos + 8,
                ISpellSortable.SortType.ELEMENT,
                this.menu,
                this,
                button -> {
                }
        ));
        this.addRenderableWidget(sortButtons[2] = new SpellSortButton(
                this.leftPos + 104 - BOOKSHELF_UI_WIDTH, this.topPos + 8,
                ISpellSortable.SortType.ALPHABETICAL,
                this.menu,
                this,
                button -> {
                }
        ));
        for (Button sortButton : this.sortButtons) {
            sortButton.active = false;
        }
        this.searchEditBox = new EditBox(
                this.font,
                this.leftPos - 113, this.topPos + 22,
                104, this.font.lineHeight,
                Component.empty()
        );
        this.searchEditBox.setMaxLength(50);
        this.searchEditBox.setBordered(false);
        this.searchEditBox.setVisible(true);
        this.searchEditBox.setTextColor(16777215);
        this.searchEditBox.setCanLoseFocus(ClientConfig.unfocusedSearchBars);
        this.searchEditBox.setFocused(!ClientConfig.unfocusedSearchBars);
        this.addRenderableWidget(this.searchEditBox);
        this.tooltipElements.clear();
        this.tooltipElements.add(new TooltipElementItemName(Style.EMPTY.withColor(ChatFormatting.WHITE), LINE_SPACING_WIDE));
        this.tooltipElements.add(new TooltipElementManaReadout(LINE_SPACING_WIDE));
        this.tooltipElements.add(new TooltipElementProgressionBar(LINE_SPACING_WIDE));
        this.tooltipElements.add(new TooltipElementSpellList(LINE_SPACING_WIDE));
        this.tooltipElements.add(new TooltipElementUpgradeList(LINE_SPACING_WIDE));
    }

    protected void extractLabels(@NonNull GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        if (this.menu.hasBookshelves()) {
            graphics.text(this.font, this.bookshelfTitle, this.bookshelfLabelX, this.bookshelfLabelY, -12566464, false);
        }
    }

    public int rightExtensionWidth() {
        return this.menu.hasBookshelves() ? BOOKSHELF_UI_WIDTH : 0;
    }

    public int leftExtensionWidth() {
        return this.menu.slots.get(ArcaneWorkbenchMenu.CENTRE_SLOT).hasItem() ? TOOLTIP_WIDTH : 0;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        this.updateModuleState();
        this.menu.updateSlotPosition();
        this.extractBackground(graphics, mouseX, mouseY, partialTicks);
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
    }

    public void updateModuleState() {
        //更新排序按钮位置
        for (Button button : this.sortButtons) {
            button.visible = this.menu.hasBookshelves();
        }
        this.clearBtn.active = this.applyBtn.active = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).hasItem();
    }

    @Override
    public void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        if (this.menu.slots.get(ArcaneWorkbenchMenu.CENTRE_SLOT).hasItem()) {

            ItemStack stack = this.menu.slots.get(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

            if (!(stack.getItem() instanceof IWorkbenchItem)) {
                Wizardry.LOGGER.warn("奥术工作台中央槽位的无效物品, 怎么会出现在那里?!, Render 阶段: extractContents");
                return;
            }

            if (((IWorkbenchItem) stack.getItem()).showTooltip(stack)) {

                int x = leftPos + MAIN_GUI_WIDTH + TOOLTIP_BORDER;
                int y = TOOLTIP_BORDER + this.topPos;

                for (TooltipElement element : this.tooltipElements) {
                    y = element.drawForegroundLayer(graphics, x, y, stack, partialTicks, mouseX, mouseY);
                }
            }
        }
    }

    protected void extractSlots(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        this.menu.inventoryInfoIterator(ArcaneWorkbenchMenu.EmbeddedSlot.STAFF_SLOT, (_, slot) -> {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, texture,
                    slot.x - 8, slot.y - 8,
                    2, 222,
                    32, 32,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT
            );
        });
        for (Slot slot : this.menu.slots) {
            if (slot.isActive()) {
                this.extractSlot(graphics, slot, mouseX, mouseY);
            }
        }

    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        int leftXOffset = this.menu.hasBookshelves() ? -BOOKSHELF_UI_WIDTH : 0;
        if (this.menu.hasBookshelves()) {
            // 绘制书橱背景
            DrawingUtils.drawTexturedRect(
                    graphics, texture,
                    this.leftPos + leftXOffset, this.topPos,
                    0, 256,
                    BOOKSHELF_UI_WIDTH, this.getImageHeight(),
                    TEXTURE_WIDTH, TEXTURE_HEIGHT
            );

            // 绘制滚动条
            DrawingUtils.drawTexturedRect(
                    graphics, texture,
                    this.leftPos + leftXOffset + SCROLL_BAR_LEFT, (int) (this.topPos + SCROLL_BAR_TOP + (scroll * (SCROLL_BAR_HEIGHT - SCROLL_HANDLE_HEIGHT) + 0.5f)),
                    getMaxScrollRows() > 0 ? 0 : SCROLL_BAR_WIDTH, 476,
                    SCROLL_BAR_WIDTH, SCROLL_HANDLE_HEIGHT,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT
            );
        }
        //灰色背景
        DrawingUtils.drawTexturedRect(
                graphics, texture,
                this.leftPos + RUNE_LEFT, this.topPos + RUNE_TOP,
                MAIN_GUI_WIDTH + TOOLTIP_WIDTH, 0,
                RUNE_WIDTH, RUNE_HEIGHT,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
        //黄色动画背景
        if (animationTimer > 0) {
            Matrix3x2fStack poseStack = graphics.pose();
            poseStack.pushMatrix();
            int x = this.leftPos + RUNE_LEFT + RUNE_WIDTH / 2;
            int y = this.topPos + RUNE_TOP + RUNE_HEIGHT / 2;

            float scale = (animationTimer + partialTicks) / ANIMATION_DURATION;
            scale = (float) (1 - Math.pow(1 - scale, 1.4f)); // Makes it slower at the start and speed up
            poseStack.scale(scale, scale);
            poseStack.translate(x / scale, y / scale);

            DrawingUtils.drawTexturedRect(graphics, texture, -HALO_DIAMETER / 2, -HALO_DIAMETER / 2, MAIN_GUI_WIDTH + TOOLTIP_WIDTH, RUNE_HEIGHT,
                    HALO_DIAMETER, HALO_DIAMETER, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            poseStack.popMatrix();
        }
        //主仓库背景
        DrawingUtils.drawTexturedRect(
                graphics, texture,
                this.leftPos, this.topPos,
                0, 0,
                MAIN_GUI_WIDTH, this.getImageHeight(),
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );
        if (this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).hasItem()) {
            ItemStack stack = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();
            if (!(stack.getItem() instanceof IWorkbenchItem)) {
                Wizardry.LOGGER.warn("奥术工作台中央槽位的无效物品, 怎么会出现在那里?!, Render 阶段: extractBackground");
                return;
            }

            if (((IWorkbenchItem) stack.getItem()).showTooltip(stack)) {

                int tooltipHeight = tooltipElements.stream().mapToInt(e -> e.getTotalHeight(stack)).sum() - tooltipElements.getLast().spaceAfter;

                // Tooltip box
                DrawingUtils.drawTexturedRect(graphics, texture, this.leftPos + MAIN_GUI_WIDTH, this.topPos, MAIN_GUI_WIDTH, 0, TOOLTIP_WIDTH,
                        TOOLTIP_BORDER + tooltipHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
                DrawingUtils.drawTexturedRect(graphics, texture,
                        this.leftPos + MAIN_GUI_WIDTH, this.topPos + TOOLTIP_BORDER + tooltipHeight,
                        MAIN_GUI_WIDTH, this.imageHeight - TOOLTIP_BORDER, TOOLTIP_WIDTH, TOOLTIP_BORDER, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                int x = this.leftPos + MAIN_GUI_WIDTH + TOOLTIP_BORDER;
                int y = this.topPos + TOOLTIP_BORDER;

                for (TooltipElement element : this.tooltipElements) {
                    y = element.drawBackgroundLayer(graphics, x, y, stack, partialTicks, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (this.animationTimer > 0) {
            this.animationTimer--;
        }
    }

    private int getMaxScrollRows() {
        return Math.max(0, Mth.ceil((float) this.menu.getActiveBookshelfSlots().size()
                / ArcaneWorkbenchMenu.BOOKSHELF_SLOTS_X) - ArcaneWorkbenchMenu.BOOKSHELF_SLOTS_Y);
    }

    private TooltipElement @NonNull [] generateSpellEntries(int count) {
        TooltipElement[] entries = new TooltipElement[count];
        for (int i = 0; i < count; i++) {
            entries[i] = new TooltipElementSpellEntry(i);
        }
        return entries;
    }

    private static class SimpleButton extends Button {
        private final int extraOffsetX;

        public SimpleButton(int x, int y, int extraOffsetX, Component message, OnPress onPress) {
            super(
                    x, y,
                    16, 16,
                    message,
                    onPress,
                    DEFAULT_NARRATION
            );
            this.extraOffsetX = extraOffsetX;
            this.active = false;
        }

        @Override
        protected void extractContents(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
            int u = 72;
            int v = 220 + extraOffsetX;
            if (this.isActive()) {
                if (this.isHovered) {
                    u += this.width * 2;
                }
            } else {
                u += this.width;
            }

            guiGraphicsExtractor.blit(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    this.getX(), this.getY(),
                    u, v,
                    this.width, this.height,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT
            );
        }
    }

    private abstract class TooltipElement {

        private final TooltipElement[] children;
        private final int spaceAfter;

        public TooltipElement(int spaceAfter, TooltipElement... children) {
            this.children = children;
            this.spaceAfter = spaceAfter;
        }

        public int getTotalHeight(ItemStack stack) {
            if (!this.isVisible(stack)) return 0;
            int height = this.getHeight(stack);
            for (TooltipElement child : children) {
                height += child.getTotalHeight(stack);
            }
            return height + spaceAfter;
        }

        public int drawBackgroundLayer(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            if (!this.isVisible(stack)) {
                return y;
            }
            this.drawBackground(guiGraphicsExtractor, x, y, stack, partialTicks, mouseX, mouseY);
            y += this.getHeight(stack);
            for (TooltipElement child : children) {
                y = child.drawBackgroundLayer(guiGraphicsExtractor, x, y, stack, partialTicks, mouseX, mouseY);
            }
            return y + spaceAfter;
        }

        public int drawForegroundLayer(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            if (!this.isVisible(stack)) return y;
            this.drawForeground(guiGraphicsExtractor, x, y, stack, partialTicks, mouseX, mouseY);
            y += this.getHeight(stack);
            for (TooltipElement child : children) {
                y = child.drawForegroundLayer(guiGraphicsExtractor, x, y, stack, partialTicks, mouseX, mouseY);
            }
            return y + spaceAfter;
        }

        protected Font getFont(ItemStack stack) {
            return ArcaneWorkbenchScreen.this.getFont();
        }

        protected abstract boolean isVisible(ItemStack stack);

        protected abstract int getHeight(ItemStack stack);

        protected abstract void drawBackground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY);

        protected abstract void drawForeground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY);

    }

    private class TooltipElementText extends TooltipElement {

        private final MutableComponent text;
        private final Style style;

        public TooltipElementText(MutableComponent text, Style style, int spaceAfter, TooltipElement... children) {
            super(spaceAfter, children);
            this.text = text;
            this.style = style;
        }

        protected MutableComponent getText(ItemStack stack) {
            return text;
        }

        protected Style getColour(ItemStack stack, float partialTicks) {
            return this.style;
        }

        @Override
        protected boolean isVisible(ItemStack stack) {
            return true;
        }

        @Override
        protected int getHeight(ItemStack stack) {
            return getFont(stack).split(getText(stack), TOOLTIP_WIDTH - 2 * TOOLTIP_BORDER)
                    .size() * getFont(stack).lineHeight;
        }

        @Override
        protected void drawBackground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            // Nothing here because this element is only text!
        }

        @Override
        protected void drawForeground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            for (FormattedCharSequence line : getFont(stack).split(getText(stack), TOOLTIP_WIDTH - 2 * TOOLTIP_BORDER)) {
                FormattedCharSequence styledLine = (sink) -> line.accept((index, oldStyle, codepoint) -> {
                    Style finalStyle = oldStyle.applyTo(this.getColour(stack, partialTicks));
                    return sink.accept(index, finalStyle, codepoint);
                });
                guiGraphicsExtractor.text(this.getFont(stack), styledLine, x, y, ARGB.color(255, 255, 255, 255), false);
                y += getFont(stack).lineHeight;
            }
        }
    }

    private class TooltipElementItemName extends TooltipElementText {

        public TooltipElementItemName(Style style, int spaceAfter) {
            super(null, style, spaceAfter);
        }

        @Override
        protected @NonNull MutableComponent getText(@NonNull ItemStack stack) {
            return stack.getItem().getName(stack).copy();
        }

    }

    private class TooltipElementManaReadout extends TooltipElementText {

        public TooltipElementManaReadout(int spaceAfter) {
            super(null, Style.EMPTY.withColor(ChatFormatting.BLUE), spaceAfter);
        }

        @Contract("_ -> new")
        @Override
        protected @NonNull MutableComponent getText(@NonNull ItemStack stack) {
            return Component.translatable("container." + Wizardry.MODID + ".arcane_workbench.mana",
                    ((IManaStoringItem) stack.getItem()).getMana(stack),
                    ((IManaStoringItem) stack.getItem()).getManaCapacity(stack));
        }

        @Override
        protected boolean isVisible(@NonNull ItemStack stack) {
            return stack.getItem() instanceof IManaStoringItem && ((IManaStoringItem) stack.getItem()).showManaInWorkbench(Minecraft.getInstance().player, stack);
        }

    }

    private class TooltipElementProgressionBar extends TooltipElement {

        private static final int PROGRESSION_BAR_WIDTH = 131;
        private static final int PROGRESSION_BAR_HEIGHT = 3;

        public TooltipElementProgressionBar(int spaceAfter) {
            super(spaceAfter);
        }

        @Override
        protected boolean isVisible(@NonNull ItemStack stack) {
            return stack.getItem() instanceof WandItem && !ServerConfig.legacyWandLevelling;
        }

        @Override
        protected int getHeight(ItemStack stack) {
            return this.getFont(stack).lineHeight + LINE_SPACING_NARROW + PROGRESSION_BAR_HEIGHT;
        }

        @Override
        protected void drawBackground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {

            y += this.getFont(stack).lineHeight + LINE_SPACING_NARROW;

            TierEnum tier = WandItem.getTier(stack);

            float progressFraction = 1;

            if (tier != TierEnum.MASTER) {
                progressFraction = (float) WandHelper.getProgression(stack) / TierEnum.values()[tier.level + 1].getProgression();
            }

            DrawingUtils.drawTexturedRect(guiGraphicsExtractor, texture, x, y, MAIN_GUI_WIDTH, ArcaneWorkbenchScreen.this.imageHeight + PROGRESSION_BAR_HEIGHT, PROGRESSION_BAR_WIDTH, PROGRESSION_BAR_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            int width = (int) (PROGRESSION_BAR_WIDTH * progressFraction);
            DrawingUtils.drawTexturedRect(guiGraphicsExtractor, texture, x, y, MAIN_GUI_WIDTH, ArcaneWorkbenchScreen.this.imageHeight, width, PROGRESSION_BAR_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        @Override
        protected void drawForeground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {

            TierEnum tier = WandItem.getTier(stack);

            guiGraphicsExtractor.text(this.getFont(stack), tier.getDisplayNameWithFormatting(), x, y, ARGB.color(255, 0, 0, 0), false);

            if (tier != TierEnum.MASTER) {
                TierEnum nextTier = TierEnum.values()[tier.level + 1];
                MutableComponent s = nextTier.getDisplayName().withStyle(ChatFormatting.DARK_GRAY);
                if (WandHelper.getProgression(stack) >= nextTier.getProgression()) {
                    s = nextTier.getDisplayNameWithFormatting();
                }
                guiGraphicsExtractor.text(this.getFont(stack), s, x + TOOLTIP_WIDTH - TOOLTIP_BORDER * 2 - this.getFont(stack).width(s), y, ARGB.color(255, 0, 0, 0));
            }
        }

    }

    private class TooltipElementSpellList extends TooltipElement {

        public TooltipElementSpellList(int spaceAfter) {
            super(spaceAfter, generateSpellEntries(8));
        }

        @Override
        protected boolean isVisible(@NonNull ItemStack stack) {
            return stack.getItem() instanceof ISpellCastingItem && ((ISpellCastingItem) stack.getItem()).showSpellsInWorkbench(Minecraft.getInstance().player, stack);
        }

        @Override
        protected int getHeight(ItemStack stack) {
            return 0;
        }

        @Override
        protected void drawBackground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            // Has no background of its own
        }

        @Override
        protected void drawForeground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            //Has no text of its own
        }
    }

    private class TooltipElementSpellEntry extends TooltipElementText {

        private final int index;

        public TooltipElementSpellEntry(int index) {
            super(null, Style.EMPTY.withColor(ChatFormatting.BLUE), LINE_SPACING_NARROW);
            this.index = index;
        }

        private AbstractSpell getSpell(ItemStack stack) {

            ItemStack spellBook = ArcaneWorkbenchScreen.this.menu.slots.get(index).getItem();

            if (!spellBook.isEmpty() && spellBook.getItem() instanceof SpellBookItem spellBookItem) {
                return spellBookItem.getCurrentSpell(stack);
            } else {
                return ((ISpellCastingItem) stack.getItem()).getSpells(stack)[index];
            }
        }

        private boolean shouldFlash(ItemStack stack) {
            ItemStack spellBook = ArcaneWorkbenchScreen.this.menu.getItemStack(index);
            return !spellBook.isEmpty() && spellBook.getItem() instanceof SpellBookItem spellBookItem && spellBookItem.getCurrentSpell(stack) != ((ISpellCastingItem) stack.getItem()).getSpells(stack)[index];
        }

        private float getAlpha(float partialTicks) {
            if (Minecraft.getInstance().player != null) {
                return (Mth.sin(0.2f * (Minecraft.getInstance().player.tickCount + partialTicks)) + 1) / 4 + 0.5f;
            }
            return 1.0f;
        }

        @Override
        protected boolean isVisible(@NonNull ItemStack stack) {
            if (stack.getItem() instanceof ISpellCastingItem iSpellCastingItem) {
                int len = iSpellCastingItem.getSpells(stack).length;
            }
            return stack.getItem() instanceof ISpellCastingItem iSpellCastingItem
                    && index < iSpellCastingItem.getSpells(stack).length;
        }

        @Override
        protected Font getFont(ItemStack stack) {
            return ClientHelper.shouldDisplayDiscovered(getSpell(stack), null) ? super.getFont(stack) : super.getFont(stack);
        }

        @Override
        protected Style getColour(ItemStack stack, float partialTicks) {
            return shouldFlash(stack) ? Style.EMPTY.withColor(DrawingUtils.makeTranslucent(0x000000, getAlpha(partialTicks))) : super.getColour(stack, partialTicks);
        }

        @Override
        protected MutableComponent getText(ItemStack stack) {

            AbstractSpell spell = this.getSpell(stack);

            if (ClientHelper.shouldDisplayDiscovered(spell, null)) {
                return spell.getDisplayNameWithFormatting();
            } else {
                return GlyphGenerator.getGlyphName(spell);
            }
        }

        @Override
        protected void drawBackground(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            AbstractSpell spell = this.getSpell(stack);
            Identifier texture = ClientHelper.shouldDisplayDiscovered(spell, stack) ? spell.getElement().getIcon() : ElementEnum.MAGIC.getIcon();
            guiGraphicsExtractor.blit(
                    RenderPipelines.GUI_TEXTURED, texture,
                    x, y,
                    0, 0,
                    8, 8,
                    8, 8,
                    ARGB.color((int) (this.shouldFlash(stack) ? this.getAlpha(partialTicks) * 255 : 255), 255, 255, 255)
            );
        }

        @Override
        protected void drawForeground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            super.drawForeground(guiGraphicsExtractor, x + 11, y, stack, partialTicks, mouseX, mouseY);
        }
    }

    private class TooltipElementUpgradeList extends TooltipElementText {

        public TooltipElementUpgradeList(int spaceAfter) {
            super(Component.translatable("container." + Wizardry.MODID + ":arcane_workbench.upgrades"),
                    Style.EMPTY.withColor(ChatFormatting.WHITE), spaceAfter, new TooltipElementUpgrades(0));
        }

        @Override
        protected int getHeight(ItemStack stack) {
            return super.getHeight(stack) + LINE_SPACING_NARROW; // Gap between heading and upgrade icons
        }

        @Override
        protected boolean isVisible(ItemStack stack) {
            return WandHelper.getTotalUpgrades(stack) > 0;
        }

    }

    private class TooltipElementUpgrades extends TooltipElement {

        private static final int ITEM_SIZE = 16;
        private static final int ITEM_SPACING = 2;

        public TooltipElementUpgrades(int spaceAfter) {
            super(spaceAfter);
        }

        @Override
        protected boolean isVisible(ItemStack stack) {
            return true;
        }

        @Override
        protected int getHeight(ItemStack stack) {
            int rows = 1 + (WandHelper.getTotalUpgrades(stack) * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING)
                    / (TOOLTIP_WIDTH - TOOLTIP_BORDER * 2);
            return rows * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
        }

        @Override
        protected void drawBackground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
            int x1 = 0;
            for (Item item : WandHelper.getSpecialUpgrades()) {

                int level = WandHelper.getUpgradeLevel(stack, item);

                if (level > 0) {

                    ItemStack upgrade = new ItemStack(item, level);

                    guiGraphicsExtractor.item(upgrade, x + x1, y);
                    guiGraphicsExtractor.itemDecorations(this.getFont(stack), upgrade, x + x1, y, null);

                    x1 += ITEM_SIZE + ITEM_SPACING;

                    if (x1 + ITEM_SIZE > TOOLTIP_WIDTH - TOOLTIP_BORDER * 2) {
                        x1 = 0;
                        y += ITEM_SIZE + ITEM_SPACING;
                    }
                }
            }
        }

        @Override
        protected void drawForeground(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {

            int x1 = 0;

            // Wand upgrade tooltips
            for (Item item : WandHelper.getSpecialUpgrades()) {

                int level = WandHelper.getUpgradeLevel(stack, item);

                if (level > 0) {
                    if (ArcaneWorkbenchScreen.this.isHovering(x + x1, y, ITEM_SIZE, ITEM_SIZE, mouseX, mouseY)) {
                        ItemStack upgrade = new ItemStack(item, level);
//                        renderToolTip(upgrade, mouseX - ArcaneWorkbenchScreen.this.leftPos, mouseY - ArcaneWorkbenchScreen.this.topPos);
                    }

                    x1 += ITEM_SIZE + ITEM_SPACING;

                    if (TOOLTIP_BORDER * 2 + x1 + ITEM_SIZE > TOOLTIP_WIDTH) {
                        x1 = 0;
                        y += ITEM_SIZE + ITEM_SPACING;
                    }

                }
            }

        }

    }
}
