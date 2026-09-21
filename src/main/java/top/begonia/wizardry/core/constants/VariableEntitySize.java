package top.begonia.wizardry.core.constants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record VariableEntitySize(
        float width,
        float height,
        float eyeHeight,
        float sizeMultiplier
) {
    public static final Codec<VariableEntitySize> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("width").forGetter(VariableEntitySize::width),
                    Codec.FLOAT.fieldOf("height").forGetter(VariableEntitySize::height),
                    Codec.FLOAT.fieldOf("eye_height").forGetter(VariableEntitySize::eyeHeight),
                    Codec.FLOAT.fieldOf("size_multiplier").forGetter(VariableEntitySize::sizeMultiplier)
            ).apply(instance, VariableEntitySize::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, VariableEntitySize> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, VariableEntitySize::width,
                    ByteBufCodecs.FLOAT, VariableEntitySize::height,
                    ByteBufCodecs.FLOAT, VariableEntitySize::eyeHeight,
                    ByteBufCodecs.FLOAT, VariableEntitySize::sizeMultiplier,
                    VariableEntitySize::new
            );
}
