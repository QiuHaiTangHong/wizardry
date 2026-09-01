package top.begonia.wizardry.core.api.particle.type;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.core.api.particle.options.IParticleOptionsExtension;

public abstract class ParticleTypeExtension<T extends IParticleOptionsExtension> extends ParticleType<T> {
    private final Identifier identifier;
    protected ParticleTypeExtension(Identifier identifier, boolean overrideLimiter) {
        super(overrideLimiter);
        this.identifier = identifier;
    }

    public Identifier identifier(){
        return this.identifier;
    }
}
