package top.begonia.wizardry.core.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.Projectile;

public class ForceArrowEntity extends Projectile {
    public ForceArrowEntity(EntityType<? extends ForceArrowEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }
}
