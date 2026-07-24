package top.begonia.wizardry.core.entity.living.minion;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.core.entity.living.ISummonedCreature;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardryEntityDataSerializers;

import java.util.Optional;
import java.util.UUID;

public class WitherSkeletonMinionEntity extends WitherSkeleton implements ISummonedCreature {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(WitherSkeletonMinionEntity.class, WizardryEntityDataSerializers.CASTER_UUID.get());
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(WitherSkeletonMinionEntity.class, EntityDataSerializers.INT);

    public WitherSkeletonMinionEntity(EntityType<? extends WitherSkeleton> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public WitherSkeletonMinionEntity(Level level) {
        this(WizardryEntities.WITHER_SKELETON_MINION.get(), level);
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return WitherSkeleton
                .createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER_UUID, Optional.empty());
        builder.define(DATA_LIFETIME, -1);
    }

    @Override
    public void tick() {
        super.tick();
        this.updateDelegate();
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        valueOutput.storeNullable("casterUUID", UUIDUtil.CODEC, this.getEntityData().get(DATA_OWNER_UUID).orElse(null));
        valueOutput.putInt("lifetime", this.getEntityData().get(DATA_LIFETIME));
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        this.getEntityData().set(DATA_OWNER_UUID, valueInput.read("casterUUID", UUIDUtil.CODEC));
        this.getEntityData().set(DATA_LIFETIME, valueInput.getIntOr("lifetime", -1));
    }

    @Override
    public int getLifetime() {
        return this.getEntityData().get(DATA_LIFETIME);
    }

    @Override
    public void setLifetime(int lifetime) {
        this.getEntityData().set(DATA_LIFETIME, lifetime);
    }

    @Override
    public @Nullable Entity getOwner() {
        return this.getEntityData().get(DATA_OWNER_UUID).map(uuid -> this.level().getEntity(uuid)).orElse(null);
    }

    @Override
    public void setOwner(Entity entity) {
        if (entity != null) {
            this.getEntityData().set(DATA_OWNER_UUID, Optional.of(entity.getUUID()));
        }
    }

    @Override
    public void onSpawn() {
        this.spawnParticleEffect();
    }

    @Override
    public void onDespawn() {
        this.spawnParticleEffect();
    }

    @Override
    public boolean hasParticleEffect() {
        return true;
    }

    @Override
    public @NonNull Component getDisplayName() {
        if (this.getOwner() != null) {
            return Component.translatable(NAMEPLATE_TRANSLATION_KEY, this.getOwner().getName(), this.getName());
        } else {
            return super.getDisplayName();
        }
    }

    @Override
    public boolean hasCustomName() {
        return ClientConfig.summonedCreatureNames && this.getOwner() != null;
    }

    private void spawnParticleEffect() {
        if (this.level().isClientSide()) {
            for (int i = 0; i < 15; i++) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX() + this.getRandom().nextFloat() - 0.5f,
                        this.getY() + this.getRandom().nextFloat() * this.getBbHeight(), this.getZ() + this.getRandom().nextFloat() - 0.5f, 0, 0, 0);
            }
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, LivingEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                0,
                false,
                true,
                getTargetSelector()
        ));
        super.registerGoals();
    }
}
