package top.begonia.wizardry.client.data.definition.handbook.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ContentsConfigData(
        String id,
        boolean hyperlinks,
        boolean pageNumbers,
        String separator
) {
    public static final Codec<ContentsConfigData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("id").forGetter(ContentsConfigData::id),
                    Codec.BOOL.fieldOf("hyperlinks").forGetter(ContentsConfigData::hyperlinks),
                    Codec.BOOL.fieldOf("page_numbers").forGetter(ContentsConfigData::pageNumbers),
                    Codec.STRING.fieldOf("separator").forGetter(ContentsConfigData::separator)
            ).apply(instance, ContentsConfigData::new)
    );
}
