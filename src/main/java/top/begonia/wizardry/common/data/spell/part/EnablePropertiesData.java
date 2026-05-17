package top.begonia.wizardry.common.data.spell.part;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import top.begonia.wizardry.common.data.spell.IWizardryProperty;
import top.begonia.wizardry.common.data.spell.constants.EnabledEnum;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public record EnablePropertiesData(
        Map<EnabledEnum, Boolean> enableProperties) implements IWizardryProperty<EnablePropertiesData> {
    public static final Codec<EnablePropertiesData> CODEC = Codec.unboundedMap(EnabledEnum.CODEC, Codec.BOOL)
            .xmap(EnablePropertiesData::new, EnablePropertiesData::enableProperties);
    public static final StreamCodec<FriendlyByteBuf, EnablePropertiesData> STREAM_CODEC =
            ByteBufCodecs.fromCodec(CODEC).cast();

    public static EnablePropertiesData getDefault() {
        Map<EnabledEnum, Boolean> map = new EnumMap<>(EnabledEnum.class);
        for (EnabledEnum value : EnabledEnum.values()) {
            map.put(value, true);
        }
        return new EnablePropertiesData(Collections.unmodifiableMap(map));
    }

    public boolean isEnabled(EnabledEnum context) {
        return enableProperties.getOrDefault(context, true);
    }

    @Override
    public Codec<EnablePropertiesData> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super FriendlyByteBuf, EnablePropertiesData> getStreamCodec() {
        return STREAM_CODEC;
    }
}
