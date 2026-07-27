package top.begonia.wizardry.client.data.definition.particle;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.data.IResultData;

import java.util.List;

public record ParticleDescriptionData(List<List<Identifier>> textures) implements IResultData {
    private static final Codec<List<List<Identifier>>> SMART_MATRIX_CODEC = Codec.either(
            Codec.list(Identifier.CODEC),
            Codec.list(Codec.list(Identifier.CODEC))
    ).xmap(
            either -> either.map(
                    List::of,
                    twoDim -> twoDim
            ),
            Either::right
    );
    public static final Codec<ParticleDescriptionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    SMART_MATRIX_CODEC.fieldOf("textures").forGetter(ParticleDescriptionData::textures)
            ).apply(instance, ParticleDescriptionData::new)
    );

    @Contract(pure = true)
    @Override
    public @NonNull Class<? extends IResultData> getDataClass() {
        return ParticleDescriptionData.class;
    }
}
