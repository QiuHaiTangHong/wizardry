package top.begonia.wizardry.api.particle.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.BeamParticleOptions;

public class BeamParticleType extends ParticleTypeExtension<BeamParticleOptions> {
    private final MapCodec<BeamParticleOptions> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, BeamParticleOptions> streamCodec;

    public BeamParticleType(Identifier identifier, boolean overrideLimiter) {
        super(identifier, overrideLimiter);
        this.codec = BeamParticleOptions.codec(this);
        this.streamCodec = BeamParticleOptions.streamCodec(this);
    }

    @Override
    public @NonNull MapCodec<BeamParticleOptions> codec() {
        return this.codec;
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, BeamParticleOptions> streamCodec() {
        return this.streamCodec;
    }
}
