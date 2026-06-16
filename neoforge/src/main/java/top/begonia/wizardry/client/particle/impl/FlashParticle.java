package top.begonia.wizardry.client.particle.impl;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.particle.AbstractParticle;
import top.begonia.wizardry.client.particle.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;

public class FlashParticle extends AbstractParticle {
    public FlashParticle(WizardryParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, MutableDoubleSpriteSet sprites) {
        super(options, level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        this.setInitialColor(1, 1, 1);
        this.setFadeColor(1, 1, 1);
        this.quadSize = 0.6f;
        this.lifetime = 6;
    }

    @Override
    public void extract(@NonNull QuadParticleRenderState particleTypeRenderState, @NonNull Camera camera, float partialTickTime) {
        this.setAlpha(0.6F - ((float) this.age + partialTickTime - 1.0F) / this.lifetime * 0.5F);
        super.extract(particleTypeRenderState, camera, partialTickTime);
    }
}
