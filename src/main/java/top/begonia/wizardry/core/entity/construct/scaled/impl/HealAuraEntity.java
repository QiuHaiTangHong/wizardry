package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class HealAuraEntity extends ScaledConstructEntity {
    public HealAuraEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setSizeMultiplier(WizardrySpells.HEALING_AURA.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS));
    }

    @Override
    public void tick() {

        if (this.tickCount % 25 == 1) {
            this.playSound(WizardrySounds.ENTITY_HEAL_AURA_AMBIENT.get(), 0.1f, 1.0f);
        }

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

                    if (target.isInvertedHealAndHarm()) {

                        Vec3 velocity = target.getDeltaMovement();
                        double velX = velocity.x;
                        double velY = velocity.y;
                        double velZ = velocity.z;

                        if (this.tickCount % 10 == 1) {
                            if (this.getOwner() != null) {
                                target.hurtServer(
                                        serverLevel,
                                        WizardryDamageSource.causeIndirectMagicDamage(
                                                WizardryDamageTypes.RADIANT.apply(this.registryAccess()),
                                                this,
                                                this.getOwner(),
                                                false
                                        ),
                                        WizardrySpells.HEALING_AURA.get().getBaseProperty(AbstractSpell.DAMAGE) * this.getDamageMultiplier()
                                );
                            } else {
                                target.hurtServer(
                                        serverLevel,
                                        new DamageSources(this.registryAccess()).magic(),
                                        WizardrySpells.HEALING_AURA.get().getBaseProperty(AbstractSpell.DAMAGE) * this.getDamageMultiplier()
                                );
                            }

                            // Removes knockback
                            this.setDeltaMovement(velX, velY, velZ);
                        }
                    }

                } else if (target.getHealth() < target.getMaxHealth() && target.tickCount % 5 == 0) {
                    target.heal(WizardrySpells.HEALING_AURA.get().getBaseProperty(AbstractSpell.HEALTH) * this.getDamageMultiplier());
                }
            }
        } else {
            for (int i = 1; i < 3; i++) {
                float brightness = 0.5f + (this.random.nextFloat() * 0.5f);
                double radius = this.random.nextDouble() * (this.getBbWidth() / 2);
                float angle = this.random.nextFloat() * (float) Math.PI * 2;
                ClientLevel clientLevel = (ClientLevel) this.level();
                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        WizardryParticles.SPARKLE.get(),
                        this.getX() + radius * Mth.cos(angle),
                        this.getY(),
                        this.getZ() + radius * Mth.sin(angle)
                ).ifPresent(p -> p.speed(0.0F, 0.05F, 0.0F)
                        .time(48 + this.random.nextInt(12))
                        .color(1.0f, 1.0f, brightness)
                        .spawn()
                );
            }
        }
    }
}
