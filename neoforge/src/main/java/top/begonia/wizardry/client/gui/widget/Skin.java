package top.begonia.wizardry.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.hub.SpellHubConfigData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.client.util.DrawingUtils;

import java.util.Optional;

public class Skin {
    /**
     * Scale of the next/previous spell names.
     */
    private static final float SPELL_NAME_SCALE = 0.5f;
    /**
     * Opacity of the next/previous spell names, as a fraction.
     */
    private static final float SPELL_NAME_OPACITY = 0.3f;
    /**
     * Width and height of the spell icon (very unlikely to change!)
     */
    private static final int SPELL_ICON_SIZE = 32;
    private static final RandomSource random = RandomSource.create();
    private final Identifier texture;
    private String name;
    private String description;
    private int width;
    private int height;
    private boolean mirrorX;
    private boolean mirrorY;
    private int spellIconInsetX;
    private int spellIconInsetY;
    private int textInsetX;
    private int textInsetY;
    private int cascadeOffsetX;
    private int cascadeOffsetY;
    private int cooldownBarX;
    private int cooldownBarY;
    private int cooldownBarLength;
    private int cooldownBarHeight;
    private boolean cooldownBarMirrorX;
    private boolean cooldownBarMirrorY;
    private boolean showCooldownWhenFull;

    public Skin(Identifier texture, Identifier metadata) {
        this.texture = texture;
        Optional<SpellHubConfigData> spellHubConfigData = WizardryClientDataManager.getInstance().getData(metadata, SpellHubConfigData.class);
        spellHubConfigData.ifPresent(this::parseData);
    }

