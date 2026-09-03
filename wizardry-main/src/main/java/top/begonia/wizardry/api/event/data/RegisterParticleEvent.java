package top.begonia.wizardry.api.event.data;

import net.neoforged.bus.api.Event;
import top.begonia.wizardry.api.particle.manager.SimpleParticleProvider;
import top.begonia.wizardry.api.particle.manager.WizardryParticleProvider;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;

import java.util.Map;

public class RegisterParticleEvent extends Event {
    private final Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> particleProviders;

    public RegisterParticleEvent(Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> particleProviders) {
        this.particleProviders = particleProviders;
    }

    public <T extends IParticleOptionsExtension> void registerProvider(
            ParticleTypeExtension<T> type,
            WizardryParticleProvider<T> provider
    ) {
        this.particleProviders.put(type, provider);
    }

    public <T extends IParticleOptionsExtension> void registerSimpleProvider(
            ParticleTypeExtension<T> type,
            SimpleParticleProvider<T> provider
    ) {
        this.registerProvider(type, (particleResourceAccessor, clientLevel, options, x, y, z) -> {
            if (options instanceof QuadParticleOptions quadParticleOptions) {
                quadParticleOptions.setSpriteSet(particleResourceAccessor.getSpriteSet(quadParticleOptions.getType()));
            }
            return provider.createParticle(clientLevel, options, x, y, z);
        });
    }
}
