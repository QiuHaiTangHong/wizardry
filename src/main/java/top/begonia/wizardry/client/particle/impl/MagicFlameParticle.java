package top.begonia.wizardry.client.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.particle.AbstractParticle;
import top.begonia.wizardry.client.particle.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;

public class MagicFlameParticle extends AbstractParticle {
    public MagicFlameParticle(WizardryParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull MutableDoubleSpriteSet sprites) {
        super(options, level, x, y, z, xd, yd, zd, sprites);
        this.animationRowIndex = this.random.nextInt(4);
        this.setColor(1.0F, 1.0F, 1.0F);
        this.setFadeColor(1.0F, 1.0F, 1.0F);
        this.lifetime = 12 + this.random.nextInt(4);
        this.shaded = false;
        this.hasPhysics = true;
        this.setSpriteFromAge(sprites);
    }
}
