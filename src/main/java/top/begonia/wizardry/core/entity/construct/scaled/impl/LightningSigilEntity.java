package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import top.begonia.wizardry.api.particle.extension.builder.ParticleBuilder;
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

public class LightningSigilEntity extends ScaledConstructEntity {
    public static final String SECONDARY_RANGE = "secondary_range";
    public static final String SECONDARY_MAX_TARGETS = "secondary_max_targets";

    public LightningSigilEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setSizeMultiplier(WizardrySpells.LIGHTNING_SIGIL.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS));
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        super.tick();

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.tickCount > 600 && this.getOwner() == null) {
                this.discard();
            }

            List<LivingEntity> targets = EntityUtils.getLivingWithinCylinder(
                    this.getBbWidth() / 2,
                    this.position(),
                    this.getBbHeight(),
                    this.level()
            );

            for (LivingEntity target : targets) {

                if (this.isValidTarget(target)) {

                    Vec3 velocity = target.getDeltaMovement();
                    double velX = velocity.x;
                    double velY = velocity.y;
                    double velZ = velocity.z;

                    // Only works if target is actually damaged to account for hurtResistantTime
                    if (target.hurtServer(
                            serverLevel,
                            this.getOwner() != null
                                    ? WizardryDamageSource.causeIndirectMagicDamage(
                                    WizardryDamageTypes.SHOCK.apply(this.registryAccess()),
                                    this,
                                    this.getOwner(),
                                    false)
                                    : new DamageSources(this.registryAccess()).magic(),
                            WizardrySpells.LIGHTNING_SIGIL.get().getBaseProperty(AbstractSpell.DIRECT_DAMAGE) * this.getDamageMultiplier()
                    )) {

                        // Removes knockback
                        target.setDeltaMovement(velX, velY, velZ);

                        this.playSound(WizardrySounds.ENTITY_LIGHTNING_SIGIL_TRIGGER.get(), 1.0f, 1.0f);

                        // Secondary chaining effect
                        double seekerRange = WizardrySpells.LIGHTNING_SIGIL.get().getBaseProperty(SECONDARY_RANGE);

                        List<LivingEntity> secondaryTargets = EntityUtils.getLivingWithinRadius(
                                seekerRange,
                                target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                                this.level()
                        );

                        for (int j = 0; j < Math.min(secondaryTargets.size(),
                                WizardrySpells.LIGHTNING_SIGIL.get().getBaseProperty(SECONDARY_MAX_TARGETS)); j++) {

                            LivingEntity secondaryTarget = secondaryTargets.get(j);

                            if (secondaryTarget != target && this.isValidTarget(secondaryTarget)) {

                                if (this.level().isClientSide()) {
                                    ClientLevel clientLevel = (ClientLevel) this.level();
                                    WizardryClient.particleManager.getParticle(
                                            clientLevel,
                                            WizardryParticles.SPARK.get(),
                                            0, target.getBbHeight() / 2, 0
                                    ).ifPresent(p -> p.linkEntity(target)
                                            .targetEntity(secondaryTarget)
                                            .spawn()
                                    );
                                    WizardryClient.particleManager.spawnShockParticles(
                                            clientLevel,
                                            secondaryTarget.getX(),
                                            secondaryTarget.getY() + secondaryTarget.getBbHeight() / 2,
                                            secondaryTarget.getZ()
                                    );
                                }

                                secondaryTarget.playSound(
                                        WizardrySounds.ENTITY_LIGHTNING_SIGIL_TRIGGER.get(),
                                        1.0F,
                                        this.random.nextFloat() * 0.4F + 1.5F
                                );

                                secondaryTarget.hurtServer(
                                        serverLevel,
                                        WizardryDamageSource.causeIndirectMagicDamage(
                                                WizardryDamageTypes.SHOCK.apply(this.registryAccess()),
                                                this,
                                                this.getOwner(),
                                                false
                                        ),
                                        WizardrySpells.LIGHTNING_SIGIL.get().getBaseProperty(AbstractSpell.SPLASH_DAMAGE) * this.getDamageMultiplier()
                                );
                            }

                        }
                        // The trap is destroyed once triggered.
                        this.discard();
                    }
                }
            }
        }

        if (this.level().isClientSide() && this.random.nextInt(15) == 0) {
            double radius = (0.5 + this.random.nextDouble() * 0.3) * this.getBbWidth() / 2;
            float angle = this.random.nextFloat() * (float) Math.PI * 2;
            ClientLevel clientLevel = (ClientLevel) this.level();
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    WizardryParticles.SPARK.get(),
                    this.getX() + radius * Mth.cos(angle), this.getY() + 0.1, this.getZ() + radius * Mth.sin(angle)
            ).ifPresent(ParticleBuilder::spawn);
        }
    }
}
