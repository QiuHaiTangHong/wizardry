package top.begonia.wizardry.client.data.definition.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.core.api.event.data.RegisterParticleEvent;
import top.begonia.wizardry.client.particle.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;

public class ParticleCombinedHolder {
    private final MutableDoubleSpriteSet spriteSet = new MutableDoubleSpriteSet();
    private RegisterParticleEvent.ParticleConstructor constructor;

    public MutableDoubleSpriteSet getSpriteSet() {
        return this.spriteSet;
    }

    public void setConstructor(RegisterParticleEvent.ParticleConstructor constructor) {
        this.constructor = constructor;
    }

    public @Nullable <T extends WizardryParticleOptions> Particle create(
            T options,
            ClientLevel level,
            double x, double y, double z,
            double xSpeed, double ySpeed, double zSpeed
    ) {
        if (this.constructor == null || this.spriteSet.isEmpty()) {
            return null;
        }
        return this.constructor.create(options, level, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
    }
}
