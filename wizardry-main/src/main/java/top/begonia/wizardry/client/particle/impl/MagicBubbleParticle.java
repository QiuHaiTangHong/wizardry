package top.begonia.wizardry.client.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.particle.AbstractParticle;
import top.begonia.wizardry.client.particle.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;

public class MagicBubbleParticle extends AbstractParticle {
    public MagicBubbleParticle(WizardryParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull MutableDoubleSpriteSet sprites) {
        super(options, level, x, y, z, xd, yd, zd, sprites);
        this.setInitialColor(1, 1, 1);
        this.setFadeColor(1, 1, 1);
        this.setSize(0.02F, 0.02F);
        this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
        this.lifetime = (int) (8.0D / this.random.nextDouble() * 0.8D + 0.2D);
    }

    @Override
    public void tick() {
        this.yd += 0.002D;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.8500000238418579D;
        this.yd *= 0.8500000238418579D;
        this.zd *= 0.8500000238418579D;

        if (this.lifetime-- <= 0) {
            this.remove();
        }
    }
}
