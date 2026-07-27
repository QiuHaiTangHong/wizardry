package top.begonia.wizardry.core.spell.impl.ray.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.client.util.ParticleBuilder;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.construct.BubbleEntity;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.spell.impl.ray.AbstractRaySpell;

public class BubbleRaySpell extends AbstractRaySpell {
    public BubbleRaySpell(Identifier identifier) {
        super(identifier, ItemUseAnimation.NONE, false);
        this.soundValues(0.5f, 1.1f, 0.2f);
    }

    @Override
    protected boolean onEntityHit(@NonNull Level level, Entity target, Vec3 hit, @Nullable LivingEntity owner, Vec3 origin, int ticksInUse, SpellContext context) {
        if (level instanceof ServerLevel serverLevel && owner != null) {
            target.hurtServer(
                    serverLevel,
                    WizardryDamageSource.causeDirectMagicDamage(
                            WizardryDamageTypes.MAGIC.apply(serverLevel.registryAccess()),
                            owner,
                            false
                    ),
                    1
            );
            BubbleEntity bubble = new BubbleEntity(level);
            bubble.setPos(target.getX(), target.getY(), target.getZ());
            bubble.setOwner(owner);
            bubble.setLifetime((int) (this.getBaseProperty(DURATION) * context.getWandUpgrade(WizardryItems.DURATION_UPGRADE.get())));
            bubble.setDarkOrb(false);
            bubble.setDamageMultiplier(context.potency());
            level.addFreshEntity(bubble);
            target.startRiding(bubble);
        }

        return true;
    }

    @Override
    protected boolean onBlockHit(Level level, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse, SpellContext context) {
        return false;
    }

    @Override
    protected boolean onMiss(Level level, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse, SpellContext context) {
        return true;
    }

    @Override
    protected void spawnParticle(@NonNull Level level, double x, double y, double z, double vx, double vy, double vz) {
        level.addParticle(ParticleTypes.SPLASH, x, y, z, 0, 0, 0);
        ParticleBuilder.create(WizardryParticles.MAGIC_BUBBLE.get()).pos(x, y, z).spawn(level);
    }
}
