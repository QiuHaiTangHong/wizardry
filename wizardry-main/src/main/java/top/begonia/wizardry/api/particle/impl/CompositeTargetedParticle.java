package top.begonia.wizardry.api.particle.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeParticle;
import top.begonia.wizardry.api.particle.extension.builder.ParticleBuilder;
import top.begonia.wizardry.api.particle.extension.extract.ExtractFlow;
import top.begonia.wizardry.api.particle.extension.extract.IPositionFlowOperation;
import top.begonia.wizardry.api.particle.extension.extract.IRotateFlowOperation;
import top.begonia.wizardry.api.particle.options.impl.TargetParticleOptions;

import javax.annotation.Nullable;

public class CompositeTargetedParticle<T extends TargetParticleOptions> extends CompositeParticle<T> {
    private static final double THIRD_PERSON_AXIAL_OFFSET = 1.2;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected double length;

    @Nullable
    protected EntityReference<Entity> target = null;

    public CompositeTargetedParticle(ClientLevel level, @NonNull T options, double x, double y, double z) {
        super(level, options, x, y, z);
    }

    protected boolean shouldApplyOriginOffset() {
        return true;
    }

    @Override
    public ParticleBuilder targetPosition(double x, double y, double z) {
        this.targetX = x;
        this.targetY = y;
        this.targetZ = z;
        return this;
    }

    @Override
    public ParticleBuilder targetVelocity(double vx, double vy, double vz) {
        return this;
    }

    @Override
    public ParticleBuilder targetEntity(Entity target) {
        this.target = EntityReference.of(target);
        return this;
    }

    @Override
    public ParticleBuilder length(double length) {
        this.length = length;
        return this;
    }

    @Override
    protected void extractGlobalAdditionalData(ExtractFlow extractFlow) {
        Entity targetEntity = EntityReference.getEntity(this.target, this.level);
        if (targetEntity != null) {
            this.extractFlow.putAdditionalData("targetEntity", targetEntity, Entity.class);
            this.extractFlow.putAdditionalData("targetPos", targetEntity.position(), Vec3.class);
            this.extractFlow.putAdditionalData("targetPrevPos", new Vec3(targetEntity.xo, targetEntity.yo, targetEntity.zo), Vec3.class);
            this.extractFlow.putAdditionalData("targetVel", targetEntity.getDeltaMovement(), Vec3.class);
        } else {
            this.extractFlow.putAdditionalData("targetPos", new Vec3(this.targetX, this.targetY, this.targetZ), Vec3.class);
        }
        this.extractFlow.putAdditionalData("length", this.length, Double.class);
    }

    @Override
    protected void extractPosition(@NonNull IPositionFlowOperation positionOperation) {
        super.extractPosition(positionOperation);
    }

    @Override
    protected void extractRotation(@NonNull IRotateFlowOperation rotateOperation) {
        super.extractRotation(rotateOperation);
    }

    @Override
    protected void extractSurface(ExtractFlow extractFlow) {

    }
}
