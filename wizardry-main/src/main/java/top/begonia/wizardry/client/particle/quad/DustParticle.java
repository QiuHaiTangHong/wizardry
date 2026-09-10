package top.begonia.wizardry.client.particle.quad;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.impl.OneQuadParticle;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;

public class DustParticle extends OneQuadParticle {
    public DustParticle(ClientLevel level, @NonNull QuadParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
        this.setSize(0.01F, 0.01F);
        this.quadSize *= (this.random.nextFloat() + 0.2F) * 0.1F;
        this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
        this.currentColor(1.0f, 1.0f, 1.0f);
        this.startColor(1.0f, 1.0f, 1.0f);
        this.endColor(1.0f, 1.0f, 1.0f);
    }
}
