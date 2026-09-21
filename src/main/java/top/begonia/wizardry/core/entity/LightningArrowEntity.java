package top.begonia.wizardry.core.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.projectile.Projectile;
import org.jspecify.annotations.NonNull;

public class LightningArrowEntity extends Projectile {
    public LightningArrowEntity(EntityType<? extends LightningArrowEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {

    }
}
