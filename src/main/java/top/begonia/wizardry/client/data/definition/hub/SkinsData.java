package top.begonia.wizardry.client.data.definition.hub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import top.begonia.wizardry.api.data.IResultData;
import top.begonia.wizardry.client.util.DrawingUtils;

import java.util.Map;

public record SkinsData(
        Map<String, Skin> skins
) implements IResultData {
    public static final SkinsData DEFAULT = new SkinsData(Map.of());

    public record Coordinate(int x, int y) {
        public static final Codec<Coordinate> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("x").forGetter(Coordinate::x),
                        Codec.INT.fieldOf("y").forGetter(Coordinate::y)
                ).apply(instance, Coordinate::new)
        );
    }

    public record Mirror(boolean x, boolean y) {
        public static final Codec<Mirror> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("x").forGetter(Mirror::x),
                Codec.BOOL.fieldOf("y").forGetter(Mirror::y)
        ).apply(instance, Mirror::new));
    }

    public record CooldownBar(
            int x, int y,
            int length, int height,
            Mirror mirror,
            boolean showWhenFull
    ) {
        public static final Codec<CooldownBar> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("x").forGetter(CooldownBar::x),
                        Codec.INT.fieldOf("y").forGetter(CooldownBar::y),
                        Codec.INT.fieldOf("length").forGetter(CooldownBar::length),
                        Codec.INT.fieldOf("height").forGetter(CooldownBar::height),
                        Mirror.CODEC.fieldOf("mirror").forGetter(CooldownBar::mirror),
                        Codec.BOOL.fieldOf("show_when_full").forGetter(CooldownBar::showWhenFull)
                ).apply(instance, CooldownBar::new)
        );
    }

    public static final Codec<SkinsData> CODEC = Codec.unboundedMap(
            Codec.STRING,
            Skin.CODEC
    ).xmap(
            SkinsData::new,
            SkinsData::skins
    );

    public record Skin(
            Identifier texture,
            String name,
            String description,
            int width,
            int height,
            Mirror mirror,
            Coordinate spellIconInset,
            Coordinate textInset,
            Coordinate spellCascadeOffset,
            CooldownBar cooldownBar
    ) {
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
        public static final Codec<Skin> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(Skin::texture),
                        Codec.STRING.fieldOf("name").forGetter(Skin::name),
                        Codec.STRING.fieldOf("description").forGetter(Skin::description),
                        Codec.INT.fieldOf("width").forGetter(Skin::width),
                        Codec.INT.fieldOf("height").forGetter(Skin::height),
                        Mirror.CODEC.fieldOf("mirror").forGetter(Skin::mirror),
                        Coordinate.CODEC.fieldOf("spell_icon_inset").forGetter(Skin::spellIconInset),
                        Coordinate.CODEC.fieldOf("text_inset").forGetter(Skin::textInset),
                        Coordinate.CODEC.fieldOf("spell_cascade_offset").forGetter(Skin::spellCascadeOffset),
                        CooldownBar.CODEC.fieldOf("cooldown_bar").forGetter(Skin::cooldownBar)
                ).apply(instance, Skin::new)
        );

        public void drawBackground(
                GuiGraphicsExtractor guiGraphicsExtractor,
                int x, int y,
                boolean flipX, boolean flipY,
                Identifier icon,
                float cooldownBarProgress,
                boolean creativeMode,
                boolean jammed
        ) {
            if (flipX && !this.mirror.x) {
                x -= width;
            }
            if (flipY && !this.mirror.y) {
                y += this.height;
            }

            int x1 = flipX && this.mirror.x ? x - this.spellIconInset.x - SPELL_ICON_SIZE : x + this.spellIconInset.x;
            int y1 = flipY && this.mirror.y ? y + this.spellIconInset.y : y - this.spellIconInset.y - SPELL_ICON_SIZE;

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

            x1 = flipX && this.mirror.x ? x - width : x;
            y1 = flipY && this.mirror.y ? y : y - height;
            if (jammed) {
                DrawingUtils.drawGlitchRect(
                        guiGraphicsExtractor, texture,
                        random,
                        x1, y1,
                        creativeMode ? 128 : 0, 0,
                        width, height,
                        256, 256,
                        flipX && this.mirror.x, flipY && this.mirror.y
                );
            } else {
                DrawingUtils.drawTexturedFlippedRect(
                        guiGraphicsExtractor, texture,
                        x1, y1,
                        creativeMode ? 128 : 0, 0,
                        width, height,
                        256, 256,
                        flipX && this.mirror.x, flipY && this.mirror.y
                );
            }

            if (!creativeMode && cooldownBarProgress > 0 && (this.cooldownBar.showWhenFull || cooldownBarProgress < 1)) {

                int l = (int) (cooldownBarProgress * this.cooldownBar.length);

                x1 = flipX && this.mirror.x ? x - this.cooldownBar.x - (this.cooldownBar.mirror.x ? l : this.cooldownBar.length) : x + this.cooldownBar.x;
                y1 = flipY && this.mirror.y ? y + this.cooldownBar.y : y - this.cooldownBar.y - this.cooldownBar.height;

                int u = this.cooldownBar.x;
                int v = height;

                if (jammed) {
                    DrawingUtils.drawGlitchRect(
                            guiGraphicsExtractor, texture,
                            random,
                            x1, y1,
                            u, v,
                            l, this.cooldownBar.height,
                            256, 256,
                            flipX && this.cooldownBar.mirror.x, flipY && this.cooldownBar.mirror.y
                    );
                } else {
                    DrawingUtils.drawTexturedFlippedRect(
                            guiGraphicsExtractor, texture,
                            x1, y1,
                            u, v,
                            l, this.cooldownBar.height,
                            256, 256,
                            flipX && this.cooldownBar.mirror.x, flipY && this.cooldownBar.mirror.y
                    );
                }
            }
        }

        public void drawText(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, boolean flipX, boolean flipY, Component prevSpellName, Component spellName, Component nextSpellName, float animationProgress) {
            if (flipX && !this.mirror.x) x -= width;
            if (flipY && !this.mirror.y) y += height;
            Font font = Minecraft.getInstance().font;
            int x1 = flipX && this.mirror.x ? x - width : x + this.textInset.x;
            int y1 = flipY && this.mirror.y ? y + this.textInset.y - font.lineHeight / 2 + 2 : y - this.textInset.y - font.lineHeight / 2 - 1;
            int maxWidth = width - this.textInset.x;
            if (animationProgress == 0) {
                float xPrev = flipX && this.mirror.x ? x - width : x + this.textInset.x - (flipY ? -1 : 1) * this.spellCascadeOffset.x;
                float xNext = flipX && this.mirror.x ? x - width : x + this.textInset.x + (flipY ? -1 : 1) * this.spellCascadeOffset.x;
                float yPrev = y1 - (this.spellCascadeOffset.y + 1);
                float yNext = y1 + this.spellCascadeOffset.y;
                float maxWidthPrev = maxWidth + (flipY ? -1 : 1) * this.spellCascadeOffset.x;
                float maxWidthNext = maxWidth - (flipY ? -1 : 1) * this.spellCascadeOffset.x;
                int nextPrevClr = DrawingUtils.makeTranslucent(0xffffff, SPELL_NAME_OPACITY);
                DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, prevSpellName, xPrev, yPrev, SPELL_NAME_SCALE, nextPrevClr, maxWidthPrev, true, flipX && this.mirror.x);
                DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, spellName, x1, y1, 1, 0xffffffff, maxWidth, true, flipX && this.mirror.x);
                DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, nextSpellName, xNext, yNext, SPELL_NAME_SCALE, nextPrevClr, maxWidthNext, true, flipX && this.mirror.x);

            } else {

                boolean reverse = animationProgress < 0;
                if (reverse)
                    animationProgress = 1 - Math.abs(animationProgress);

                float xPrev = flipX && this.mirror.x ? x - width : x + this.textInset.x - (flipY ? -1 : 1) * this.spellCascadeOffset.x * animationProgress;
                float xNext = flipX && this.mirror.x ? x - width : x + this.textInset.x + (flipY ? -1 : 1) * this.spellCascadeOffset.x * (1 - animationProgress);
                float yPrev = y1 - (this.spellCascadeOffset.y + 1) * animationProgress;
                float yNext = y1 + this.spellCascadeOffset.y * (1 - animationProgress);
                float maxWidthPrev = maxWidth + (flipY ? -1 : 1) * this.spellCascadeOffset.x * animationProgress;
                float maxWidthNext = maxWidth - (flipY ? -1 : 1) * this.spellCascadeOffset.x * (1 - animationProgress);
                float scalePrev = SPELL_NAME_SCALE + (1 - SPELL_NAME_SCALE) * (1 - animationProgress);
                float scaleNext = SPELL_NAME_SCALE + (1 - SPELL_NAME_SCALE) * (animationProgress);
                int clrPrev = DrawingUtils.makeTranslucent(0xffffff, SPELL_NAME_OPACITY + (1 - SPELL_NAME_OPACITY) * (1 - animationProgress));
                int clrNext = DrawingUtils.makeTranslucent(0xffffff, SPELL_NAME_OPACITY + (1 - SPELL_NAME_OPACITY) * animationProgress);
                if (reverse) {
                    DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, spellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, true, flipX && this.mirror.x);
                    DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, nextSpellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, true, flipX && this.mirror.x);

                } else {
                    DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, prevSpellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, true, flipX && this.mirror.x);
                    DrawingUtils.drawScaledStringToWidth(guiGraphicsExtractor, font, spellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, true, flipX && this.mirror.x);
                }
            }
        }
    }

    @Override
    public Class<? extends IResultData> getDataClass() {
        return SkinsData.class;
    }
}
