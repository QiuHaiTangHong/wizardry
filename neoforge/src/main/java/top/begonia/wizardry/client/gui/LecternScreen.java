package top.begonia.wizardry.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.util.*;
import top.begonia.wizardry.client.gui.widget.InvisibleButton;
import top.begonia.wizardry.client.gui.widget.SpellSortButton;
import top.begonia.wizardry.client.gui.widget.TurnPageButton;
import top.begonia.wizardry.core.block.BookshelfBlock;
import top.begonia.wizardry.core.entity.block.LecternBlockEntity;
import top.begonia.wizardry.core.item.impl.SpellBookItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.TextHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 讲台界面类, 用于展示和管理魔法书中的法术信息
 * <p> 该界面提供了法术浏览, 搜索, 排序和定位等交互功能, 包含翻页按钮, 排序按钮和法术选择按钮.
 * 界面以索引页和详情页两种模式显示: 索引页显示法术列表, 详情页显示当前选中法术的详细信息.
 * <p> 实现了 {@link ISpellSortable} 接口, 支持按法术层级和名称进行排序.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @email 3040469570@qq.com
 * @date 2026.06.15
 * @since 1.0.0
 */
public class LecternScreen extends SpellInfoScreen implements ISpellSortable {
    /**
     * 讲台 GUI 容器背景纹理的静态标识符
     * <p> 指向默认的讲台纹理贴图资源路径
     */
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/container/lectern.png");
    /**
     * 页面按钮的垂直内边距, 单位为像素
     */
    private static final int PAGE_BUTTON_INSET_X = 22, PAGE_BUTTON_INSET_Y = 13;
    /**
     * 页面按钮间距
     */
    private static final int PAGE_BUTTON_SPACING = 20;
    /**
     * 排序按钮的垂直内边距, 单位是像素
     */
    private static final int SORT_BUTTON_INSET_X = 96, SORT_BUTTON_INSET_Y = 20;
    /**
     * 排序按钮之间的间距
     */
    private static final int SORT_BUTTON_SPACING = 13;
    /**
     * 拼写按钮 Y 轴内边距
     */
    private static final int SPELL_BUTTON_INSET_X = 23, SPELL_BUTTON_INSET_Y = 44;
    /**
     * 咒语按钮之间的间距像素值
     */
    private static final int SPELL_BUTTON_SPACING = 38;
    /**
     * 法术列表的默认列数
     * <p> 用于控制界面中展示法术列表时的列布局数量, 默认为 3 列
     */
    private static final int SPELL_ROWS = 3, SPELL_COLUMNS = 3;
    /**
     * 法术按钮总数, 等于法术行数, 列数与 2 的乘积
     */
    public static final int SPELL_BUTTON_COUNT = SPELL_ROWS * SPELL_COLUMNS * 2;
    /**
     * 搜索提示框悬停时间阈值
     */
    private static final int SEARCH_TOOLTIP_HOVER_TIME = 20;
    /**
     * 工具提示文本的显示样式, 使用黄色高亮显示语法信息
     */
    private static final Style TOOLTIP_SYNTAX = Style.EMPTY.withColor(ChatFormatting.YELLOW);
    /**
     * 工具提示正文的样式, 颜色设置为白色
     */
    private static final Style TOOLTIP_BODY = Style.EMPTY.withColor(ChatFormatting.WHITE);

    private final LecternBlockEntity lectern;
    private final Button[] sortButtons = new Button[3];
    private final SpellButton[] spellButtons = new SpellButton[SPELL_BUTTON_COUNT];
    private final List<AbstractSpell> availableSpells = new ArrayList<>();
    private Button nextPageButton;
    private Button prevPageButton;
    private Button lastPageButton;
    private Button firstPageButton;
    private Button indexButton;
    private Button locateButton;
    private AbstractSpell currentSpell;
    private List<AbstractSpell> matchingSpells = new ArrayList<>();
    private ISpellSortable.SortType sortType = ISpellSortable.SortType.TIER;
    private boolean sortDescending = false;
    private EditBox searchEditBox;
    private int searchBarHoverTime;
    private boolean isSearchBarHover = false;
    private int currentPage = 0;

    public LecternScreen(@NonNull LecternBlockEntity lectern) {
        super(Component.empty(), 288, 180);
        this.lectern = lectern;
        this.currentSpell = lectern.currentSpell;
        this.setTextureSize(512, 512);
    }

    @Override
    public AbstractSpell getSpell() {
        return this.currentSpell;
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }

    @Override
    public SortType getSortType() {
        return sortType;
    }

    @Override
    public boolean isSortDescending() {
        return this.sortDescending;
    }

