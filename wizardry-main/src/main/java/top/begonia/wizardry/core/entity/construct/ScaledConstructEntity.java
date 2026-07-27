package top.begonia.wizardry.core.entity.construct;

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

public class ScaledConstructEntity extends MagicConstructEntity {
    protected static final EntityDataAccessor<Float> DATA_ENTITY_WIDTH = SynchedEntityData.defineId(
            ScaledConstructEntity.class,
            EntityDataSerializers.FLOAT
    );
    protected static final EntityDataAccessor<Float> DATA_ENTITY_HEIGHT = SynchedEntityData.defineId(
            ScaledConstructEntity.class,
            EntityDataSerializers.FLOAT
    );
    protected static final EntityDataAccessor<Float> DATA_ENTITY_SIZE_MULTIPLIER = SynchedEntityData.defineId(
            ScaledConstructEntity.class,
            EntityDataSerializers.FLOAT
    );

    public ScaledConstructEntity(EntityType<?> type, Level level) {
        super(type, level);
        EntityDimensions dimensions = this.getType().getDimensions();
        this.setEntityWidth(dimensions.width());
        this.setEntityHeight(dimensions.height());
        this.setEntitySizeMultiplier(1.0F);
    }

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        float multiplier = this.getEntitySizeMultiplier();
        float newWidth = shouldScaleWidth() ? this.getEntityWidth() * multiplier : this.getEntityWidth();
        float newHeight = shouldScaleHeight() ? this.getEntityHeight() * multiplier : this.getEntityHeight();
        float newEyeHeight = super.getDimensions(pose).eyeHeight() * multiplier;
        return EntityDimensions.scalable(newWidth, newHeight).withEyeHeight(newEyeHeight);
    }

    public float getEntitySizeMultiplier() {
        return this.getEntityData().get(DATA_ENTITY_SIZE_MULTIPLIER);
    }

    public void setEntitySizeMultiplier(float sizeMultiplier) {
        this.getEntityData().set(DATA_ENTITY_SIZE_MULTIPLIER, sizeMultiplier);
        this.refreshDimensions();
    }

    protected boolean shouldScaleWidth() {
        return true;
    }

    public float getEntityWidth() {
        return this.getEntityData().get(DATA_ENTITY_WIDTH);
    }

    public void setEntityWidth(float entityWidth) {
        this.getEntityData().set(DATA_ENTITY_WIDTH, entityWidth);
        this.refreshDimensions();
    }

    protected boolean shouldScaleHeight() {
        return true;
    }

    public float getEntityHeight() {
        return this.getEntityData().get(DATA_ENTITY_HEIGHT);
    }

    public void setEntityHeight(float entityHeight) {
        this.getEntityData().set(DATA_ENTITY_HEIGHT, entityHeight);
        this.refreshDimensions();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ENTITY_SIZE_MULTIPLIER, 1.0F);
        builder.define(DATA_ENTITY_HEIGHT, 1.0F);
        builder.define(DATA_ENTITY_WIDTH, 1.0F);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.entityData.set(DATA_ENTITY_SIZE_MULTIPLIER, valueInput.getFloatOr("sizeEntityMultiplier", 1.0F));
        this.entityData.set(DATA_ENTITY_HEIGHT, valueInput.getFloatOr("entityHeight", 1.0F));
        this.entityData.set(DATA_ENTITY_WIDTH, valueInput.getFloatOr("entityWidth", 1.0F));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putFloat("sizeEntityMultiplier", this.getEntitySizeMultiplier());
        valueOutput.putFloat("entityHeight", this.getEntityHeight());
        valueOutput.putFloat("entityWidth", this.getEntityWidth());
    }
}
