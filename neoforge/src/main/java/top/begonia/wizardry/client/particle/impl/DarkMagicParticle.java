package top.begonia.wizardry.client.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.particle.AbstractParticle;
import top.begonia.wizardry.client.particle.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;

public class DarkMagicParticle extends AbstractParticle {
    public DarkMagicParticle(WizardryParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull MutableDoubleSpriteSet sprites) {
        super(options, level, x, y, z, xd, yd, zd, sprites);
        this.yd *= 0.20000000298023224D;
        this.setInitialColor(1, 1, 1);
        this.setFadeColor(1, 1, 1);
        this.quadSize *= 0.75F;
        this.lifetime = (int) (8.0D / (this.random.nextDouble() * 0.8D + 0.2D));
        this.hasPhysics = true;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.setSpriteFromAge(this.sprites);
        this.yd += 0.004D;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.9599999785423279D;
        this.yd *= 0.9599999785423279D;
        this.zd *= 0.9599999785423279D;

        if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
        }
    }
}