    @Override
    public void init() {
        super.init();
        final int left = this.width / 2 - this.xSize / 2;
        final int top = this.height / 2 - this.ySize / 2;
        // 添加跟翻页有关的按钮
        this.addRenderableWidget(nextPageButton = new TurnPageButton(
                left + xSize - PAGE_BUTTON_INSET_X - TurnPageButton.WIDTH,
                top + ySize - PAGE_BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.NEXT_PAGE,
                TEXTURE,
                textureWidth, textureHeight,
                _ -> {
                    this.currentPage = this.currentPage < this.getPageCount() - 1 ? this.currentPage + 1 : this.currentPage;
                    this.updateButtonVisibility();
                }
        ));

        this.addRenderableWidget(prevPageButton = new TurnPageButton(
                left + PAGE_BUTTON_INSET_X,
                top + ySize - PAGE_BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.PREVIOUS_PAGE,
                TEXTURE,
                textureWidth, textureHeight,
                _ -> {
                    this.currentPage = this.currentPage > 0 ? this.currentPage - 1 : this.currentPage;
                    this.updateButtonVisibility();
                }
        ));

        this.addRenderableWidget(lastPageButton = new TurnPageButton(
                left + xSize - PAGE_BUTTON_INSET_X - TurnPageButton.WIDTH - PAGE_BUTTON_SPACING,
                top + ySize - PAGE_BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.NEXT_SECTION,
                TEXTURE,
                textureWidth, textureHeight,
                _ -> {
                    this.currentPage = this.getPageCount() - 1;
                    this.updateButtonVisibility();
                }
        ));

        this.addRenderableWidget(firstPageButton = new TurnPageButton(
                left + PAGE_BUTTON_INSET_X + PAGE_BUTTON_SPACING,
                top + ySize - PAGE_BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.PREVIOUS_SECTION,
                TEXTURE,
                textureWidth, textureHeight,
                _ -> {
                    this.currentPage = 0;
                    this.updateButtonVisibility();
                }
        ));

        this.addRenderableWidget(indexButton = new TurnPageButton(
                left + xSize / 2 - 23,
                top + ySize - PAGE_BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                TurnPageButton.Type.CONTENTS,
                TEXTURE,
                textureWidth, textureHeight,
                _ -> {
                    this.currentSpell = WizardrySpells.NONE.get();
                    this.updateButtonVisibility();
                }
        ));

        this.addRenderableWidget(locateButton = new LocateBookButton(
                left + xSize / 2 - 34,
                top + ySize - PAGE_BUTTON_INSET_Y - TurnPageButton.HEIGHT,
                _ -> {
                    if (this.minecraft.player != null) {
                        this.minecraft.player.connection.send(new ServerboundContainerClosePacket(this.minecraft.player.containerMenu.containerId));
                        this.minecraft.gui.setScreen(null);
                    }
                    if (this.minecraft.level == null || this.minecraft.player == null) {
                        return;
                    }
                    ClientLevel level = this.minecraft.level;
                    BookshelfBlock.findNearbyBookshelves(level, lectern.getBlockPos(), (itemResourceResourceHandler, blockEntity) -> {
                        for (int i = 0; i < itemResourceResourceHandler.size(); i++) {
                            ItemResource resource = itemResourceResourceHandler.getResource(i);
                            if (resource.getItem() instanceof SpellBookItem spellBookItem) {
                                AbstractSpell spell = spellBookItem.getCurrentSpell(resource);
                                if (spell == this.currentSpell) {
                                    for (Direction side : Direction.values()) {
                                        ParticleBuilder.create(WizardryParticles.BLOCK_HIGHLIGHT.get()).pos(
                                                        GeometryUtils.getFaceCentre(blockEntity.getBlockPos(), side)
                                                                .add(new Vec3(side.getUnitVec3f())
                                                                        .scale(GeometryUtils.ANTI_Z_FIGHTING_OFFSET)))
                                                .face(side).clr(0.9f, 0.5f, 0.8f).fade(0.7f, 0, 1).spawn(level);
                                    }
                                    level.playLocalSound(
                                            blockEntity.getBlockPos(),
                                            WizardrySounds.BLOCK_LECTERN_LOCATE_SPELL.get(),
                                            SoundSource.BLOCKS,
                                            1.0F,
                                            0.7F,
                                            false
                                    );
                                    return true;
                                }
                            }
                        }
                        return false;
                    });
                    this.updateButtonVisibility();
                }
        ));

        // 排序按钮
        for (SortType sortType : SortType.values()) {
            this.addRenderableWidget(sortButtons[sortType.ordinal()] = new SpellSortButton(
                    left + SORT_BUTTON_INSET_X + SORT_BUTTON_SPACING * sortType.ordinal(),
                    top + SORT_BUTTON_INSET_Y,
                    sortType,
                    this,
                    this,
                    button -> {
                        SortType currentSortType = ((SpellSortButton) button).sortType;
                        if (this.sortType == currentSortType) {
                            this.sortDescending = !this.sortDescending;
                        } else {
                            this.sortType = currentSortType;
                            this.sortDescending = false;
                        }
                        updateMatchingSpells();
                    }
            ));
        }

        // 法术显示按钮
        this.rowColumnIterators((buttonIndex, relativeX, relativeY) ->
                this.addRenderableWidget(spellButtons[buttonIndex] =
                        new SpellButton(
                                left + relativeX, top + relativeY,
                                buttonIndex,
                                button -> {
                                    this.currentSpell = this.getSpellForButton((SpellButton) button);
                                    this.updateButtonVisibility();
                                }
                        )
                )
        );

        this.searchEditBox = new EditBox(this.font, left + 157, top + 21, 106, this.font.lineHeight, Component.empty());
        this.searchEditBox.setMaxLength(50);
        this.searchEditBox.setBordered(false);
        this.searchEditBox.setVisible(true);
        this.searchEditBox.setTextShadow(false);
        this.searchEditBox.setTextColor(ARGB.color(255, 255, 255, 255));
        this.addRenderableWidget(this.searchEditBox);
        refreshAvailableSpells();
    }

