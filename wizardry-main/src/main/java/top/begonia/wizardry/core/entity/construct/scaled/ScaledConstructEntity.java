package top.begonia.wizardry.core.entity.construct.scaled;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.entity.construct.MagicConstructEntity;

public abstract class ScaledConstructEntity extends MagicConstructEntity {
    private static final EntityDataAccessor<Float> DATA_SIZE_MULTIPLIER = SynchedEntityData.defineId(
            ScaledConstructEntity.class,
            EntityDataSerializers.FLOAT
    );

    public ScaledConstructEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SIZE_MULTIPLIER, 1.0F);
    }

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        EntityDimensions dimensions = this.getType().getDimensions();
        float scale = this.getSizeMultiplier();
        if (scale == 1.0F) {
            return dimensions;
        }
        return dimensions.scale(
                this.shouldScaleWidth() ? scale : 1.0F,
                this.shouldScaleHeight() ? scale : 1.0F
        );
    }

    public float getSizeMultiplier() {
        return this.entityData.get(DATA_SIZE_MULTIPLIER);
    }

    public void setSizeMultiplier(float sizeMultiplier) {
        this.entityData.set(DATA_SIZE_MULTIPLIER, sizeMultiplier);
        this.refreshDimensions();
    }

    protected boolean shouldScaleWidth() {
        return true;
    }

    protected boolean shouldScaleHeight() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.setSizeMultiplier(valueInput.getFloatOr("sizeMultiplier", this.getSizeMultiplier()));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putFloat("sizeMultiplier", this.getSizeMultiplier());
    }

    @Override
    public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_SIZE_MULTIPLIER.equals(accessor)) {
            this.refreshDimensions();
        }
    }
}
