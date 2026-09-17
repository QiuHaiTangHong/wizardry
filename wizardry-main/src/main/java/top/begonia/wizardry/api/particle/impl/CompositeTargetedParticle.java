package top.begonia.wizardry.api.particle.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeQuadParticle;
import top.begonia.wizardry.api.particle.extension.builder.ParticleBuilder;
import top.begonia.wizardry.api.particle.extension.extract.ExtractFlow;
import top.begonia.wizardry.api.particle.extension.extract.IPositionFlowOperation;
import top.begonia.wizardry.api.particle.extension.extract.IRotateFlowOperation;
import top.begonia.wizardry.api.particle.options.impl.TargetParticleOptions;

import javax.annotation.Nullable;

public class CompositeTargetedParticle<T extends TargetParticleOptions> extends CompositeQuadParticle<T> {
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
        // Translates the particle a short distance in front of the entity
        Entity linkEntity = positionOperation.getLinkEntity();
        Entity viewEntity = positionOperation.getCamera().entity();
        if (linkEntity != null && this.shouldApplyOriginOffset()) {
            if (linkEntity != viewEntity || Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                Vec3 look = linkEntity.getLookAngle().scale(THIRD_PERSON_AXIAL_OFFSET);
                positionOperation.setPosition(positionOperation.getPosition().add(look.toVector3f()));
            }
        }
        float partialTick = positionOperation.getPartialTick();
        double length = positionOperation.getAdditionalData("length", Double.class);
        Entity targetEntity = positionOperation.getAdditionalData("targetEntity", Entity.class);
        Vec3 targetPos = positionOperation.getAdditionalData("targetPos", Vec3.class);
        Vec3 targetVel = positionOperation.getAdditionalData("targetVel", Vec3.class);
        Vec3 targetDisplacement = new Vec3(0, 0, 0);
        if (targetEntity != null) {
            Vec3 targetPrevPos = positionOperation.getAdditionalData("targetPrevPos", Vec3.class);
            targetPos = targetPos.subtract(targetPrevPos)
                    .scale(partialTick)
                    .add(targetPrevPos)
                    .add(0.0F, targetEntity.getBbHeight() / 2, 0.0F);
            targetDisplacement = targetPos.subtract(positionOperation.getVec3Position()).add(targetVel.scale(partialTick));
        } else if (length > 0) {
            if (viewEntity != null) {
                Vec3 look = viewEntity.getLookAngle().scale(length);
                targetPos = positionOperation.getVec3Position().add(look);
                targetDisplacement = targetPos.subtract(positionOperation.getVec3Position());
            }
        }
        positionOperation.putAdditionalData("targetDisplacement", targetDisplacement, Vec3.class);
        positionOperation.putAdditionalData("length", targetDisplacement.length(), Double.class);
    }

    @Override
    protected void extractRotation(@NonNull IRotateFlowOperation rotateOperation) {
        super.extractRotation(rotateOperation);
        Vec3 targetDisplacement = rotateOperation.getAdditionalData("targetDisplacement", Vec3.class);
        double dx = targetDisplacement.x();
        double dy = targetDisplacement.y();
        double dz = targetDisplacement.z();
        Quaternionf rotate = rotateOperation.getRotate();
        float pitch = (float) (180d / Math.PI * Math.atan(-dy / Math.sqrt(dz * dz + dx * dx)));
        float yaw = (float) (180d / Math.PI * Math.atan2(dx, dz));
        rotate.rotateAxis(pitch, 1, 0, 0);
        rotate.rotateAxis(yaw, 0, 1, 0);
        rotateOperation.setRotate(rotate);
    }

    @Override
    protected void extractSurface(ExtractFlow extractFlow) {

    }
}
