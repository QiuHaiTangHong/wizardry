package top.begonia.wizardry.api.particle.manager;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;

@FunctionalInterface
public interface WizardryParticleProvider<T extends IParticleOptionsExtension> {
    @Nullable
    CompositeQuadParticle<T> createParticle(
            ParticleResourceAccessor accessor,
            ClientLevel clientLevel,
            T options,
            double x, double y, double z
    );
}
