package top.begonia.wizardry.client.data.definition.handbook.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CentreConfigData(
        boolean x, boolean y
) {
    public static final Codec<CentreConfigData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("x").forGetter(CentreConfigData::x),
                    Codec.BOOL.fieldOf("y").forGetter(CentreConfigData::y)
            ).apply(instance, CentreConfigData::new)
    );
}
