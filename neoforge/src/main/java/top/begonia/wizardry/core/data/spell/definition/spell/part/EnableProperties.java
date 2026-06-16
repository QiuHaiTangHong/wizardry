package top.begonia.wizardry.core.data.spell.definition.spell.part;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import top.begonia.wizardry.core.constants.EnabledEnum;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public record EnableProperties(
        Map<EnabledEnum, Boolean> enableProperties) {
    public static final Codec<EnableProperties> CODEC = Codec.unboundedMap(EnabledEnum.CODEC, Codec.BOOL)
            .xmap(EnableProperties::new, EnableProperties::enableProperties);
    public static final StreamCodec<FriendlyByteBuf, EnableProperties> STREAM_CODEC =
            ByteBufCodecs.fromCodec(CODEC).cast();
    public static final EnableProperties DEFAULT = new EnableProperties(Collections.emptyMap());

    public boolean isEnabled(EnabledEnum context) {
        return enableProperties.getOrDefault(context, true);
    }
}
