package top.begonia.wizardry.core.entity.projectile.bomb;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.util.ParticleBuilder;
import top.begonia.wizardry.core.damage.WizardryDamageSource;
import top.begonia.wizardry.core.damage.WizardryDamageType;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import java.util.List;

public class FireBombEntity extends BombEntity {

    public FireBombEntity(EntityType<? extends BombEntity> type, Level level) {
        super(type, level);
    }

    public FireBombEntity(EntityType<? extends BombEntity> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
    }

    public FireBombEntity(EntityType<? extends BombEntity> type, double x, double y, double z, Level level, ItemStack itemStack) {
        super(type, x, y, z, level, itemStack);
    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult hitResult) {
        if (!this.level().isClientSide()) {
            Entity hitEntity = hitResult.getEntity();
            float damage = WizardrySpells.FIRE_BOMB.get().getBaseProperty(AbstractSpell.DIRECT_DAMAGE) * damageMultiplier;
            hitEntity.hurtServer(
                    (ServerLevel) this.level(),
                    WizardryDamageSource.causeIndirectMagicDamage(this, this.getOwner(), WizardryDamageType.FIRE, false),
                    damage
            );
            if (!hitEntity.fireImmune()) {
                hitEntity.setRemainingFireTicks((int) WizardrySpells.FIRE_BOMB.get().getBaseProperty(AbstractSpell.BURN_DURATION));
            }
        }
    }

    @Override
    protected void onHitBlock(@NonNull BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (!this.level().isClientSide()) {
            this.playSound(WizardrySounds.ENTITY_FIREBOMB_SMASH.get(), 1.5F, this.random.nextFloat() * 0.4F + 0.6F);
            this.playSound(WizardrySounds.ENTITY_FIREBOMB_FIRE.get(), 1, 1);
            double range = WizardrySpells.FIRE_BOMB.get().getBaseProperty(AbstractSpell.BLAST_RADIUS) * blastMultiplier;
            List<LivingEntity> targets = EntityUtils.getLivingWithinRadius(range, this.getX(), this.getY(), this.getZ(), this.level());
            for (LivingEntity target : targets) {
                if (target != this.getOwner() && !target.fireImmune()) {
                    // Splash damage does not count as projectile damage
                    target.hurtServer(
                            (ServerLevel) this.level(),
                            WizardryDamageSource.causeIndirectMagicDamage(this, this.getOwner(), WizardryDamageType.FIRE, false),
                            WizardrySpells.FIRE_BOMB.get().getBaseProperty(AbstractSpell.SPLASH_DAMAGE) * damageMultiplier
                    );
                    target.setRemainingFireTicks((int) WizardrySpells.FIRE_BOMB.get().getBaseProperty(AbstractSpell.BURN_DURATION));
                }
            }
        }
    }

    @Override
    protected void createParticles(Level level) {
        Vec3 hitPos = this.position();
        ParticleBuilder.create(WizardryParticles.FLASH.get()).pos(hitPos).scale(5 * blastMultiplier).clr(1, 0.6f, 0)
                .spawn(level);
        for (int i = 0; i < 60 * this.blastMultiplier; i++) {
            ParticleBuilder.create(WizardryParticles.MAGIC_FIRE.get(), this.random, hitPos.x(), hitPos.y(), hitPos.z(), 2 * blastMultiplier, false)
                    .time(10 + this.random.nextInt(4)).scale(2 + this.random.nextFloat()).spawn(level);

            ParticleBuilder.create(WizardryParticles.DARK_MAGIC.get(), this.random, hitPos.x(), hitPos.y(), hitPos.z(), 2 * blastMultiplier, false)
                    .clr(1.0f, 0.2f + this.getRandom().nextFloat() * 0.4f, 0.0f).spawn(level);
        }

        this.level().addParticle(
                ParticleTypes.EXPLOSION,
                hitPos.x(), hitPos.y(), hitPos.z(),
                0, 0, 0
        );
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return WizardryItems.FIRE_BOMB.get();
    }
}
