package top.begonia.wizardry.core.api.particle.manager;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import top.begonia.wizardry.core.api.particle.WizardryParticle;
import top.begonia.wizardry.core.api.particle.options.IParticleOptionsExtension;

@FunctionalInterface
public interface WizardryParticleProvider<T extends IParticleOptionsExtension> {
    @Nullable
    WizardryParticle<T> createParticle(
            ParticleResourceAccessor accessor,
            ClientLevel clientLevel,
            T options,
            double x, double y, double z
    );
}
