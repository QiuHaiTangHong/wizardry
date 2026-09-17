package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class CombustionRuneEntity extends ScaledConstructEntity {
    public CombustionRuneEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean shouldScaleWidth() {
        return false; // We're using the blast modifier for an actual explosion here, rather than the entity size
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        super.tick();

        if (!this.level().isClientSide()) {

            List<LivingEntity> targets = EntityUtils.getLivingWithinRadius(
                    this.getBbWidth() / 2,
                    this.getX(), this.getY(), this.getZ(),
                    this.level()
            );

            for (LivingEntity target : targets) {

                if (this.isValidTarget(target)) {

                    float strength = WizardrySpells.COMBUSTION_RUNE.get().getBaseProperty(AbstractSpell.BLAST_RADIUS) * this.getSizeMultiplier();

                    ServerLevel serverLevel = (ServerLevel) this.level();
                    serverLevel.explode(
                            this.getOwner(),
                            this.getX(),
                            this.getY(),
                            this.getZ(),
                            strength,
                            true,
                            EntityUtils.canDamageBlocks(this.getOwner(), serverLevel)
                                    ? Level.ExplosionInteraction.BLOCK
                                    : Level.ExplosionInteraction.NONE
                    );

                    // The trap is destroyed once triggered.
                    this.discard();
                }
            }
        } else if (this.getRandom().nextInt(15) == 0) {
            double radius = 0.5 + this.getRandom().nextDouble() * 0.3;
            float angle = this.getRandom().nextFloat() * (float) Math.PI * 2;
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
