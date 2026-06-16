package top.begonia.wizardry.client.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

public final class WizardryParticleType extends ParticleType<WizardryParticleOptions> {
    private final MapCodec<WizardryParticleOptions> codec = MapCodec.unit(new WizardryParticleOptions(this));
    private final StreamCodec<RegistryFriendlyByteBuf, WizardryParticleOptions> streamCodec = StreamCodec.unit(new WizardryParticleOptions(this));

    public WizardryParticleType(boolean overrideLimiter) {
        super(overrideLimiter);
    }

    @Override
    public @NonNull MapCodec<WizardryParticleOptions> codec() {
        return this.codec;
    }

    @Override
    public @NonNull StreamCodec<? super RegistryFriendlyByteBuf, WizardryParticleOptions> streamCodec() {
        return this.streamCodec;
    }
}
