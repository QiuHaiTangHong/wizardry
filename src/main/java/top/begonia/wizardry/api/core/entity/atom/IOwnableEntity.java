package top.begonia.wizardry.api.core.entity.atom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;

public interface IOwnableEntity extends OwnableEntity {
    void setOwner(LivingEntity owner);
}
