package top.begonia.wizardry.client.data.definition.handbook.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record ImageData(
        Identifier location,
        String caption,
        int u, int v,
        int width, int height,
        Optional<Integer> textureWidth, Optional<Integer> textureHeight,
        Optional<Boolean> border
) {
    public static final Codec<ImageData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("location").forGetter(ImageData::location),
                    Codec.STRING.fieldOf("caption").forGetter(ImageData::caption),
                    Codec.INT.fieldOf("u").forGetter(ImageData::u),
                    Codec.INT.fieldOf("v").forGetter(ImageData::v),
                    Codec.INT.fieldOf("width").forGetter(ImageData::width),
                    Codec.INT.fieldOf("height").forGetter(ImageData::height),
                    Codec.INT.optionalFieldOf("texture_width").forGetter(ImageData::textureWidth),
                    Codec.INT.optionalFieldOf("texture_height").forGetter(ImageData::textureHeight),
                    Codec.BOOL.optionalFieldOf("border").forGetter(ImageData::border)
            ).apply(instance, ImageData::new)
    );
}
