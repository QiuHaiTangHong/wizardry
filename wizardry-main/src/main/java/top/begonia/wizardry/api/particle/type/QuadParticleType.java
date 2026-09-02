package top.begonia.wizardry.api.particle.type;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;

public class QuadParticleType extends ParticleTypeExtension<QuadParticleOptions> {
    private final MapCodec<QuadParticleOptions> codec = MapCodec.unit(new QuadParticleOptions(this));
    private final StreamCodec<RegistryFriendlyByteBuf, QuadParticleOptions> streamCodec = StreamCodec.unit(new QuadParticleOptions(this));

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
