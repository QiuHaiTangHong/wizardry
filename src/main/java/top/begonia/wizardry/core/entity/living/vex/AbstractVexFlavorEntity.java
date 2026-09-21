package top.begonia.wizardry.core.entity.living.vex;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.entity.atom.IFlagEntity;
import top.begonia.wizardry.api.entity.atom.ILifeTicksEntity;
import top.begonia.wizardry.api.entity.atom.IOwnableEntity;
import top.begonia.wizardry.api.entity.atom.IAttachedEntity;
import top.begonia.wizardry.api.entity.ai.goal.VexChargeAttackGoal;
import top.begonia.wizardry.api.entity.ai.goal.VexMoveRandomGoal;

public abstract class AbstractVexFlavorEntity extends Monster implements IAttachedEntity, ILifeTicksEntity, IOwnableEntity, IFlagEntity {
    protected static final EntityDataAccessor<Integer> FLAGS_ID = SynchedEntityData.defineId(
            AbstractVexFlavorEntity.class,
            EntityDataSerializers.INT
    );
    private @Nullable EntityReference<LivingEntity> owner;
    private @Nullable BlockPos boundOrigin;
    private boolean hasLifeTicks;
    private int lifeTicks;

    protected AbstractVexFlavorEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new VexChargeAttackGoal<>(this, 1.0F));
        this.goalSelector.addGoal(8, new VexMoveRandomGoal<>(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
    }

    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(FLAGS_ID, 0);
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = true;
        this.setNoGravity(true);
        if (this.hasLifeTicks){
            this.updateDelegate();
        }
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.storeNullable("bound_pos", BlockPos.CODEC, this.boundOrigin);
        valueOutput.putInt("life_ticks", this.lifeTicks);
        EntityReference.store(this.owner, valueOutput, "owner");
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        this.boundOrigin = valueInput.read("bound_pos", BlockPos.CODEC).orElse(null);
        valueInput.getInt("life_ticks").ifPresentOrElse(this::setLifetime, () -> this.hasLifeTicks = false);
        this.owner = EntityReference.read(valueInput, "owner");
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public int getFlags() {
        return this.entityData.get(FLAGS_ID);
    }

    @Override
    public void setFlags(int flags) {
        this.entityData.set(FLAGS_ID, flags);
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return this.owner;
    }

    @Override
    public @Nullable LivingEntity getOwner(){
        return EntityReference.getLivingEntity(this.owner, this.level());
    }

    @Override
    public void setOwner(LivingEntity owner) {
        this.owner = EntityReference.of(owner);
    }

    @Override
    public int getLifetime() {
        return this.lifeTicks;
    }

    @Override
    public void setLifetime(int ticks) {
        this.lifeTicks = ticks;
        if (lifeTicks > 0) {
            this.hasLifeTicks = true;
        }
    }

    @Override
    public @Nullable BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    @Override
    public void setBoundOrigin(@Nullable BlockPos boundOriginIn) {
        this.boundOrigin = boundOriginIn;
    }
}
