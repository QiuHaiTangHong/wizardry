package top.begonia.wizardry.core.entity.living.minion;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.api.entity.hybrid.ISummonedCreature;
import top.begonia.wizardry.api.entity.ai.control.VexMoveControl;
import top.begonia.wizardry.core.entity.living.vex.AbstractVexFlavorEntity;
import top.begonia.wizardry.core.registry.WizardryParticles;

public class VexMinionEntity extends AbstractVexFlavorEntity implements TraceableEntity, ISummonedCreature {
    public static final float FLAP_DEGREES_PER_TICK = 45.836624F;
    public static final int TICKS_PER_FLAP = Mth.ceil(3.9269907F);

    public VexMinionEntity(EntityType<? extends VexMinionEntity> type, Level level) {
        super(type, level);
        this.moveControl = new VexMoveControl<>(this);
        this.xpReward = 3;
    }

    public static AttributeSupplier.@NonNull Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 14.0F)
                .add(Attributes.ATTACK_DAMAGE, 4.0F);
    }

    @Override
    protected boolean isAffectedByBlocks() {
        return !this.isRemoved();
    }

    @Override
    public boolean isFlapping() {
        return this.tickCount % TICKS_PER_FLAP == 0;
    }

    @Override
    public void restoreFrom(@NonNull Entity oldEntity) {
        super.restoreFrom(oldEntity);
        if (oldEntity instanceof VexMinionEntity vex) {
            this.setOwner(vex.getOwner());
        }
    }

    @Override
    public void setLastHurtByMob(@Nullable LivingEntity hurtBy) {
        if (this.shouldLastHurtByMob(hurtBy)) {
            super.setLastHurtByMob(hurtBy);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.removeAllGoals(_ -> true);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class,
                0, false, false, this.getTargetSelector()));
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        this.updateDelegate();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    @Override
    protected void populateDefaultEquipmentSlots(
            @NonNull RandomSource random,
            @NonNull DifficultyInstance difficulty
    ) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
            @NonNull ServerLevelAccessor level,
            @NonNull DifficultyInstance difficulty,
            @NonNull EntitySpawnReason spawnReason,
            @Nullable SpawnGroupData groupData
    ) {
        RandomSource random = level.getRandom();
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(level, random, difficulty);
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
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
    public int getAnimationColour(float animationProgress) {
        return 0xEF829C;
    }

    private void spawnParticleEffect() {
        if (this.level() instanceof ClientLevel clientLevel) {
            for (int i = 0; i < 15; i++) {
                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        WizardryParticles.DARK_MAGIC.get(),
                        this.getX() + this.getRandom().nextFloat(),
                        this.getY() + this.getRandom().nextFloat(),
                        this.getZ() + this.getRandom().nextFloat()
                ).ifPresent(p -> p.color(0.3f, 0.3f, 0.3f)
                        .spawn()
                );
            }
        }
    }

    @Override
    protected @NonNull SoundEvent getHurtSound(@NonNull DamageSource source) {
        return SoundEvents.VEX_HURT;
    }

    @Override
    protected @NonNull SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }
}
