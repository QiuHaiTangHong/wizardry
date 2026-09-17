package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class FireSigilEntity extends ScaledConstructEntity {
    public FireSigilEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setSizeMultiplier(WizardrySpells.FIRE_SIGIL.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS));
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();

            List<LivingEntity> targets = EntityUtils.getLivingWithinCylinder(
                    this.getBbWidth() / 2,
                    this.position(),
                    this.getBbHeight(),
                    this.level()
            );

            for (LivingEntity target : targets) {

                if (this.isValidTarget(target)) {

                    Vec3 velocity = this.getDeltaMovement();
                    double velX = velocity.x;
                    double velY = velocity.y;
                    double velZ = velocity.z;

                    target.hurtServer(
                            serverLevel,
                            this.getOwner() != null
                            ? WizardryDamageSource.causeIndirectMagicDamage(
                                    WizardryDamageTypes.FIRE.apply(this.registryAccess()),
                                    this,
                                    this.getOwner(),
                                    false
                            )
                            : new DamageSources(this.registryAccess()).magic(),
                            WizardrySpells.FIRE_SIGIL.get().getBaseProperty(AbstractSpell.DAMAGE) * this.getDamageMultiplier()
                    );

                    // Removes knockback
                    this.setDeltaMovement(velX, velY, velZ);

                    if (!EntityUtils.isEntityImmune(WizardryDamageTypes.FIRE.apply(this.registryAccess()), target)) {
                        target.setRemainingFireTicks((int) WizardrySpells.FIRE_SIGIL.get().getBaseProperty(AbstractSpell.BURN_DURATION) * 20);
                    }

                    this.playSound(WizardrySounds.ENTITY_FIRE_SIGIL_TRIGGER.get(), 1, 1);

                    // The trap is destroyed once triggered.
                    this.discard();
                }
            }
        } else if (this.random.nextInt(15) == 0) {
            double radius = (0.5 + this.random.nextDouble() * 0.3) * this.getBbWidth() / 2;
            float angle = this.random.nextFloat() * (float) Math.PI * 2;
            this.level().addParticle(
                    ParticleTypes.FLAME,
                    this.getX() + radius * Mth.cos(angle),
                    this.getY() + 0.1,
                    this.getZ() + radius * Mth.sin(angle),
                    0, 0, 0
            );
        }
    }
}
