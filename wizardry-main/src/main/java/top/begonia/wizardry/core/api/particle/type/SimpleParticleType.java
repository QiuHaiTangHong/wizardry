package top.begonia.wizardry.core.api.particle.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.particle.options.SimpleParticleOptions;

public class SimpleParticleType extends ParticleType<SimpleParticleOptions> {
    private final MapCodec<SimpleParticleOptions> codec = MapCodec.unit(new SimpleParticleOptions(this));
    private final StreamCodec<RegistryFriendlyByteBuf, SimpleParticleOptions> streamCodec = StreamCodec.unit(new SimpleParticleOptions(this));

    protected SimpleParticleType(boolean overrideLimiter) {
        super(overrideLimiter);
    }

    @Override
    public @NonNull MapCodec<SimpleParticleOptions> codec() {
        return this.codec;
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, SimpleParticleOptions> streamCodec() {
        return this.streamCodec;
    }
}
