package top.begonia.wizardry.client.data.definition.hub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import top.begonia.wizardry.core.api.data.IResultData;

public record SpellHubConfigData(
        String name,
        String description,
        int width,
        int height,
        Vec2b mirror,
        Vec2i spellIconInset,
        Vec2i textInset,
        Vec2i spellCascadeOffset,
        CooldownBar cooldownBar

) implements IResultData {
    public static final Codec<SpellHubConfigData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(SpellHubConfigData::name),
            Codec.STRING.fieldOf("description").forGetter(SpellHubConfigData::description),
            Codec.INT.fieldOf("width").forGetter(SpellHubConfigData::width),
            Codec.INT.fieldOf("height").forGetter(SpellHubConfigData::height),
            Vec2b.CODEC.fieldOf("mirror").forGetter(SpellHubConfigData::mirror),
            Vec2i.CODEC.fieldOf("spell_icon_inset").forGetter(SpellHubConfigData::spellIconInset),
            Vec2i.CODEC.fieldOf("text_inset").forGetter(SpellHubConfigData::textInset),
            Vec2i.CODEC.fieldOf("spell_cascade_offset").forGetter(SpellHubConfigData::spellCascadeOffset),
            CooldownBar.CODEC.fieldOf("cooldown_bar").forGetter(SpellHubConfigData::cooldownBar)
    ).apply(instance, SpellHubConfigData::new));

    @Override
    public Class<? extends IResultData> getDataClass() {
        return SpellHubConfigData.class;
    }

    public record Vec2i(
            int x,
            int y
    ) {
        public static final Codec<Vec2i> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.INT.fieldOf("x").forGetter(Vec2i::x),
                        Codec.INT.fieldOf("y").forGetter(Vec2i::y)
                ).apply(instance, Vec2i::new)
        );
    }

    public record Vec2b(boolean x, boolean y) {
        public static final Codec<Vec2b> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("x").forGetter(Vec2b::x),
                Codec.BOOL.fieldOf("y").forGetter(Vec2b::y)
        ).apply(instance, Vec2b::new));
    }

    public record CooldownBar(
            int x,
            int y,
            int length,
            int height,
            Vec2b mirror,
            boolean showWhenFull
    ) {
        public static final Codec<CooldownBar> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("x").forGetter(CooldownBar::x),
                Codec.INT.fieldOf("y").forGetter(CooldownBar::y),
                Codec.INT.fieldOf("length").forGetter(CooldownBar::length),
                Codec.INT.fieldOf("height").forGetter(CooldownBar::height),
                Vec2b.CODEC.fieldOf("mirror").forGetter(CooldownBar::mirror),
                Codec.BOOL.fieldOf("show_when_full").forGetter(CooldownBar::showWhenFull)
        ).apply(instance, CooldownBar::new));
    }
}
