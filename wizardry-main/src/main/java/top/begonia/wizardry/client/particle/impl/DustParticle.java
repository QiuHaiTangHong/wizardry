package top.begonia.wizardry.client.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import top.begonia.wizardry.client.particle.AbstractParticle;
import top.begonia.wizardry.client.particle.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;

public class DustParticle extends AbstractParticle {
    public DustParticle(WizardryParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, MutableDoubleSpriteSet sprites) {
        super(options, level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        this.setSize(0.01F, 0.01F);
        this.quadSize *= (float) ((this.random.nextFloat() + 0.2F) * 0.1);
        this.lifetime = (int) (16.0D / (Math.random() * 0.8D + 0.2D));
        this.setColor(1, 1, 1);
        this.setInitialColor(1, 1, 1);
        this.setFadeColor(1, 1, 1);
    }
}
