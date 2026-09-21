package top.begonia.wizardry.core.entity.living.minion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.registry.WizardryEntities;

public class HuskMinionEntity extends ZombieMinionEntity {
    public HuskMinionEntity(EntityType<? extends HuskMinionEntity> type, Level level) {
        super(type, level);
    }

    public HuskMinionEntity(Level level) {
        this(WizardryEntities.HUSK_MINION.get(), level);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public boolean doHurtTarget(@NonNull ServerLevel level, @NonNull Entity target) {
        boolean flag = super.doHurtTarget(level, target);
        if (flag && this.getMainHandItem().isEmpty() && target instanceof LivingEntity livingTarget) {
            float f = level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            livingTarget.addEffect(new MobEffectInstance(MobEffects.HUNGER, 140 * (int) f));
        }
        return flag;
    }

    @Override
    protected @NonNull SoundEvent getAmbientSound() {
        return SoundEvents.HUSK_AMBIENT;
    }

    @Override
    protected @NonNull SoundEvent getHurtSound(@NonNull DamageSource damageSourceIn) {
        return SoundEvents.HUSK_HURT;
    }

    @Override
    protected @NonNull SoundEvent getDeathSound() {
        return SoundEvents.HUSK_DEATH;
    }

    @Override
    protected @NonNull SoundEvent getStepSound() {
        return SoundEvents.HUSK_STEP;
    }
}