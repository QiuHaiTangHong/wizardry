package top.begonia.wizardry.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.impl.WizardryQuadParticle;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;

public class MagicBubbleParticle extends WizardryQuadParticle {
    public MagicBubbleParticle(ClientLevel level, @NonNull QuadParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
    }
}
