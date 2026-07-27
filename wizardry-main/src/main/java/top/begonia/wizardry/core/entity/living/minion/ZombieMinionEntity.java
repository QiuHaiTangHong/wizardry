package top.begonia.wizardry.core.entity.living.minion;

import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.core.entity.living.ISummonedCreature;
import top.begonia.wizardry.core.item.impl.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardryEntityDataSerializers;
import top.begonia.wizardry.core.registry.WizardryItems;

import java.util.Optional;
import java.util.UUID;

public class ZombieMinionEntity extends Zombie implements ISummonedCreature {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(ZombieMinionEntity.class, WizardryEntityDataSerializers.CASTER_UUID.get());
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(ZombieMinionEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SPAWN_PARTICLES = SynchedEntityData.defineId(ZombieMinionEntity.class, EntityDataSerializers.BOOLEAN);

    public ZombieMinionEntity(EntityType<? extends ZombieMinionEntity> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public ZombieMinionEntity(Level level) {
        this(WizardryEntities.ZOMBIE_MINION.get(), level);
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return Zombie
                .createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this,
                LivingEntity.class,
                0,
                false,
                true,
                getTargetSelector()
        ));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, false, 4, () -> false));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER_UUID, Optional.empty());
        builder.define(DATA_LIFETIME, -1);
        builder.define(SPAWN_PARTICLES, true);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public void setBaby(boolean babyZombie) {
    }

    @Override
    public void tick() {
        super.tick();
        this.updateDelegate();
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    @Override
    protected void populateDefaultEquipmentSlots(@NonNull RandomSource random, @NonNull DifficultyInstance difficulty) {
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        valueOutput.storeNullable("ownerUUID", UUIDUtil.CODEC, this.getEntityData().get(DATA_OWNER_UUID).orElse(null));
        valueOutput.putInt("lifetime", this.getEntityData().get(DATA_LIFETIME));
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        this.getEntityData().set(DATA_OWNER_UUID, valueInput.read("ownerUUID", UUIDUtil.CODEC));
        this.getEntityData().set(DATA_LIFETIME, valueInput.getIntOr("lifetime", -1));
    }

    @Override
    public boolean killedEntity(@NonNull ServerLevel level, @NonNull LivingEntity entity, @NonNull DamageSource source) {
        return false;
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
        if (this.getEntityData().get(SPAWN_PARTICLES)) {
            this.spawnParticleEffect();
        }
        if (isSunSensitive() && this.getOwner() instanceof Player player && ArtefactItem.isArtefactActive(player, WizardryItems.CHARM_UNDEAD_HELMETS.get())) {
            setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
        }
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
    public boolean hasAnimation() {
        return this.getEntityData().get(SPAWN_PARTICLES) || this.tickCount > 20;
    }

    private void spawnParticleEffect() {
        if (this.level().isClientSide()) {
            for (int i = 0; i < 15; i++) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX() + this.getRandom().nextFloat() - 0.5f,
                        this.getY() + this.getRandom().nextFloat() * 2, this.getZ() + this.getRandom().nextFloat() - 0.5f, 0, 0, 0);
            }
        }
    }
}
