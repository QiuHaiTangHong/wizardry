package top.begonia.wizardry.api.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import top.begonia.wizardry.api.entity.atom.IFlagEntity;
import top.begonia.wizardry.api.entity.utils.EntityFlags;

import java.util.EnumSet;

public class VexChargeAttackGoal<T extends Mob & IFlagEntity> extends Goal {
    private final T mob;
    private final double speedModifier;

    public VexChargeAttackGoal(T mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getTarget() != null
                && this.mob.getTarget().isAlive()
                && !this.mob.getMoveControl().hasWanted()
                && this.mob.getRandom().nextInt(7) == 0
        ) {
            return this.mob.distanceToSqr(this.mob.getTarget()) > 4.0D;
        } else {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getMoveControl().hasWanted()
                && this.mob.getFlag(EntityFlags.CHARGING)
                && this.mob.getTarget() != null
                && this.mob.getTarget().isAlive();
    }

    @Override
    public void start() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return;
        }
        Vec3 eyePosition = livingEntity.getEyePosition();
        this.mob.getMoveControl().setWantedPosition(
                eyePosition.x,
                eyePosition.y,
                eyePosition.z,
                this.speedModifier
        );
        this.mob.setFlag(EntityFlags.CHARGING, true);
    }

    @Override
    public void stop() {
        this.mob.setFlag(EntityFlags.CHARGING, false);
    }

    @Override
    public void tick() {

        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity == null) {
            return;
        }

        if (this.mob.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
            this.mob.distanceToSqr(livingEntity);
            this.mob.setFlag(EntityFlags.CHARGING, false);
        } else {
            double d0 = this.mob.distanceToSqr(livingEntity);

            if (d0 < 9.0D) {
                Vec3 vec3 = livingEntity.getEyePosition();
                this.mob.getMoveControl().setWantedPosition(
                        vec3.x,
                        vec3.y,
                        vec3.z,
                        this.speedModifier
                );
            }
        }
    }
}
