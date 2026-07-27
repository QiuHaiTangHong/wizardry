package top.begonia.wizardry.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.jspecify.annotations.NonNull;

public class WizardryParticleOptions implements ParticleOptions {
    private final ParticleType<?> type;

    public WizardryParticleOptions(ParticleType<?> type) {
        this.type = type;
    }

    @Override
    public @NonNull ParticleType<?> getType() {
        return this.type;
    }
}
