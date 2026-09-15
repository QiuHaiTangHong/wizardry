package top.begonia.wizardry.api.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import top.begonia.wizardry.api.entity.atom.IAttachedEntity;

import java.util.EnumSet;

public class VexMoveRandomGoal<T extends PathfinderMob & IAttachedEntity> extends Goal {
    private final T mob;

    public VexMoveRandomGoal(T mob) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return !this.mob.getMoveControl().hasWanted()
                && this.mob.getRandom().nextInt(7) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void tick() {

        BlockPos blockpos = this.mob.getBoundOrigin();

        if (blockpos == null) {
            blockpos = this.mob.blockPosition();
        }

        for (int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.offset(
                    this.mob.getRandom().nextInt(15) - 7,
                    this.mob.getRandom().nextInt(11) - 5,
                    this.mob.getRandom().nextInt(15) - 7
            );

            if (this.mob.level()
                    .getBlockState(blockpos1)
                    .isAir()
            ) {
                this.mob.getMoveControl().setWantedPosition(
                        (double) blockpos1.getX() + 0.5D,
                        (double) blockpos1.getY() + 0.5D,
                        (double) blockpos1.getZ() + 0.5D, 0.25D
                );

                if (this.mob.getTarget() == null) {
                    this.mob.getLookControl().setLookAt(
                            (double) blockpos1.getX() + 0.5D,
                            (double) blockpos1.getY() + 0.5D,
                            (double) blockpos1.getZ() + 0.5D,
                            180.0F,
                            20.0F
                    );
                }

                break;
            }
        }
    }
}
