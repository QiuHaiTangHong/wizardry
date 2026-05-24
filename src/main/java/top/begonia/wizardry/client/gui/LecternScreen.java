package top.begonia.wizardry.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.util.*;
import top.begonia.wizardry.client.gui.widget.InvisibleButton;
import top.begonia.wizardry.client.gui.widget.SpellSortButton;
import top.begonia.wizardry.client.gui.widget.TurnPageButton;
import top.begonia.wizardry.core.block.BookshelfBlock;
import top.begonia.wizardry.core.entity.block.BookshelfBlockEntity;
import top.begonia.wizardry.core.entity.block.LecternBlockEntity;
import top.begonia.wizardry.core.item.impl.SpellBookItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.TooltipBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class LecternScreen extends SpellInfoScreen implements ISpellSortable {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/gui/container/lectern.png");
    /**
     * The distance of the page buttons from the bottom outside corners of the GUI.
     */
    private static final int PAGE_BUTTON_INSET_X = 22, PAGE_BUTTON_INSET_Y = 13;
    /**
     * The distance between adjacent page turn buttons.
     */
    private static final int PAGE_BUTTON_SPACING = 20;
    /**
     * The distance of the sort buttons from the top left corner of the GUI.
     */
    private static final int SORT_BUTTON_INSET_X = 96, SORT_BUTTON_INSET_Y = 20;
    /**
     * The distance between adjacent sort buttons.
     */
    private static final int SORT_BUTTON_SPACING = 13;
    /**
     * The distance of the spell buttons from the top outside corners of the GUI.
     */
    private static final int SPELL_BUTTON_INSET_X = 23, SPELL_BUTTON_INSET_Y = 44;
    /**
     * The distance between adjacent spell buttons (in both x and y).
     */
    private static final int SPELL_BUTTON_SPACING = 38;

    private static final int SPELL_ROWS = 3, SPELL_COLUMNS = 3;
    public static final int SPELL_BUTTON_COUNT = SPELL_ROWS * SPELL_COLUMNS * 2;

    private static final int SEARCH_TOOLTIP_HOVER_TIME = 20;

    private static final Style TOOLTIP_SYNTAX = Style.EMPTY.withColor(ChatFormatting.YELLOW);
    private static final Style TOOLTIP_BODY = Style.EMPTY.withColor(ChatFormatting.WHITE);

    private final LecternBlockEntity lectern;

    private Button nextPageButton;
    private Button prevPageButton;
    private Button lastPageButton;
    private Button firstPageButton;
    private Button indexButton;
    private Button locateButton;

    private final Button[] sortButtons = new Button[3];
    private final SpellButton[] spellButtons = new SpellButton[SPELL_BUTTON_COUNT];

    /**
     * The spell currently being viewed, or null if the index is being viewed.
     */
    private AbstractSpell currentSpell;
    /**
     * The available spells in nearby bookshelves; should not contain duplicates.
     */
    private final List<AbstractSpell> availableSpells = new ArrayList<>();
    private List<AbstractSpell> matchingSpells = new ArrayList<>();

    private ISpellSortable.SortType sortType = ISpellSortable.SortType.TIER;
    private boolean sortDescending = false;

    private EditBox searchEditBox;
    private int searchBarHoverTime;

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

        // Page buttons
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
                        this.minecraft.setScreen(null);
                    }
                    if (this.minecraft.level == null || this.minecraft.player == null) {
                        return;
                    }
                    ClientLevel level = this.minecraft.level;
                    for (BookshelfBlockEntity bookshelfBlockEntity : BookshelfBlock.findNearbyBookshelves(level, lectern.getBlockPos())) {
                        for (int i = 0; i < bookshelfBlockEntity.getSlotCount(); i++) {
                            ItemStack stack = bookshelfBlockEntity.getStackInSlot(i);
                            if (stack.getItem() instanceof SpellBookItem spellBookItem) {
                                AbstractSpell spell = spellBookItem.getCurrentSpell(stack);
                                if (spell == this.currentSpell) {
                                    BlockPos pos = bookshelfBlockEntity.getBlockPos();
                                    for (Direction side : Direction.values()) {
                                        ParticleBuilder.create(WizardryParticles.BLOCK_HIGHLIGHT.get()).pos(
                                                        GeometryUtils.getFaceCentre(pos, side)
                                                                .add(new Vec3(side.getUnitVec3f())
                                                                        .scale(GeometryUtils.ANTI_Z_FIGHTING_OFFSET)))
                                                .face(side).clr(0.9f, 0.5f, 0.8f).fade(0.7f, 0, 1).spawn(level);
                                    }
                                    level.playLocalSound(
                                            pos,
                                            WizardrySounds.BLOCK_LECTERN_LOCATE_SPELL.get(),
                                            SoundSource.BLOCKS,
                                            1.0F,
                                            0.7F,
                                            false
                                    );
                                    break;
                                }
                            }
                        }
                    }
                    this.updateButtonVisibility();
                }
        ));

        // Sort buttons
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

        // Spell buttons
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
            if (searchEditBox.isMouseOver(mouseX, mouseY)) {
                if (searchBarHoverTime < SEARCH_TOOLTIP_HOVER_TIME) {
                    searchBarHoverTime++;
                } else if (searchBarHoverTime == SEARCH_TOOLTIP_HOVER_TIME) {
                    graphics.setTooltipForNextFrame(
                            this.font,
                            TooltipBuilder.splitComponentToList(Component.translatable(
                                    "container." + Wizardry.MODID + ".lectern.search_tooltip",
                                    Component.literal(ChatFormatting.YELLOW.toString()),
                                    Component.literal(ChatFormatting.WHITE.toString())
                            ), TOOLTIP_BODY),
                            Optional.empty(),
                            mouseX,
                            mouseY
                    );
                }
            } else {
                searchBarHoverTime = 0;
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

    @FunctionalInterface
    protected interface TriIterators {
        void accept(int buttonIndex, int relativeX, int relativeY);
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
            }); // Hide all buttons...
            indexButton.visible = true; // ... except the index button and locate button
            locateButton.visible = true;
        }
    }

    public void refreshAvailableSpells() {
        availableSpells.clear();
        if (lectern.getLevel() != null) {
            for (BookshelfBlockEntity bookshelf : BookshelfBlock.findNearbyBookshelves(lectern.getLevel(), lectern.getBlockPos())) {
                for (int i = 0; i < bookshelf.getSlotCount(); i++) {
                    ItemStack stack = bookshelf.getStackInSlot(i);
                    if (stack.getItem() instanceof SpellBookItem) {
                        AbstractSpell spell = null;
                        Holder<AbstractSpell> spellHolder = stack.get(WizardryComponents.SPELL.get());
                        if (spellHolder != null) {
                            spell = spellHolder.value();
                        }
                        if (spell != null && spell != WizardrySpells.NONE.get() && !availableSpells.contains(spell)) {
                            availableSpells.add(spell);
                        }
                    }
                }
            }
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

    private AbstractSpell getSpellForButton(SpellButton button) {
        return matchingSpells.get(currentPage * SPELL_BUTTON_COUNT + button.getIndex());
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
                        extractor.text(
                                getFont(),
                                spell.getDisplayName(),
                                mouseX, mouseY,
                                ARGB.color(255, 0, 0, 0),
                                false
                        );
                    } else {
                        extractor.text(
                                getFont(),
                                GlyphGenerator.getGlyphName(spell),
                                mouseX, mouseY,
                                ARGB.color(255, 0, 0, 0),
                                false
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
