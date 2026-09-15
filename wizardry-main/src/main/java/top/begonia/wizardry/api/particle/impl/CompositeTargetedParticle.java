package top.begonia.wizardry.api.particle.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.extension.builder.ParticleBuilder;
import top.begonia.wizardry.api.particle.extension.extract.ExtractFlow;
import top.begonia.wizardry.api.particle.options.RayParticleOptions;

import javax.annotation.Nullable;

public class CompositeTargetedParticle extends CompositeQuadParticle<RayParticleOptions> {
    private static final double THIRD_PERSON_AXIAL_OFFSET = 1.2;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected double targetVelX;
    protected double targetVelY;
    protected double targetVelZ;
    protected double length;

    @Nullable
    protected EntityReference<Entity> target = null;

    public CompositeTargetedParticle(ClientLevel level, @NonNull RayParticleOptions options, double x, double y, double z) {
        super(level, options, x, y, z);
    }

    @Override
    public ParticleBuilder targetPosition(double x, double y, double z){
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        return this;
    }

    @Override
    public ParticleBuilder targetVelocity(double vx, double vy, double vz){
        this.targetVelX = vx;
        this.targetVelY = vy;
        this.targetVelZ = vz;
        return this;
    }

    @Override
    public ParticleBuilder targetEntity(Entity target){
        this.target = EntityReference.of(target);
        return this;
    }

    @Override
    public ParticleBuilder length(double length){
        this.length = length;
        return this;
    }

    @Override
    public void tick(){
        super.tick();

    }

    @Override
    protected void extractSurface(ExtractFlow extractFlow) {

    }
}
