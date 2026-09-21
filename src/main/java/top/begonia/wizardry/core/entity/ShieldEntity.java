package top.begonia.wizardry.core.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class ShieldEntity extends Entity {
    public ShieldEntity(EntityType<? extends ShieldEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {

    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {

    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {

    }
}
