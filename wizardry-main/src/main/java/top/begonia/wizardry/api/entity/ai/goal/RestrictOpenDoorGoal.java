package top.begonia.wizardry.api.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;

public class RestrictOpenDoorGoal extends Goal {
    @Override
    public boolean canUse() {
        return false;
    }
//    private final Mob entity;
//    private VillageDoorInfo frontDoor;
//    @Override
//    public boolean canUse() {
//        return false;
//    }
}
