package top.begonia.wizardry.core.spell.impl.ray.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.util.GeometryUtils;
import top.begonia.wizardry.client.util.ParticleBuilder;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.spell.impl.ray.AbstractRaySpell;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class CelestialSmiteRaySpell extends AbstractRaySpell {
    public CelestialSmiteRaySpell(Identifier identifier) {
        super(identifier, ItemUseAnimation.NONE, false);
        this.ignoreLivingEntities(true);
    }

    @Override
    protected boolean onEntityHit(Level level, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse, SpellContext context) {
        return false;
    }

    @Override
    protected boolean onBlockHit(Level level, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse, SpellContext context) {
        if (level instanceof ServerLevel serverLevel) {
            double radius = this.getBaseProperty(EFFECT_RADIUS) * context.getWandUpgrade(WizardryItems.BLAST_UPGRADE.get());
            List<LivingEntity> targets = EntityUtils.getLivingWithinRadius(radius, hit.x, hit.y, hit.z, level);
            DamageSource source = caster == null ? serverLevel.damageSources().magic() : WizardryDamageSource.causeDirectMagicDamage(WizardryDamageTypes.RADIANT.apply(serverLevel.registryAccess()), caster, false);
            float damage = this.getBaseProperty(DAMAGE) * context.potency();
            for (LivingEntity target : targets) {
                EntityUtils.attackEntityWithoutKnockback(serverLevel, target, source, damage);
                target.setRemainingFireTicks((int) this.getBaseProperty(BURN_DURATION));
            }
        } else {
            ParticleBuilder.create(WizardryParticles.BEAM.get()).pos(hit.x, level.getHeight(), hit.z).target(hit).scale(8)
                    .clr(0xffbf00).time(10).spawn(level);
            ParticleBuilder.create(WizardryParticles.SPHERE.get()).pos(hit).scale(4).clr(0xfff098).spawn(level);

            if (side == Direction.UP) {
                Vec3 vec = hit.add(new Vec3(side.getUnitVec3f()).scale(GeometryUtils.ANTI_Z_FIGHTING_OFFSET));
                ParticleBuilder.create(WizardryParticles.SCORCH.get()).pos(vec).face(side).scale(3).spawn(level);
            }
        }

        return true;
    }

    @Override
    protected boolean onMiss(Level level, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse, SpellContext context) {
        return false;
    }
}
