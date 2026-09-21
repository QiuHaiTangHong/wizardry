package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardryMobEffects;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class BlizzardEntity extends ScaledConstructEntity {
    public BlizzardEntity(EntityType<?> type, Level level) {
        super(type, level);
        float r = WizardrySpells.BLIZZARD.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS);
        this.setSizeMultiplier(r);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        if (this.tickCount % 120 == 1) {
            this.playSound(WizardrySounds.ENTITY_BLIZZARD_AMBIENT.get(), 1.0f, 1.0f);
        }

        super.tick();

        // This is a good example of why you might define a spell base property without necessarily using it in the
        // spell - in fact, blizzard doesn't even have a spell class (yet)
        double radius = WizardrySpells.BLIZZARD.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS) * this.getSizeMultiplier();

        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            List<LivingEntity> targets = EntityUtils.getLivingWithinRadius(
                    radius,
                    this.getX(), this.getY(), this.getZ(),
                    this.level()
            );

            for (LivingEntity target : targets) {
                if (this.isValidTarget(target)) {

                    if (this.getOwner() != null) {
                        EntityUtils.attackEntityWithoutKnockback(
                                (ServerLevel) this.level(),
                                target,
                                WizardryDamageSource.causeIndirectMagicDamage(
                                        WizardryDamageTypes.FROST.apply(this.registryAccess()),
                                        this,
                                        this.getOwner(),
                                        false
                                ),
                                1 * this.getDamageMultiplier());
                    } else {
                        EntityUtils.attackEntityWithoutKnockback(
                                serverLevel,
                                target,
                                new DamageSources(this.registryAccess()).magic(),
                                1 * this.getDamageMultiplier()
                        );
                    }
                }

                // All entities are slowed, even the caster (except those immune to frost effects)
                if (!this.level().isClientSide()
                        && !EntityUtils.isEntityImmune(WizardryDamageTypes.FROST.apply(this.registryAccess()), target)
                )
                    target.addEffect(new MobEffectInstance(
                            WizardryMobEffects.FROST,
                            20,
                            0
                    ));
            }

        } else {
            ClientLevel clientLevel = (ClientLevel) this.level();
            for (int i = 0; i < 6; i++) {
                double speed = (this.getRandom().nextBoolean() ? 1 : -1) * (0.1 + 0.05 * this.getRandom().nextDouble());
                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        WizardryParticles.SNOW.get(),
                        this.getX(), this.getY() + this.getRandom().nextDouble() * this.getBbHeight(), this.getZ()
                ).ifPresent(p -> p.speed(0, 0, 0)
                        .time(100)
                        .scaleValue(2)
                        .spin(this.getRandom().nextDouble() * (radius - 0.5) + 0.5, speed)
                        .shaded(true)
                        .spawn()
                );
            }

            for (int i = 0; i < 3; i++) {
                double speed = (this.getRandom().nextBoolean() ? 1 : -1) * (0.05 + 0.02 * this.getRandom().nextDouble());
                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        WizardryParticles.CLOUD.get(),
                        this.getX(), this.getY() + this.getRandom().nextDouble() * (this.getBbHeight() - 0.5), this.getZ()
                ).ifPresent(p -> p.speed(0, 0, 0)
                        .color(0xffffff)
                        .shaded(true)
                        .spin(this.getRandom().nextDouble() * (radius - 1) + 0.5, speed)
                        .spawn()
                );
            }
        }
    }
}
