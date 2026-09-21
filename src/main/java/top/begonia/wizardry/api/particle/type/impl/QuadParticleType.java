package top.begonia.wizardry.api.particle.type.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.impl.QuadParticleOptions;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;

public class QuadParticleType extends ParticleTypeExtension<QuadParticleOptions> {
    private final MapCodec<QuadParticleOptions> codec = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("identifier").forGetter(QuadParticleOptions::identifier),
                    Codec.BOOL.fieldOf("overrideLimiter").forGetter(QuadParticleOptions::getOverrideLimiter)
            ).apply(instance, QuadParticleOptions::new));
    private final StreamCodec<RegistryFriendlyByteBuf, QuadParticleOptions> streamCodec = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            QuadParticleOptions::identifier,
            ByteBufCodecs.BOOL,
            QuadParticleOptions::getOverrideLimiter,
            QuadParticleOptions::new
    );

    public QuadParticleType(Identifier identifier, boolean overrideLimiter) {
        super(identifier, overrideLimiter);
    }

    @Override
    public @NonNull MapCodec<QuadParticleOptions> codec() {
        return this.codec;
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, QuadParticleOptions> streamCodec() {
        return this.streamCodec;
    }
}
