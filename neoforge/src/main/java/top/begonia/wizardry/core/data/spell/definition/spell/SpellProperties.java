package top.begonia.wizardry.core.data.spell.definition.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import top.begonia.wizardry.core.api.data.IResultData;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.EnabledEnum;
import top.begonia.wizardry.core.constants.SpellTypeEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.spell.definition.spell.part.BaseProperties;
import top.begonia.wizardry.core.data.spell.definition.spell.part.EnableProperties;

import java.util.Map;

public record SpellProperties(
        TierEnum tier,
        ElementEnum element,
        SpellTypeEnum type,
        int cost,
        int chargeUp,
        int cooldown,
        BaseProperties baseAttributes,
        EnableProperties enabled
) implements IResultData {
    public static final Codec<SpellProperties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringRepresentable.fromEnum(TierEnum::values).fieldOf("tier").orElse(TierEnum.DEFAULT).forGetter(SpellProperties::tier),
            StringRepresentable.fromEnum(ElementEnum::values).fieldOf("element").orElse(ElementEnum.DEFAULT).forGetter(SpellProperties::element),
            StringRepresentable.fromEnum(SpellTypeEnum::values).fieldOf("type").orElse(SpellTypeEnum.DEFAULT).forGetter(SpellProperties::type),
            Codec.INT.fieldOf("cost").orElse(0).forGetter(SpellProperties::cost),
            Codec.INT.fieldOf("charge_up").orElse(0).forGetter(SpellProperties::chargeUp),
            Codec.INT.fieldOf("cooldown").orElse(0).forGetter(SpellProperties::cooldown),
            BaseProperties.CODEC.optionalFieldOf("base_properties", BaseProperties.getDefault()).orElse(new BaseProperties(Map.of())).forGetter(SpellProperties::baseAttributes),
            EnableProperties.CODEC.optionalFieldOf("enabled", EnableProperties.DEFAULT).forGetter(SpellProperties::enabled)
    ).apply(instance, SpellProperties::new));
    public static final StreamCodec<FriendlyByteBuf, SpellProperties> STREAM_CODEC =
            ByteBufCodecs.fromCodec(CODEC).cast();

    public static SpellProperties DEFAULT = new SpellProperties(
            TierEnum.DEFAULT,
            ElementEnum.DEFAULT,
            SpellTypeEnum.DEFAULT,
            0, 0, 0,
            BaseProperties.DEFAULT,
            EnableProperties.DEFAULT
    );

    public boolean isEnabled(EnabledEnum... contexts) {
        for (EnabledEnum context : contexts) {
            if (enabled.isEnabled(context)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Class<? extends IResultData> getDataClass() {
        return SpellProperties.class;
    }
}
