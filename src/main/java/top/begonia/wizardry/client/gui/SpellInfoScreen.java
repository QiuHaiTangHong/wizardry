package top.begonia.wizardry.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.config.ClientConfig;
import top.begonia.wizardry.client.data.ClientGlyphData;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.common.data.spell.constants.TierEnum;
import top.begonia.wizardry.common.registry.WizardrySounds;
import top.begonia.wizardry.common.registry.WizardrySpells;
import top.begonia.wizardry.common.spell.AbstractSpell;

public abstract class SpellInfoScreen extends Screen {

    protected static final String TRANSLATION_KEY_PREFIX = "gui." + Wizardry.MODID + ".spell_book";

    protected final int xSize, ySize;
    protected int textureWidth = 512;
    protected int textureHeight = 256;

    public SpellInfoScreen(Component title, int xSize, int ySize) {
        super(title);
        this.xSize = xSize;
        this.ySize = ySize;
    }

    protected void setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public abstract AbstractSpell getSpell();

    public abstract Identifier getTexture();

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        int left = (this.width - xSize) / 2;
        int top = (this.height - ySize) / 2;
        this.extractBackgroundLayer(graphics, left, top, mouseX, mouseY);
        super.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        this.extractForegroundLayer(graphics, left, top, mouseX, mouseY);
    }

    protected void extractBackgroundLayer(GuiGraphicsExtractor graphics, int left, int top, int mouseX, int mouseY) {
        boolean discovered = ClientHelper.shouldDisplayDiscovered(getSpell(), null);
        Identifier iconTexture = discovered ? getSpell().getIcon() : WizardrySpells.NONE.get().getIcon();
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                iconTexture,
                left + 146, top + 20,
                0.0F, 0.0F,
                128, 128,
                128, 128
        );
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                getTexture(),
                left, top,
                0.0F, 0.0F,
                this.xSize, this.ySize,
                this.textureWidth, this.textureHeight
        );
    }

    protected void extractForegroundLayer(@NonNull GuiGraphicsExtractor graphics, int left, int top, int mouseX, int mouseY) {
        boolean discovered = ClientHelper.shouldDisplayDiscovered(getSpell(), null);
        AbstractSpell spell = getSpell();
        if (discovered) {
            graphics.text(this.font, spell.getDisplayName(), left + 17, top + 15, 0xFF000000, false);
            graphics.text(this.font, spell.getType().getDisplayName(), left + 17, top + 26, 0xFF777777, false);
        } else {
            Component glyphComponent = Component
                    .literal(ClientGlyphData.getInstance().getGlyphName(spell)).withStyle(style ->
                            style.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "alt")))
                    );
            graphics.text(this.font, glyphComponent, left + 17, top + 15, 0xFF000000, false);
            graphics.text(this.font, spell.getType().getDisplayName(), left + 17, top + 26, 0xFF777777, false);
        }

        MutableComponent tier = Component.translatable(
                TRANSLATION_KEY_PREFIX + ".tier",
                getSpell().getTier() == TierEnum.NOVICE
                        ? getSpell().getTier().getDisplayName().copy().withStyle(ChatFormatting.GRAY)
                        : getSpell().getTier().getDisplayNameWithFormatting()
        );
        graphics.text(this.font, tier, left + 17, top + 45, 0xFF000000, false);

        MutableComponent element = Component.translatable(
                TRANSLATION_KEY_PREFIX + ".element",
                getSpell().getElement().getDisplayNameWithFormatting()
        );
        if (!discovered) {
            element = Component.translatable(TRANSLATION_KEY_PREFIX + ".element_undiscovered");
        }
        graphics.text(this.font, element, left + 17, top + 57, 0xFF000000, false);

        MutableComponent manaCost = Component.translatable(TRANSLATION_KEY_PREFIX + ".mana_cost", getSpell().getCost());
        if (getSpell().isContinuous) {
            manaCost = Component.translatable(TRANSLATION_KEY_PREFIX + ".mana_cost_continuous", getSpell().getCost());
        }
        if (!discovered) {
            manaCost = Component.translatable(TRANSLATION_KEY_PREFIX + ".mana_cost_undiscovered");
        }
        graphics.text(this.font, manaCost, left + 17, top + 69, 0xFF000000, false);

        if (discovered) {
            graphics.textWithWordWrap(this.font, getSpell().getDescription(), left + 17, top + 83, 118, 0xFF000000, false);
        } else {
            Component glyphComponent = Component.literal(ClientGlyphData.getInstance().getGlyphDescription(getSpell())).withStyle(style ->
                    style.withFont(new FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "alt")))
            );
            graphics.textWithWordWrap(this.font, glyphComponent, left + 17, top + 83, 118, 0xFF000000, false);
        }
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(WizardrySounds.MISC_BOOK_OPEN.get(), 1.0F, 0.25F)
        );
    }

    @Override
    public void removed() {
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return ClientConfig.booksPauseGame;
    }

}