    private void parseData(SpellHubConfigData spellHubConfigData) {
        name = spellHubConfigData.name();
        description = spellHubConfigData.description();
        width = spellHubConfigData.width();
        if (width > 128) {
            Wizardry.LOGGER.warn("The width of the spell HUD skin {} exceeds 128, this may cause it to render strangely.", name);
        }
        height = spellHubConfigData.height();
        mirrorX = spellHubConfigData.mirror().x();
        mirrorY = spellHubConfigData.mirror().y();
        spellIconInsetX = spellHubConfigData.spellIconInset().x();
        spellIconInsetY = spellHubConfigData.spellIconInset().y();
        textInsetX = spellHubConfigData.textInset().x();
        textInsetY = spellHubConfigData.textInset().y();
        cascadeOffsetX = spellHubConfigData.spellCascadeOffset().x();
        cascadeOffsetY = spellHubConfigData.spellCascadeOffset().y();
        cooldownBarX = spellHubConfigData.cooldownBar().x();
        cooldownBarY = spellHubConfigData.cooldownBar().y();
        cooldownBarLength = spellHubConfigData.cooldownBar().length();
        cooldownBarHeight = spellHubConfigData.cooldownBar().height();
        cooldownBarMirrorX = spellHubConfigData.cooldownBar().mirror().x();
        cooldownBarMirrorY = spellHubConfigData.cooldownBar().mirror().y();
        showCooldownWhenFull = spellHubConfigData.cooldownBar().showWhenFull();

    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void drawBackground(
            GuiGraphicsExtractor guiGraphicsExtractor,
            int x, int y,
            boolean flipX, boolean flipY,
            Identifier icon,
            float cooldownBarProgress,
            boolean creativeMode,
            boolean jammed
    ) {
        if (flipX && !mirrorX) {
            x -= width;
        }
        if (flipY && !mirrorY) {
            y += height;
        }

        int x1 = flipX && mirrorX ? x - spellIconInsetX - SPELL_ICON_SIZE : x + spellIconInsetX;
        int y1 = flipY && mirrorY ? y + spellIconInsetY : y - spellIconInsetY - SPELL_ICON_SIZE;

        if (jammed) {
            if (Minecraft.getInstance().level != null) {
                random.setSeed(Minecraft.getInstance().level.getGameTime() / 2);
            }
            DrawingUtils.drawGlitchRect(
                    guiGraphicsExtractor, icon,
                    random,
                    x1, y1,
                    0, 0,
                    SPELL_ICON_SIZE, SPELL_ICON_SIZE,
                    SPELL_ICON_SIZE, SPELL_ICON_SIZE,
                    false, false
            );
        } else {
            DrawingUtils.drawTexturedRect(
                    guiGraphicsExtractor, icon,
                    x1, y1,
                    0, 0,
                    SPELL_ICON_SIZE, SPELL_ICON_SIZE,
                    SPELL_ICON_SIZE, SPELL_ICON_SIZE
            );
        }

        x1 = flipX && mirrorX ? x - width : x;
        y1 = flipY && mirrorY ? y : y - height;
        if (jammed) {
            DrawingUtils.drawGlitchRect(
                    guiGraphicsExtractor, texture,
                    random,
                    x1, y1,
                    creativeMode ? 128 : 0, 0,
                    width, height,
                    256, 256,
                    flipX && mirrorX, flipY && mirrorY
            );
        } else {
            DrawingUtils.drawTexturedFlippedRect(
                    guiGraphicsExtractor, texture,
                    x1, y1,
                    creativeMode ? 128 : 0, 0,
                    width, height,
                    256, 256,
                    flipX && mirrorX, flipY && mirrorY
            );
        }

        if (!creativeMode && cooldownBarProgress > 0 && (showCooldownWhenFull || cooldownBarProgress < 1)) {

            int l = (int) (cooldownBarProgress * cooldownBarLength);

            x1 = flipX && mirrorX ? x - cooldownBarX - (cooldownBarMirrorX ? l : cooldownBarLength) : x + cooldownBarX;
            y1 = flipY && mirrorY ? y + cooldownBarY : y - cooldownBarY - cooldownBarHeight;

            int u = cooldownBarX;
            int v = height;

            if (jammed) {
                DrawingUtils.drawGlitchRect(
                        guiGraphicsExtractor, texture,
                        random,
                        x1, y1,
                        u, v,
                        l, cooldownBarHeight,
                        256, 256,
                        flipX && cooldownBarMirrorX, flipY && cooldownBarMirrorY
                );
            } else {
                DrawingUtils.drawTexturedFlippedRect(
                        guiGraphicsExtractor, texture,
                        x1, y1,
                        u, v,
                        l, cooldownBarHeight,
                        256, 256,
                        flipX && cooldownBarMirrorX, flipY && cooldownBarMirrorY
                );
            }
        }
    }

    public void drawText(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, boolean flipX, boolean flipY, Component prevSpellName, Component spellName, Component nextSpellName, float animationProgress) {
        if (flipX && !mirrorX) x -= width;
        if (flipY && !mirrorY) y += height;
        Font font = Minecraft.getInstance().font;
        int x1 = flipX && mirrorX ? x - width : x + textInsetX;
        int y1 = flipY && mirrorY ? y + textInsetY - font.lineHeight / 2 + 2 : y - textInsetY - font.lineHeight / 2 - 1;
        int maxWidth = width - textInsetX;
        if (animationProgress == 0) {
            float xPrev = flipX && mirrorX ? x - width : x + textInsetX - (flipY ? -1 : 1) * cascadeOffsetX;
            float xNext = flipX && mirrorX ? x - width : x + textInsetX + (flipY ? -1 : 1) * cascadeOffsetX;
            float yPrev = y1 - (cascadeOffsetY + 1);
            float yNext = y1 + cascadeOffsetY;
            float maxWidthPrev = maxWidth + (flipY ? -1 : 1) * cascadeOffsetX;
            float maxWidthNext = maxWidth - (flipY ? -1 : 1) * cascadeOffsetX;
            int nextPrevClr = DrawingUtils.makeTranslucent(0xffffff, SPELL_NAME_OPACITY);
            DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, prevSpellName, xPrev, yPrev, SPELL_NAME_SCALE, nextPrevClr, maxWidthPrev, true, flipX && mirrorX);
            DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, spellName, x1, y1, 1, 0xffffffff, maxWidth, true, flipX && mirrorX);
            DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, nextSpellName, xNext, yNext, SPELL_NAME_SCALE, nextPrevClr, maxWidthNext, true, flipX && mirrorX);

        } else {

            boolean reverse = animationProgress < 0;
            if (reverse)
                animationProgress = 1 - Math.abs(animationProgress);

            float xPrev = flipX && mirrorX ? x - width : x + textInsetX - (flipY ? -1 : 1) * cascadeOffsetX * animationProgress;
            float xNext = flipX && mirrorX ? x - width : x + textInsetX + (flipY ? -1 : 1) * cascadeOffsetX * (1 - animationProgress);
            float yPrev = y1 - (cascadeOffsetY + 1) * animationProgress;
            float yNext = y1 + cascadeOffsetY * (1 - animationProgress);
            float maxWidthPrev = maxWidth + (flipY ? -1 : 1) * cascadeOffsetX * animationProgress;
            float maxWidthNext = maxWidth - (flipY ? -1 : 1) * cascadeOffsetX * (1 - animationProgress);
            float scalePrev = SPELL_NAME_SCALE + (1 - SPELL_NAME_SCALE) * (1 - animationProgress);
            float scaleNext = SPELL_NAME_SCALE + (1 - SPELL_NAME_SCALE) * (animationProgress);
            int clrPrev = DrawingUtils.makeTranslucent(0xffffff, SPELL_NAME_OPACITY + (1 - SPELL_NAME_OPACITY) * (1 - animationProgress));
            int clrNext = DrawingUtils.makeTranslucent(0xffffff, SPELL_NAME_OPACITY + (1 - SPELL_NAME_OPACITY) * animationProgress);
            if (reverse) {
                DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, spellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, true, flipX && mirrorX);
                DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, nextSpellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, true, flipX && mirrorX);

            } else {
                DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, prevSpellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, true, flipX && mirrorX);
                DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, spellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, true, flipX && mirrorX);
            }
        }
    }

}
