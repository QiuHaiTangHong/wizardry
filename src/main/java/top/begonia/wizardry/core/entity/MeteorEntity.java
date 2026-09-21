package top.begonia.wizardry.core.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;

public class MeteorEntity extends FallingBlockEntity {
    public MeteorEntity(EntityType<? extends MeteorEntity> type, Level level) {
        super(type, level);
    }
}
