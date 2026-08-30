package top.begonia.wizardry.client.particle.impl;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.particle.AbstractParticle;
import top.begonia.wizardry.client.particle.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.particle.WizardryParticleOptions;

public class BeamParticle extends AbstractParticle {
    private static final float THICKNESS = 0.1F;
    private double targetX, targetY, targetZ;
    private double length;
    public BeamParticle(WizardryParticleOptions options, ClientLevel level, double x, double y, double z, double xd, double yd, double zd, @NonNull MutableDoubleSpriteSet sprites) {
        super(options, level, x, y, z, xd, yd, zd, sprites);
        this.setInitialColor(1.0f, 1.0f, 1.0f);
        this.setLifetime(0);
        this.setShaded(false);
    }

    @Override
    public void setLength(double length) {
        this.length = length;
    }

    @Override
    public void setTargetPosition(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        double dx = x - this.x;
        double dy = y - this.y;
        double dz = z - this.z;
        this.length = Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
