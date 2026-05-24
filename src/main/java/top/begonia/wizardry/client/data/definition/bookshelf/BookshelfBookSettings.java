package top.begonia.wizardry.client.data.definition.bookshelf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.core.api.data.IResultData;

import java.util.Map;

public record BookshelfBookSettings(
        Map<Identifier, Identifier> textureMapping
) implements IResultData {
    public static final Codec<BookshelfBookSettings> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).fieldOf("texture_mapping").forGetter(BookshelfBookSettings::textureMapping)
            ).apply(instance, BookshelfBookSettings::new)
    );

    @Override
    public Class<? extends IResultData> getDataClass() {
        return BookshelfBookSettings.class;
    }
}
