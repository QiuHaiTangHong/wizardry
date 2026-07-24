package top.begonia.wizardry.core.entity.construct;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.core.registry.WizardryEntityDataSerializers;
import top.begonia.wizardry.core.util.AllyDesignationSystem;

import java.util.Optional;
import java.util.UUID;

public abstract class MagicConstructEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Optional<UUID>> DATA_CASTER_UUID = SynchedEntityData.defineId(MagicConstructEntity.class, WizardryEntityDataSerializers.CASTER_UUID.get());
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(MagicConstructEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_DAMAGE_MULTIPLIER = SynchedEntityData.defineId(MagicConstructEntity.class, EntityDataSerializers.FLOAT);

    public MagicConstructEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setBoundingBox(this.getBoundingBox().deflate(1.0f));
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(DATA_CASTER_UUID, Optional.empty());
        builder.define(DATA_LIFETIME, 600);
        builder.define(DATA_DAMAGE_MULTIPLIER, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > this.getLifetime() && this.getLifetime() != -1) {
            this.discard();
        }
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        this.getEntityData().set(DATA_CASTER_UUID, valueInput.read("casterUUID", UUIDUtil.CODEC));
        this.getEntityData().set(DATA_LIFETIME, valueInput.getIntOr("lifetime", 600));
        this.getEntityData().set(DATA_DAMAGE_MULTIPLIER, valueInput.getFloatOr("damageMultiplier", 1.0f));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        valueOutput.storeNullable("casterUUID", UUIDUtil.CODEC, this.getEntityData().get(DATA_CASTER_UUID).orElse(null));
        valueOutput.putInt("lifetime", this.getEntityData().get(DATA_LIFETIME));
        valueOutput.putFloat("damageMultiplier", this.getEntityData().get(DATA_DAMAGE_MULTIPLIER));
    }

    public int getLifetime() {
        return this.getEntityData().get(DATA_LIFETIME);
    }

    public void setLifetime(int lifetime) {
        this.getEntityData().set(DATA_LIFETIME, lifetime);
    }

    public float getDamageMultiplier() {
        return this.getEntityData().get(DATA_DAMAGE_MULTIPLIER);
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.getEntityData().set(DATA_DAMAGE_MULTIPLIER, damageMultiplier);
    }

    public boolean isValidTarget(Entity target) {
        return AllyDesignationSystem.isValidTarget(this.getOwner(), target);
    }

    @Override
    public @Nullable Entity getOwner() {
        return this.getEntityData().get(DATA_CASTER_UUID).map(uuid -> this.level().getEntity(uuid)).orElse(null);
    }

    public void setOwner(Entity entity) {
        if (entity != null) {
            this.getEntityData().set(DATA_CASTER_UUID, Optional.of(entity.getUUID()));
        }
    }
}
