package top.begonia.wizardry.common.data.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import top.begonia.wizardry.common.data.spell.constants.ElementEnum;
import top.begonia.wizardry.common.data.spell.constants.EnabledEnum;
import top.begonia.wizardry.common.data.spell.constants.SpellTypeEnum;
import top.begonia.wizardry.common.data.spell.constants.TierEnum;
import top.begonia.wizardry.common.data.spell.part.BasePropertiesData;
import top.begonia.wizardry.common.data.spell.part.EnablePropertiesData;

import java.util.Map;

public record SpellPropertiesData(
        TierEnum tier,
        ElementEnum element,
        SpellTypeEnum type,
        int cost,
        int chargeup,
        int cooldown,
        BasePropertiesData baseAttributes,
        EnablePropertiesData enabled
) implements IWizardryProperty<SpellPropertiesData> {
    public static final Codec<SpellPropertiesData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentable.fromEnum(TierEnum::values).fieldOf("tier").orElse(TierEnum.getDefault()).forGetter(SpellPropertiesData::tier),
            StringRepresentable.fromEnum(ElementEnum::values).fieldOf("element").orElse(ElementEnum.getDefault()).forGetter(SpellPropertiesData::element),
            StringRepresentable.fromEnum(SpellTypeEnum::values).fieldOf("type").orElse(SpellTypeEnum.getDefault()).forGetter(SpellPropertiesData::type),
            Codec.INT.fieldOf("cost").orElse(0).forGetter(SpellPropertiesData::cost),
            Codec.INT.fieldOf("chargeup").orElse(0).forGetter(SpellPropertiesData::chargeup),
            Codec.INT.fieldOf("cooldown").orElse(0).forGetter(SpellPropertiesData::cooldown),
            BasePropertiesData.CODEC.optionalFieldOf("base_properties", BasePropertiesData.getDefault()).orElse(new BasePropertiesData(Map.of())).forGetter(SpellPropertiesData::baseAttributes),
            EnablePropertiesData.CODEC.optionalFieldOf("enabled", EnablePropertiesData.getDefault()).forGetter(SpellPropertiesData::enabled)
    ).apply(instance, SpellPropertiesData::new));

    public static SpellPropertiesData getDefault() {
        return new SpellPropertiesData(
                TierEnum.getDefault(),
                ElementEnum.getDefault(),
                SpellTypeEnum.getDefault(),
                0, 0, 0,
                BasePropertiesData.getDefault(),
                EnablePropertiesData.getDefault()
        );
    }

    public boolean isEnabled(EnabledEnum... contexts) {
        for (EnabledEnum context : contexts) {
            if (enabled.isEnabled(context)) {
                return true;
            }
        }
        return false;
    }

    public static final StreamCodec<FriendlyByteBuf, SpellPropertiesData> STREAM_CODEC =
            ByteBufCodecs.fromCodec(CODEC).cast();

    @Override
    public Codec<SpellPropertiesData> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super FriendlyByteBuf, SpellPropertiesData> getStreamCodec() {
        return STREAM_CODEC;
    }
}
