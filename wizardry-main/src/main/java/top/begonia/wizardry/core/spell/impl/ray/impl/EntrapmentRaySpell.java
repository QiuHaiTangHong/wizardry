package top.begonia.wizardry.core.spell.impl.ray.impl;

import net.minecraft.client.multiplayer.ClientLevel;
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
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.data.constant.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.construct.BubbleEntity;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryParticles;
import top.begonia.wizardry.core.spell.impl.ray.AbstractRaySpell;

public class EntrapmentRaySpell extends AbstractRaySpell {

    public static final String DAMAGE_INTERVAL = "damage_interval";

    public EntrapmentRaySpell(Identifier identifier) {
        super(identifier, ItemUseAnimation.NONE, false);
        this.soundValues(1, 0.85f, 0.3f);
    }

    @Override
    protected boolean onEntityHit(Level level, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse, SpellContext context) {
        if (level instanceof ServerLevel serverLevel && caster != null) {
            target.hurtServer(
                    serverLevel,
                    WizardryDamageSource.causeDirectMagicDamage(
                            WizardryDamageTypes.MAGIC.apply(serverLevel.registryAccess()),
                            caster,
                            false
                    ),
                    1
            );

            BubbleEntity bubble = new BubbleEntity(level);
            bubble.setPos(target.getX(), target.getY(), target.getZ());
            bubble.setOwner(caster);
            bubble.setLifetime((int) (this.getBaseProperty(EFFECT_DURATION) * context.getWandUpgrade(WizardryItems.DURATION_UPGRADE.get())));
            bubble.setDarkOrb(true);
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
    protected void spawnParticle(Level level, double x, double y, double z, double vx, double vy, double vz) {
        if (level instanceof ClientLevel clientLevel) {
            clientLevel.addParticle(ParticleTypes.PORTAL, x, y - 0.5, z, 0, 0, 0);
            WizardryClient.particleManager.getParticle(
                    clientLevel,
                    new QuadParticleOptions(WizardryParticles.DARK_MAGIC.get()),
                    x, y, z
            ).ifPresent(p -> p.color(0.1f, 0.0f, 0.0f)
                    .spawn()
            );
        }
    }
}
