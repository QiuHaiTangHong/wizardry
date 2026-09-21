package top.begonia.wizardry.core.entity.construct;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.entity.atom.ILifeTicksEntity;
import top.begonia.wizardry.api.entity.atom.IOwnableEntity;
import top.begonia.wizardry.api.item.ISpellCastingItem;
import top.begonia.wizardry.core.registry.WizardryEntityDataSerializers;
import top.begonia.wizardry.core.util.AllyDesignationSystem;

import java.util.Optional;
import java.util.UUID;

public abstract class MagicConstructEntity extends Entity implements TraceableEntity, IOwnableEntity, ILifeTicksEntity {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(
            MagicConstructEntity.class,
            WizardryEntityDataSerializers.UUID.get()
    );
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(
            MagicConstructEntity.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Float> DATA_DAMAGE_MULTIPLIER = SynchedEntityData.defineId(
            MagicConstructEntity.class,
            EntityDataSerializers.FLOAT
    );
    private EntityReference<LivingEntity> owner;

    public MagicConstructEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(DATA_OWNER, Optional.empty())
                .define(DATA_LIFETIME, 600)
                .define(DATA_DAMAGE_MULTIPLIER, 1.0F);
    }

    @Override
    public @NonNull InteractionResult interact(
            @NonNull Player player,
            @NonNull InteractionHand hand,
            @NonNull Vec3 location
    ) {
        if (this.getLifetime() == -1
                && this.getOwner() == player
                && player.isShiftKeyDown()
                && player.getItemInHand(hand).getItem() instanceof ISpellCastingItem
        ) {
            this.despawn();
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand, location);
    }

    public void despawn() {
        this.remove(RemovalReason.DISCARDED);
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
        this.owner = EntityReference.read(valueInput, "owner");
        this.entityData.set(
                DATA_OWNER,
                this.owner == null
                        ? Optional.empty()
                        : Optional.of(this.owner.getUUID())
        );
        this.setLifetime(valueInput.getIntOr("lifetime", 600));
        this.setDamageMultiplier(valueInput.getFloatOr("damageMultiplier", 1.0F));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        EntityReference.store(this.owner, valueOutput, "owner");
        valueOutput.putInt("lifetime", this.getLifetime());
        valueOutput.putFloat("damageMultiplier", this.getDamageMultiplier());
    }

    @Override
    public int getLifetime() {
        return this.entityData.get(DATA_LIFETIME);
    }

    @Override
    public void setLifetime(int lifetime) {
        this.entityData.set(DATA_LIFETIME, lifetime);
    }

    @Override
    public void updateDelegate() {

    }

    public float getDamageMultiplier() {
        return this.entityData.get(DATA_DAMAGE_MULTIPLIER);
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.entityData.set(DATA_DAMAGE_MULTIPLIER, damageMultiplier);
    }

    public boolean isValidTarget(Entity target) {
        return AllyDesignationSystem.isValidTarget(this.getOwner(), target);
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return this.owner;
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return EntityReference.getLivingEntity(this.owner, this.level());
    }

    @Override
    public void setOwner(LivingEntity entity) {
        this.owner = EntityReference.of(entity);
        this.entityData.set(
                DATA_OWNER,
                entity == null
                        ? Optional.empty()
                        : Optional.of(entity.getUUID())
        );
    }

    @Override
    public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_OWNER.equals(accessor)) {
            Optional<UUID> ownerUUID = this.entityData.get(DATA_OWNER);
            this.owner = ownerUUID.<EntityReference<LivingEntity>>map(EntityReference::of)
                    .orElse(null);
        }
    }
}
