package top.begonia.wizardry.core.entity.projectile.arrow;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.api.particle.utils.ParticleBuilder;
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
            ParticleBuilder.create(
                            new QuadParticleOptions(WizardryParticles.FLASH.get())
                    )
                    .pos(this.getX(), this.getY(), this.getZ())
                    .clr(1, 1, 0.65f)
                    .spawn(clientLevel);
        }
    }

    @Override
    protected void onHitBlockAfter(BlockHitResult hitResult) {
        if (this.level() instanceof ClientLevel clientLevel) {
            Vec3 vec = hitResult.getLocation().add(new Vec3(hitResult.getDirection().getUnitVec3f()).scale(0.15));
            ParticleBuilder.create(
                            new QuadParticleOptions(WizardryParticles.FLASH.get())
                    )
                    .pos(vec)
                    .clr(1, 1, 0.65f)
                    .fade(0.85f, 0.5f, 0.8f)
                    .spawn(clientLevel);
        }
    }

    @Override
    public void tickInAir() {

        if (this.level() instanceof ClientLevel clientLevel) {

            if (Wizardry.tisTheSeason) {

                ParticleBuilder.create(
                                new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                                this.random,
                                this.getX(), this.getY(), this.getZ(),
                                0.03,
                                true
                        )
                        .clr(0.8f, 0.15f, 0.15f)
                        .time(20 + this.random.nextInt(10))
                        .spawn(clientLevel);

                ParticleBuilder.create(
                                new QuadParticleOptions(WizardryParticles.SNOW.get())
                        )
                        .pos(this.getX(), this.getY(), this.getZ())
                        .spawn(clientLevel);

                if (this.tickCount > 1) {
                    Vec3 motion = this.getDeltaMovement();
                    double x = this.getX() - motion.x / 2;
                    double y = this.getY() - motion.y / 2;
                    double z = this.getZ() - motion.z / 2;
                    ParticleBuilder.create(
                                    new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                                    this.random,
                                    x, y, z,
                                    0.03,
                                    true
                            )
                            .clr(0.15f, 0.7f, 0.15f)
                            .time(20 + this.random.nextInt(10))
                            .spawn(clientLevel);
                }

            } else {

                ParticleBuilder.create(
                                new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                                this.random,
                                this.getX(), this.getY(), this.getZ(),
                                0.03,
                                true
                        )
                        .clr(1, 1, 0.65f)
                        .fade(0.7f, 0, 1)
                        .time(20 + this.random.nextInt(10))
                        .spawn(clientLevel);

                if (this.tickCount > 1) {
                    Vec3 motion = this.getDeltaMovement();
                    double x = this.getX() - motion.x / 2;
                    double y = this.getY() - motion.y / 2;
                    double z = this.getZ() - motion.z / 2;
                    ParticleBuilder.create(
                                    new QuadParticleOptions(WizardryParticles.SPARKLE.get()),
                                    this.random,
                                    x, y, z,
                                    0.03,
                                    true
                            )
                            .clr(1, 1, 0.65f)
                            .fade(0.7f, 0, 1)
                            .time(20 + this.random.nextInt(10))
                            .spawn(clientLevel);
                }
            }
        }
    }
}
