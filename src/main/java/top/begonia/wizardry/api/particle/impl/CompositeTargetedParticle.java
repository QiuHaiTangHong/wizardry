package top.begonia.wizardry.api.particle.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.particle.CompositeParticle;
import top.begonia.wizardry.api.particle.extension.builder.ParticleBuilder;
import top.begonia.wizardry.api.particle.extension.extract.ExtractFlow;
import top.begonia.wizardry.api.particle.extension.extract.IPositionFlowOperation;
import top.begonia.wizardry.api.particle.extension.extract.IRotateFlowOperation;
import top.begonia.wizardry.api.particle.options.impl.TargetParticleOptions;

import javax.annotation.Nullable;

public abstract class CompositeTargetedParticle<T extends TargetParticleOptions> extends CompositeParticle<T> {
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
        } else {
            this.extractFlow.putAdditionalData("targetPos", new Vec3(this.targetX, this.targetY, this.targetZ), Vec3.class);
        }
        this.extractFlow.putAdditionalData("length", this.length, Double.class);
    }

    @Override
    protected void extractPosition(@NonNull IPositionFlowOperation positionOperation) {
        super.extractPosition(positionOperation);

        Vec3 cameraPos = positionOperation.getCamera().position();
        Entity linkEntity = positionOperation.getLinkEntity();
        Entity viewEntity = positionOperation.getCamera().entity();

        // 如果链接实体不为空，且启用 OriginOffset 则将粒子的位置沿着视线方向移动 THIRD_PERSON_AXIAL_OFFSET 大小。
        if (linkEntity != null && this.shouldApplyOriginOffset()) {
            if (linkEntity != viewEntity || Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
                Vec3 look = linkEntity.getLookAngle().scale(THIRD_PERSON_AXIAL_OFFSET);
                positionOperation.setPosition(positionOperation.getPosition().add(look.toVector3f()));
            }
        }

        float partialTick = positionOperation.getPartialTick();
        double length = positionOperation.getAdditionalData("length", Double.class);
        Entity targetEntity = positionOperation.getAdditionalData("targetEntity", Entity.class);
        Vec3 targetDirectionVec = null;
        if (targetEntity != null) {
            Vec3 targetPos = targetEntity.position().subtract(cameraPos);
            Vec3 targetVel = targetEntity.getDeltaMovement();
            Vec3 targetPrevPos = new Vec3(targetEntity.xo, targetEntity.yo, targetEntity.zo).subtract(cameraPos);
            targetPos = targetPos.subtract(targetPrevPos)
                    .scale(partialTick)
                    .add(targetPrevPos)
                    .add(0.0F, targetEntity.getBbHeight() / 2, 0.0F);
            targetDirectionVec = targetPos.subtract(positionOperation.getVec3Position())
                    .add(targetVel.scale(partialTick));
        } else if (length > 0 && viewEntity != null) {
            Vec3 look = viewEntity.getLookAngle().scale(length);
            Vec3 targetPos = positionOperation.getVec3Position().add(look);
            targetDirectionVec = targetPos.subtract(positionOperation.getVec3Position());
        }

        if (targetDirectionVec != null) {
            positionOperation.putAdditionalData("targetDirectionVec", targetDirectionVec.normalize(), Vec3.class);
            positionOperation.putAdditionalData("length", targetDirectionVec.length(), Double.class);
        }
    }

    @Override
    protected void extractRotation(@NonNull IRotateFlowOperation rotateOperation) {
        // 该粒子不需要计算粒子全局旋转, 旋转由粒子起始位置指向目标位置的方向矢量确定。
    }

    @Override
    protected abstract void extractSurface(ExtractFlow extractFlow);
}
