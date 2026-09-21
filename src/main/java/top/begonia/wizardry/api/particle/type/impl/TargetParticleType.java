package top.begonia.wizardry.api.particle.type.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.impl.TargetParticleOptions;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;

public class TargetParticleType extends ParticleTypeExtension<TargetParticleOptions> {
    private final MapCodec<TargetParticleOptions> codec = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("identifier").forGetter(TargetParticleOptions::identifier),
                    Codec.BOOL.fieldOf("overrideLimiter").forGetter(TargetParticleOptions::getOverrideLimiter)
            ).apply(instance, TargetParticleOptions::new));
    private final StreamCodec<RegistryFriendlyByteBuf, TargetParticleOptions> streamCodec = StreamCodec.composite(
            Identifier.STREAM_CODEC,
            TargetParticleOptions::identifier,
            ByteBufCodecs.BOOL,
            TargetParticleOptions::getOverrideLimiter,
            TargetParticleOptions::new
    );

    public TargetParticleType(Identifier identifier, boolean overrideLimiter) {
        super(identifier, overrideLimiter);
    }

    @Override
    public @NonNull MapCodec<TargetParticleOptions> codec() {
        return this.codec;
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, TargetParticleOptions> streamCodec() {
        return this.streamCodec;
    }
}
