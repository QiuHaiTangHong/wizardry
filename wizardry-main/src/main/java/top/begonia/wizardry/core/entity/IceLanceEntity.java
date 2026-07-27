package top.begonia.wizardry.core.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;

public class IceLanceEntity extends LivingEntity {
    public IceLanceEntity(EntityType<? extends IceLanceEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public @NonNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}
