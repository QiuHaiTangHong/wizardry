package top.begonia.wizardry.client.data.definition.handbook.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;

public record RecipeTagData(List<Identifier> locations) {
    public static final Codec<RecipeTagData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.listOf().fieldOf("locations").forGetter(RecipeTagData::locations)
            ).apply(instance, RecipeTagData::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeTagData> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC.apply(ByteBufCodecs.list()), RecipeTagData::locations,
            RecipeTagData::new
    );
}
