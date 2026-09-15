package top.begonia.wizardry.core.entity.living.minion;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityReference;
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
import top.begonia.wizardry.api.entity.hybrid.ISummonedCreature;
import top.begonia.wizardry.core.registry.WizardryEntities;

public class WitherSkeletonMinionEntity extends WitherSkeleton implements ISummonedCreature {
    private @Nullable EntityReference<LivingEntity> owner;
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
        builder.define(DATA_LIFETIME, -1);
    }

    @Override
    public void tick() {
        super.tick();
        this.updateDelegate();
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        EntityReference.store(this.owner, valueOutput, "owner");
        valueOutput.putInt("lifetime", this.getEntityData().get(DATA_LIFETIME));
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        this.owner = EntityReference.read(valueInput, "owner");
        this.getEntityData().set(DATA_LIFETIME, valueInput.getIntOr("lifetime", -1));
    }

    @Override
    public void setOwner(LivingEntity entity) {
        this.owner = EntityReference.of(entity);
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
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return this.owner;
    }

    @Override
    public @Nullable LivingEntity getOwner() {
        return EntityReference.getLivingEntity(this.owner, this.level());
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
