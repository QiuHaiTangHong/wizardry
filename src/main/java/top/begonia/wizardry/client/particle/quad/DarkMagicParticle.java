package top.begonia.wizardry.client.particle.quad;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.impl.OneQuadParticle;
import top.begonia.wizardry.api.particle.options.impl.QuadParticleOptions;

public class DarkMagicParticle extends OneQuadParticle {
    public DarkMagicParticle(ClientLevel level, @NonNull QuadParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
    }
}
