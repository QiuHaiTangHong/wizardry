package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.server.level.ServerLevel;
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

public class FireRingEntity extends ScaledConstructEntity {
    public FireRingEntity(EntityType<?> type, Level level) {
        super(type, level);
        float r = WizardrySpells.RING_OF_FIRE.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS);
        this.setSizeMultiplier(r);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        if (this.tickCount % 40 == 1) {
            this.playSound(WizardrySounds.ENTITY_FIRE_RING_AMBIENT.get(), 4.0f, 0.7f);
        }

        super.tick();

        if (this.tickCount % 5 == 0 && !this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            List<LivingEntity> targets = EntityUtils.getLivingWithinCylinder(
                    this.getBbWidth() / 2,
                    this.getX(), this.getY(), this.getZ(),
                    this.getBbHeight(),
                    this.level()
            );

            for (LivingEntity target : targets) {

                if (this.isValidTarget(target)) {

                    Vec3 velocity = target.getDeltaMovement();
                    double velX = velocity.x;
                    double velY = velocity.y;
                    double velZ = velocity.z;

                    if (!EntityUtils.isEntityImmune(WizardryDamageTypes.FIRE.apply(this.registryAccess()), target)) {

                        target.setRemainingFireTicks((int) WizardrySpells.RING_OF_FIRE.get().getBaseProperty(AbstractSpell.BURN_DURATION) * 20);

                        float damage = WizardrySpells.RING_OF_FIRE.get().getBaseProperty(AbstractSpell.DAMAGE) * this.getDamageMultiplier();

                        if (this.getOwner() != null) {
                            target.hurtServer(
                                    serverLevel,
                                    WizardryDamageSource.causeIndirectMagicDamage(
                                            WizardryDamageTypes.FIRE.apply(this.registryAccess()),
                                            this,
                                            this.getOwner(),
                                            false
                                    ),
                                    damage
                            );
                        } else {
                            target.hurtServer(
                                    serverLevel,
                                    new DamageSources(this.registryAccess()).magic(),
                                    damage
                            );
                        }
                    }

                    // Removes knockback
                    target.setDeltaMovement(velX, velY, velZ);
                }
            }
        }
    }
}
