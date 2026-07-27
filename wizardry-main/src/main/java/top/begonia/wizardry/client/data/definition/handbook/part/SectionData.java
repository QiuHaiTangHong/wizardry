package top.begonia.wizardry.client.data.definition.handbook.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.*;

public record SectionData(
        Optional<String> title,
        Optional<List<String>> text,
        Optional<List<Identifier>> triggers,
        Optional<String> includeInContents,
        Optional<Map<String, SectionData>> subSections,
        Optional<ContentsConfigData> contents,
        Optional<CentreConfigData> centre
) {
    public static final Codec<SectionData> CODEC = Codec.recursive("Section", self ->
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.STRING.optionalFieldOf("title").forGetter(SectionData::title),
                            Codec.STRING.listOf().optionalFieldOf("text").forGetter(SectionData::text),
                            Identifier.CODEC.listOf().optionalFieldOf("triggers").forGetter(SectionData::triggers),
                            Codec.STRING.optionalFieldOf("include_in_contents").forGetter(SectionData::includeInContents),
                            Codec.unboundedMap(Codec.STRING, self).optionalFieldOf("sections").forGetter(SectionData::subSections),
                            ContentsConfigData.CODEC.optionalFieldOf("contents").forGetter(SectionData::contents),
                            CentreConfigData.CODEC.optionalFieldOf("centre").forGetter(SectionData::centre)
                    ).apply(instance, SectionData::new)
            )
    );
}
