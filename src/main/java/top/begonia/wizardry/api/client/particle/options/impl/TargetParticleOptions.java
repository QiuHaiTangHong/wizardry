package top.begonia.wizardry.api.client.particle.options.impl;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.client.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.client.particle.type.impl.TargetParticleType;

public class TargetParticleOptions extends TargetParticleType implements IParticleOptionsExtension {

    public TargetParticleOptions(Identifier identifier, boolean overrideLimiter) {
        super(identifier, overrideLimiter);
    }

    @Override
    public double xa() {
        return 0;
    }

    @Override
    public double ya() {
        return 0;
    }

    @Override
    public double za() {
        return 0;
    }

    @Override
    public @NonNull ParticleType<TargetParticleOptions> getType() {
        return this;
    }
}