    @Override
    protected void extractBackgroundLayer(@NonNull GuiGraphicsExtractor graphics, int left, int top, int mouseX, int mouseY) {
        if (this.currentSpell == WizardrySpells.NONE.get()) {
            extractIndexPage(graphics, left, top);
        } else {
            super.extractBackgroundLayer(graphics, left, top, mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        if (this.isSearchBarHover) {
            if (searchBarHoverTime < SEARCH_TOOLTIP_HOVER_TIME) {
                searchBarHoverTime++;
            }
        } else {
            searchBarHoverTime = 0;
        }
    }

    @Override
    protected void extractForegroundLayer(@NonNull GuiGraphicsExtractor graphics, int left, int top, int mouseX, int mouseY) {
        if (currentSpell == WizardrySpells.NONE.get()) {
            graphics.text(
                    this.font,
                    Component.translatable("gui." + Wizardry.MODID + ".lectern.title"),
                    left + 20,
                    top + SORT_BUTTON_INSET_Y,
                    ARGB.color(255, 0, 0, 0),
                    false
            );
            this.isSearchBarHover = searchEditBox.isMouseOver(mouseX, mouseY);
            if (this.isSearchBarHover) {
                if (searchBarHoverTime == SEARCH_TOOLTIP_HOVER_TIME) {
                    graphics.setTooltipForNextFrame(
                            this.font,
                            TextHelper.componentWithStyles(
                                    "container." + Wizardry.MODID + ".lectern.search_tooltip",
                                    TOOLTIP_SYNTAX,
                                    TOOLTIP_BODY
                            ),
                            Optional.empty(),
                            mouseX,
                            mouseY
                    );
                }
            }
        } else {
            super.extractForegroundLayer(graphics, left, top, mouseX, mouseY);
        }
    }

    private void extractIndexPage(@NonNull GuiGraphicsExtractor guiGraphicsExtractor, int left, int top) {
        this.rowColumnIterators((buttonIndex, relativeX, relativeY) -> {
            int index = currentPage * SPELL_BUTTON_COUNT + buttonIndex;
            AbstractSpell spell = index < matchingSpells.size() ? matchingSpells.get(index) : WizardrySpells.NONE.get();
            boolean discovered = ClientHelper.shouldDisplayDiscovered(spell, null);
            Identifier iconIdentifier = discovered ? spell.getIcon() : WizardrySpells.NONE.get().getIcon();
            guiGraphicsExtractor.blit(
                    RenderPipelines.GUI_TEXTURED,
                    iconIdentifier,
                    left + relativeX + 1,
                    top + relativeY + 1,
                    0, 0,
                    32, 32,
                    32, 32
            );
        });
        guiGraphicsExtractor.blit(
                RenderPipelines.GUI_TEXTURED,
                this.getTexture(),
                left, top,
                0, 256,
                xSize, ySize,
                textureWidth, textureHeight
        );

    }

    protected void rowColumnIterators(TriIterators consumer) {
        for (int i = 0; i < SPELL_BUTTON_COUNT; i++) {
            int row = i % SPELL_COLUMNS;
            int column = (i / SPELL_COLUMNS) % SPELL_ROWS;
            int x = i < SPELL_BUTTON_COUNT / 2
                    ? SPELL_BUTTON_INSET_X + row * SPELL_BUTTON_SPACING
                    : xSize - SPELL_BUTTON_INSET_X - SpellButton.WIDTH - (2 - row) * SPELL_BUTTON_SPACING;
            int y = SPELL_BUTTON_INSET_Y + column * SPELL_BUTTON_SPACING;
            consumer.accept(i, x, y);
        }
    }

    private void updateMatchingSpells() {
        matchingSpells = availableSpells
                .stream()
                .filter(abstractSpell -> abstractSpell.matches(searchEditBox.getValue().toLowerCase(Locale.ROOT)))
                .sorted(sortDescending ? sortType.comparator.reversed() : sortType.comparator)
                .collect(Collectors.toList());
    }

    private void updateButtonVisibility() {
        if (currentSpell == WizardrySpells.NONE.get()) {
            this.searchEditBox.setVisible(true);
            int lastPage = getPageCount() - 1;
            prevPageButton.visible = currentPage > 0;
            firstPageButton.visible = currentPage > 0;
            nextPageButton.visible = currentPage < lastPage;
            lastPageButton.visible = currentPage < lastPage;

            indexButton.visible = false;
            locateButton.visible = false;

            for (Button button : sortButtons) {
                button.visible = true;
            }

            for (SpellButton button : spellButtons) {
                button.visible = currentPage * SPELL_BUTTON_COUNT + button.index < matchingSpells.size();
            }

        } else {
            this.searchEditBox.setVisible(false);
            this.renderables.forEach(renderable -> {
                if (renderable instanceof Button button) {
                    button.visible = false;
                }
            });
            indexButton.visible = true;
            locateButton.visible = true;
        }
    }

    public void refreshAvailableSpells() {
        availableSpells.clear();
        if (lectern.getLevel() != null) {
            BookshelfBlock.findNearbyBookshelves(lectern.getLevel(), lectern.getBlockPos(), (itemResourceResourceHandler, _) -> {
                for (int i = 0; i < itemResourceResourceHandler.size(); i++) {
                    ItemResource resource = itemResourceResourceHandler.getResource(i);
                    if (resource.getItem() instanceof SpellBookItem) {
                        AbstractSpell spell = null;
                        Holder<AbstractSpell> spellHolder = resource.get(WizardryComponents.SPELL.get());
                        if (spellHolder != null) {
                            spell = spellHolder.value();
                        }
                        if (spell != null && spell != WizardrySpells.NONE.get() && !availableSpells.contains(spell)) {
                            availableSpells.add(spell);
                        }
                    }
                }
                return false;
            });
        }

        if (!availableSpells.contains(currentSpell)) {
            currentSpell = WizardrySpells.NONE.get();
        }

        updateMatchingSpells();
        updateButtonVisibility();

    }

    private int getPageCount() {
        return Mth.ceil((float) matchingSpells.size() / SPELL_BUTTON_COUNT);
    }

    private AbstractSpell getSpellForButton(@NonNull SpellButton button) {
        return matchingSpells.get(currentPage * SPELL_BUTTON_COUNT + button.getIndex());
    }

    @FunctionalInterface
    protected interface TriIterators {
        void accept(int buttonIndex, int relativeX, int relativeY);
    }

    private class SpellButton extends InvisibleButton {
        private static final int WIDTH = 34, HEIGHT = 34;
        private final int index;

        public SpellButton(int x, int y, int index, OnPress onPress) {
            super(x, y, WIDTH, HEIGHT, onPress);
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public void playDownSound(@NonNull SoundManager soundHandler) {
            soundHandler.play(SimpleSoundInstance.forUI(WizardrySounds.MISC_PAGE_TURN.get(), 1.0F));
        }

        @Override
        protected void extractContents(@NonNull GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
            if (this.visible) {
                super.extractContents(extractor, mouseX, mouseY, partialTick);
                if (this.isHovered) {
                    extractor.blit(
                            RenderPipelines.GUI_TEXTURED,
                            TEXTURE,
                            this.getX(),
                            this.getY(),
                            40, 180,
                            this.width, this.height,
                            textureWidth, textureHeight
                    );
                    AbstractSpell spell = getSpellForButton(this);
                    if (ClientHelper.shouldDisplayDiscovered(spell, null)) {
                        extractor.setTooltipForNextFrame(
                                getFont(),
                                spell.getDisplayName().withStyle(ChatFormatting.WHITE),
                                mouseX, mouseY
                        );
                    } else {
                        extractor.setTooltipForNextFrame(
                                getFont(),
                                GlyphGenerator.getGlyphName(spell),
                                mouseX, mouseY
                        );
                    }
                }
            }
        }
    }

    private class LocateBookButton extends Button {

        public LocateBookButton(int x, int y, OnPress onPress) {
            super(x, y, 12, 12, Component.empty(), onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(@NonNull GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
            if (this.visible) {
                extractor.blit(
                        RenderPipelines.GUI_TEXTURED,
                        TEXTURE,
                        this.getX(),
                        this.getY(),
                        this.isHovered ? this.width : 0, 184,
                        this.width, this.height,
                        textureWidth, textureHeight
                );
            }
        }
    }
}
