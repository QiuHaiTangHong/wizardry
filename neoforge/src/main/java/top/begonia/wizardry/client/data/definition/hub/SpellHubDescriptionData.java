package top.begonia.wizardry.client.data.definition.hub;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.core.api.data.IResultData;

import java.util.Map;

public record SpellHubDescriptionData(
        Map<String, Description> item
) implements IResultData {
    @Override
    public Class<? extends IResultData> getDataClass() {
        return SpellHubDescriptionData.class;
    }

    public static final Codec<SpellHubDescriptionData> CODEC = Codec.unboundedMap(Codec.STRING, Description.CODEC).xmap(
            SpellHubDescriptionData::new,
            SpellHubDescriptionData::item
    );

    public record Description(
            Identifier texture,
            Identifier metadata
    ) {
        public static final Codec<Description> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(Description::texture),
                        Identifier.CODEC.fieldOf("metadata").forGetter(Description::metadata)
                ).apply(instance, Description::new)
        );
    }
}
