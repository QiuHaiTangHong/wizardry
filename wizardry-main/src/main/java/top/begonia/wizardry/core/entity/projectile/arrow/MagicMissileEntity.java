package top.begonia.wizardry.core.entity.projectile.arrow;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.particle.extension.builder.ParticleBuilder;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.entity.projectile.MagicArrowEntity;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.registry.WizardrySounds;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

public class MagicMissileEntity extends MagicArrowEntity {
    public MagicMissileEntity(EntityType<? extends MagicArrowEntity> type, Level level) {
        super(type, level);
    }

    public MagicMissileEntity(Level level) {
        this(WizardryEntities.MAGIC_MISSILE.get(), level);
    }

    @Override
    public double getDamage() {
        return WizardrySpells.MAGIC_MISSILE.get().getBaseProperty(AbstractSpell.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return 12;
    }

    @Override
    public boolean doGravity() {
        return false;
    }

    @Override
    public boolean doDeceleration() {
        return false;
    }

    @Override
    protected void onHitEntityHurtAfter(LivingEntity entity) {
        this.playSound(
                WizardrySounds.ENTITY_MAGIC_MISSILE_HIT.get(),
                1.0F,
                1.2F / (this.random.nextFloat() * 0.2F + 0.9F)
        );
        if (this.level() instanceof ClientLevel clientLevel) {
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    new QuadParticleOptions(WizardryParticles.FLASH.get()),
                    this.getX(), this.getY(), this.getZ()
            ).ifPresent(p -> p.color(1.0f, 1.0f, 0.65f)
                    .spawn()
            );
        }
    }

    @Override
    protected void onHitBlockAfter(BlockHitResult hitResult) {
        if (this.level() instanceof ClientLevel clientLevel) {
            Vec3 vec = hitResult.getLocation().add(new Vec3(hitResult.getDirection().getUnitVec3f()).scale(0.15));
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    new QuadParticleOptions(WizardryParticles.FLASH.get()),
                    vec.x, vec.y, vec.z
            ).ifPresent(p -> p.color(1.0f, 1.0f, 0.65f)
                    .endColor(0.85f, 0.5f, 0.8f)
                    .spawn()
            );
        }
    }

    @Override
    public void tickInAir() {

        if (this.level() instanceof ClientLevel clientLevel) {

            if (Wizardry.tisTheSeason) {

                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        this.random,
                        new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                        this.getX(), this.getY(), this.getZ(),
                        0.03,
                        true
                ).ifPresent(p -> p.color(0.8f, 0.15f, 0.15f)
                        .time(20 + this.random.nextInt(10))
                        .spawn()
                );

                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        new QuadParticleOptions(WizardryParticles.SNOW.get()),
                        this.getX(), this.getY(), this.getZ()
                ).ifPresent(ParticleBuilder::spawn);

                if (this.tickCount > 1) {
                    Vec3 motion = this.getDeltaMovement();
                    double x = this.getX() - motion.x / 2;
                    double y = this.getY() - motion.y / 2;
                    double z = this.getZ() - motion.z / 2;
                    WizardryClient.particleManager.getParticle(
                            clientLevel,
                            this.random,
                            new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                            x, y, z,
                            0.03,
                            true
                    ).ifPresent(p -> p.color(0.15f, 0.7f, 0.15f)
                            .time(20 + this.random.nextInt(10))
                            .spawn()
                    );
                }

            } else {

                WizardryClient.particleManager.getParticle(
                        clientLevel,
                        this.random,
                        new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                        this.getX(), this.getY(), this.getZ(),
                        0.03,
                        true
                ).ifPresent(p -> p.color(1.0f, 1.0f, 0.65f)
                        .endColor(0.7f, 0.0f, 1.0f)
                        .time(20 + this.random.nextInt(10))
                        .spawn()
                );

                if (this.tickCount > 1) {
                    Vec3 motion = this.getDeltaMovement();
                    double x = this.getX() - motion.x / 2;
                    double y = this.getY() - motion.y / 2;
                    double z = this.getZ() - motion.z / 2;
                    WizardryClient.particleManager.getParticle(
                            clientLevel,
                            this.random,
                            new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                            x, y, z,
                            0.03,
                            true
                    ).ifPresent(p -> p.color(1.0f, 1.0f, 0.65f)
                            .endColor(0.7f, 0.0f, 1.0f)
                            .time(20 + this.random.nextInt(10))
                            .spawn()
                    );
                }
            }
        }
    }
}
