package top.begonia.wizardry.core.api.event.data;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import top.begonia.wizardry.core.api.particle.manager.WizardryParticleProvider;
import top.begonia.wizardry.core.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.core.api.particle.type.ParticleTypeExtension;

import java.util.Map;

public class RegisterParticleEvent extends Event implements IModBusEvent {
    private final Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> particleProviders;

    public RegisterParticleEvent(Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> particleProviders) {
        this.particleProviders = particleProviders;
    }

    public <T extends IParticleOptionsExtension> void register(
            ParticleTypeExtension<T> type,
            WizardryParticleProvider<T> provider
    ) {
        this.particleProviders.put(type, provider);
    }
}
