package top.begonia.wizardry.core.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class RestrictOpenDoor extends Goal {
    private final Mob mob;

    public RestrictOpenDoor(Mob mob){
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        BlockPos blockpos = this.mob.blockPosition();
        return false;
    }
}
