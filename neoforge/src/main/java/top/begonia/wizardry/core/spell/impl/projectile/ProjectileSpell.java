package top.begonia.wizardry.core.spell.impl.projectile;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.ISpellCaster;
import top.begonia.wizardry.core.entity.projectile.MagicProjectileEntity;
import top.begonia.wizardry.core.entity.projectile.QuadFunction;
import top.begonia.wizardry.core.entity.projectile.bomb.BombEntity;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ProjectileSpell<T extends MagicProjectileEntity> extends AbstractSpell {
    private static final float DISPENSER_INACCURACY = 1.0F;
    private static final float FALLBACK_VELOCITY = 1.5F;

    protected final Supplier<EntityType<T>> entityTypeSupplier;
    protected final Supplier<ItemStack> renderItemSupplier;
    protected final QuadFunction<EntityType<? extends T>, LivingEntity, Level, ItemStack, T> projectileFactory;

    public ProjectileSpell(
            Identifier identifier,
            Supplier<EntityType<T>> entityTypeSupplier,
            Supplier<ItemStack> renderItemSupplier,
            QuadFunction<EntityType<? extends T>, LivingEntity, Level, ItemStack, T> projectileFactory
    ) {
        super(identifier, ItemUseAnimation.NONE, false);
        this.entityTypeSupplier = entityTypeSupplier;
        this.renderItemSupplier = renderItemSupplier;
        this.projectileFactory = projectileFactory;
    }

    protected float calculateVelocity(@NonNull MagicProjectileEntity projectile, @NonNull SpellContext context, float launchHeight) {
        float range = this.getBaseProperty(RANGE) * context.getWandUpgrade(WizardryItems.RANGE_UPGRADE.get());
        if (projectile.isNoGravity()) {
            if (projectile.getLifetime() <= 0) {
                return FALLBACK_VELOCITY;
            }
            return range / projectile.getLifetime();
        } else {
            float g = 0.03f;
            return range / Mth.sqrt(2 * launchHeight / g);
        }
    }

    protected T createProjectile(EntityType<? extends T> type, @Nullable LivingEntity caster, Level level, ItemStack stack) {
        return this.projectileFactory.apply(type, caster, level, stack);
    }

    @Override
    public boolean cast(@NonNull Level level, @NonNull Player caster, InteractionHand hand, int ticksInUse, SpellContext context) {
        if (!level.isClientSide()) {
            T projectile = this.createProjectile(this.entityTypeSupplier.get(), caster, level, this.renderItemSupplier.get());
            projectile.aim(
                    caster,
                    calculateVelocity(
                            projectile,
                            context,
                            caster.getEyeHeight() - (float) MagicProjectileEntity.LAUNCH_Y_OFFSET
                    )
            );
            projectile.damageMultiplier = context.potency();
            if (projectile instanceof BombEntity bombEntity) {
                bombEntity.blastMultiplier = context.getWandUpgrade(WizardryItems.BLAST_UPGRADE.get());
            }
            addProjectileExtras(projectile, caster, context);
            level.addFreshEntity(projectile);
        }
        caster.swing(hand);
        this.playSound(level, caster, ticksInUse, -1, context);
        return true;
    }

    @Override
    public boolean cast(Level level, Mob caster, InteractionHand hand, int ticksInUse, LivingEntity target, SpellContext context) {
        if (target != null) {
            if (!level.isClientSide()) {
                T projectile = projectileFactory.apply(this.entityTypeSupplier.get(), caster, level, this.renderItemSupplier.get());
                int aimingError = caster instanceof ISpellCaster
                        ? ((ISpellCaster) caster).getAimingError(level.getDifficulty())
                        : EntityUtils.getDefaultAimingError(level.getDifficulty());
                projectile.aim(
                        caster,
                        target,
                        calculateVelocity(
                                projectile,
                                context,
                                caster.getEyeHeight() - (float) MagicProjectileEntity.LAUNCH_Y_OFFSET
                        ),
                        aimingError
                );
                projectile.damageMultiplier = context.potency();
                if (projectile instanceof BombEntity bombEntity) {
                    bombEntity.blastMultiplier = context.getWandUpgrade(WizardryItems.BLAST_UPGRADE.get());
                }
                addProjectileExtras(projectile, caster, context);
                level.addFreshEntity(projectile);
            }
            caster.swing(hand);
            this.playSound(level, caster, ticksInUse, -1, context);
            return true;
        }

        return false;
    }

    @Override
    public boolean cast(@NonNull Level level, double x, double y, double z, Direction direction, int ticksInUse, int duration, SpellContext context) {
        if (!level.isClientSide()) {
            T projectile = projectileFactory.apply(this.entityTypeSupplier.get(), null, level, this.renderItemSupplier.get());
            projectile.setPos(x, y, z);
            Vec3i vec = direction.getUnitVec3i();
            projectile.shoot(vec.getX(), vec.getY(), vec.getZ(), calculateVelocity(projectile, context,
                    0.375f), DISPENSER_INACCURACY);
            projectile.damageMultiplier = context.potency();
            if (projectile instanceof BombEntity bombEntity) {
                bombEntity.blastMultiplier = context.getWandUpgrade(WizardryItems.BLAST_UPGRADE.get());
            }
            addProjectileExtras(projectile, null, context);
            level.addFreshEntity(projectile);
        }
        this.playSound(level, x - direction.getStepX(), y - direction.getStepY(), z - direction.getStepZ(), ticksInUse, duration, context);

        return true;
    }

    protected void addProjectileExtras(T projectile, @Nullable LivingEntity caster, SpellContext context) {
    }
}
