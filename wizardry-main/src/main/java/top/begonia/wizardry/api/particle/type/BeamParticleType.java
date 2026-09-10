package top.begonia.wizardry.api.particle.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.RayParticleOptions;

public class BeamParticleType extends ParticleTypeExtension<RayParticleOptions> {
    private final MapCodec<RayParticleOptions> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, RayParticleOptions> streamCodec;

    public BeamParticleType(Identifier identifier, boolean overrideLimiter) {
        super(identifier, overrideLimiter);
        this.codec = RayParticleOptions.codec(this);
        this.streamCodec = RayParticleOptions.streamCodec(this);
    }

    @Override
    public @NonNull MapCodec<RayParticleOptions> codec() {
        return this.codec;
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, RayParticleOptions> streamCodec() {
        return this.streamCodec;
    }
}
