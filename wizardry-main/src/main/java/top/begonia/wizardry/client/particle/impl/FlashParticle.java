package top.begonia.wizardry.client.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.particle.impl.WizardryQuadParticle;
import top.begonia.wizardry.core.api.particle.options.QuadParticleOptions;

public class FlashParticle extends WizardryQuadParticle {
    public FlashParticle(ClientLevel level, @NonNull QuadParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
    }
}
