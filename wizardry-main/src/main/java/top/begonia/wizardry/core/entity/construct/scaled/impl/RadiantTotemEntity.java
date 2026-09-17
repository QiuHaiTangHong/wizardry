package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.AllyDesignationSystem;
import top.begonia.wizardry.core.util.BlockUtils;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RadiantTotemEntity extends ScaledConstructEntity {
    private static final int PERIMETER_PARTICLE_DENSITY = 6;
    public static final String MAX_TARGETS = "max_targets";

    public RadiantTotemEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean shouldScaleWidth() {
        return false;
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        if (this.level().isClientSide() && this.tickCount == 1) {
            ClientHelper.playMovingSound(
                    this,
                    WizardrySounds.ENTITY_RADIANT_TOTEM_AMBIENT.get(),
                    WizardrySounds.SPELLS,
                    1, 1,
                    true
            );
        }

        super.tick();

        double radius = WizardrySpells.RADIANT_TOTEM.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS) * this.getSizeMultiplier();

        if (this.level().isClientSide()) {
            ClientLevel clientLevel = (ClientLevel) this.level();
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    this.random,
                    WizardryParticles.DUST.get(),
                    this.getX(), this.getY() + 0.2, this.getZ(),
                    0.3,
                    false
            ).ifPresent(p -> p.speed(0, -0.02 - this.random.nextFloat() * 0.01, 0)
                    .color(0xffffff)
                    .endColor(0xffec90)
                    .spawn()
            );

            for (int i = 0; i < PERIMETER_PARTICLE_DENSITY; i++) {

                float angle = ((float) Math.PI * 2) / PERIMETER_PARTICLE_DENSITY * (i + this.random.nextFloat());

                double x = this.getX() + radius * Mth.sin(angle);
                double z = this.getZ() + radius * Mth.cos(angle);

                Integer y = BlockUtils.getNearestSurface(
                        this.level(),
                        new BlockPos((int) x, (int) this.getY(), (int) z),
                        Direction.UP,
                        5,
                        true,
                        BlockUtils.SurfaceCriteria.COLLIDABLE
                );

                if (y != null) {
                    WizardryClient.particleManager.getParticle(
                            clientLevel,
                            WizardryParticles.DUST.get(),
                            x, y, z
                    ).ifPresent(p -> p.speed(0, 0.01, 0)
                            .color(0xffffff)
                            .endColor(0xffec90)
                            .spawn()
                    );
                }
            }
        }

        List<LivingEntity> nearby = EntityUtils.getLivingWithinRadius(
                radius,
                this.getX(), this.getY(), this.getZ(),
                this.level()
        );
        nearby.sort(Comparator.comparingDouble(e -> e.distanceToSqr(this)));

        List<LivingEntity> nearbyAllies = nearby.stream().filter(
                e -> e == this.getOwner()
                        || AllyDesignationSystem.isAllied(this.getOwner(), e)
        ).collect(Collectors.toList());
        nearby.removeAll(nearbyAllies);

        int targetsRemaining = (int) (WizardrySpells.RADIANT_TOTEM.get().getBaseProperty(MAX_TARGETS)
                + (int) ((this.getDamageMultiplier() - 1) / ServerConfig.Constants.potencyIncreasePerTier));

        while (!nearbyAllies.isEmpty() && targetsRemaining > 0) {

            LivingEntity ally = nearbyAllies.removeFirst();

            if (ally.getHealth() < ally.getMaxHealth()) {
                // Slightly slower than healing aura, and it only does 1 at a time (without potency modifiers)
                if (ally.tickCount % 8 == 0)
                    ally.heal(WizardrySpells.RADIANT_TOTEM.get().getBaseProperty(AbstractSpell.HEALTH));
                targetsRemaining--;

                if (this.level().isClientSide()) {
                    // TODO
//                    ParticleBuilder.create(Type.BEAM).pos(this.getPositionVector().add(0, height/2, 0))
//                            .target(ally).clr(1, 0.6f + 0.3f * world.rand.nextFloat(), 0.2f).spawn(world);
                }
            }
        }

        while (!nearby.isEmpty() && targetsRemaining > 0) {

            LivingEntity target = nearby.removeFirst();

            if (EntityUtils.isLiving(target) && isValidTarget(target)) {

                if (target.tickCount % target.invulnerableTime == 1 && this.level() instanceof ServerLevel serverLevel) {

                    float damage = WizardrySpells.RADIANT_TOTEM.get().getBaseProperty(AbstractSpell.DAMAGE);

                    EntityUtils.attackEntityWithoutKnockback(
                            serverLevel,
                            target,
                            WizardryDamageSource.causeIndirectMagicDamage(
                                    WizardryDamageTypes.RADIANT.apply(this.registryAccess()),
                                    this,
                                    this.getOwner(),
                                    false
                            ),
                            damage
                    );
                }

                targetsRemaining--;

                if (this.level().isClientSide()) {
                    // TODO
//                    ParticleBuilder.create(Type.BEAM).pos(this.getPositionVector().add(0, height/2, 0))
//                            .target(target).clr(1, 0.6f + 0.3f * world.rand.nextFloat(), 0.2f).spawn(world);
                }
            }
        }
    }

    @Override
    public void despawn() {
        this.playSound(WizardrySounds.ENTITY_RADIANT_TOTEM_VANISH.get(), 1, 1);
        super.despawn();
    }
}
