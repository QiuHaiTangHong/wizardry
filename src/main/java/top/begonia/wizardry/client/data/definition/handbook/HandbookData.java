package top.begonia.wizardry.client.data.definition.handbook;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import top.begonia.wizardry.client.data.definition.IData;
import top.begonia.wizardry.client.data.definition.handbook.part.ImageData;
import top.begonia.wizardry.client.data.definition.handbook.part.RecipeTagData;
import top.begonia.wizardry.client.data.definition.handbook.part.SectionData;

import java.util.Map;

public record HandbookData(
        String bookmarkStartSection,
        Map<String, Integer> colours,
        Map<String, ImageData> images,
        Map<String, RecipeTagData> recipes,
        Map<String, SectionData> sections
) implements IData {
    public static final Codec<HandbookData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("bookmark_start_section").forGetter(HandbookData::bookmarkStartSection),
                    Codec.unboundedMap(Codec.STRING, ExtraCodecs.STRING_RGB_COLOR).fieldOf("colours").forGetter(HandbookData::colours),
                    Codec.unboundedMap(Codec.STRING, ImageData.CODEC).fieldOf("images").forGetter(HandbookData::images),
                    Codec.unboundedMap(Codec.STRING, RecipeTagData.CODEC).fieldOf("recipes").forGetter(HandbookData::recipes),
                    Codec.unboundedMap(Codec.STRING, SectionData.CODEC).fieldOf("sections").forGetter(HandbookData::sections)
            ).apply(instance, HandbookData::new)
    );

    @Override
    public Class<? extends IData> getDataClass() {
        return HandbookData.class;
    }
}