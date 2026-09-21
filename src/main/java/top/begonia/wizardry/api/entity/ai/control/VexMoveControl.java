package top.begonia.wizardry.api.entity.ai.control;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;

public class VexMoveControl<T extends Mob> extends MoveControl<T> {
    public VexMoveControl(T mob) {
        super(mob);
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO) {
            double d0 = this.wantedX - this.mob.getX();
            double d1 = this.wantedY - this.mob.getY();
            double d2 = this.wantedZ - this.mob.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            d3 = Math.sqrt(d3);

            if (d3 < this.mob.getBoundingBox().getSize()) {

                this.operation = Operation.WAIT;
                mob.setDeltaMovement(
                        mob.getDeltaMovement().multiply(0.5D, 0.5D, 0.5D)
                );

            } else {
                mob.setDeltaMovement(
                        mob.getDeltaMovement().x + d0 / d3 * 0.05D * this.speedModifier,
                        mob.getDeltaMovement().y + d1 / d3 * 0.05D * this.speedModifier,
                        mob.getDeltaMovement().z + d2 / d3 * 0.05D * this.speedModifier
                );

                if (this.mob.getTarget() == null) {
                    mob.setYRot(
                            -((float)Math.atan2(
                                    mob.getDeltaMovement().x,
                                    mob.getDeltaMovement().z
                            )) * (180F / (float)Math.PI)
                    );
                } else {
                    double dx = mob.getTarget().getX() - mob.getX();
                    double dz = mob.getTarget().getZ() - mob.getZ();
                    mob.setYRot(
                            -((float)Math.atan2(dx, dz))
                                    * (180F / (float)Math.PI)
                    );
                }

                mob.yBodyRot = mob.getYRot();
            }
        }
    }
}
