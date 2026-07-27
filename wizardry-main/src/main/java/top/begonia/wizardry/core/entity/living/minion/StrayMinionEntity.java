package top.begonia.wizardry.core.entity.living.minion;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.core.registry.WizardryEntities;

public class StrayMinionEntity extends SkeletonMinionEntity {
    public StrayMinionEntity(EntityType<? extends Skeleton> type, Level level) {
        super(type, level);
    }

    public StrayMinionEntity(Level level) {
        this(WizardryEntities.STRAY_MINION.get(), level);
    }

    @Override
    protected @NonNull SoundEvent getAmbientSound() {
        return SoundEvents.STRAY_AMBIENT;
    }

    @Override
    protected @NonNull SoundEvent getHurtSound(@NonNull DamageSource source) {
        return SoundEvents.STRAY_HURT;
    }

    @Override
    protected @NonNull SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }

    @Override
    protected @NonNull SoundEvent getStepSound() {
        return SoundEvents.STRAY_STEP;
    }

    @Override
    protected @NonNull AbstractArrow getArrow(@NonNull ItemStack projectile, float power, @Nullable ItemStack firingWeapon) {
        AbstractArrow abstractArrow = super.getArrow(projectile, power, firingWeapon);
        if (abstractArrow instanceof Arrow arrow) {
            arrow.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 600));
        }

        return abstractArrow;
    }
}
