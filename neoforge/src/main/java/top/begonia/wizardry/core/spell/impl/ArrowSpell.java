package top.begonia.wizardry.core.spell.impl;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.SpellActions;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.ISpellCaster;
import top.begonia.wizardry.core.entity.projectile.MagicArrowEntity;
import top.begonia.wizardry.core.entity.projectile.MagicProjectileEntity;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.EntityUtils;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ArrowSpell<T extends MagicArrowEntity> extends AbstractSpell {
    private static final float DISPENSER_INACCURACY = 1;
    private static final float FALLBACK_VELOCITY = 2;

    protected final Supplier<EntityType<T>> entityTypeSupplier;
    protected final BiFunction<EntityType<? extends T>, Level, T> arrowFactory;

    public ArrowSpell(
            Identifier identifier,
            Supplier<EntityType<T>> entityTypeSupplier,
            BiFunction<EntityType<? extends T>, Level, T> arrowFactory
    ) {
        super(identifier, SpellActions.POINT, false);
        this.npcSelector((e, o) -> true);
        this.entityTypeSupplier = entityTypeSupplier;
        this.arrowFactory = arrowFactory;
    }

    protected float calculateVelocity(@NonNull MagicArrowEntity projectile, @NonNull SpellContext context, float launchHeight) {
        float range = this.getBaseProperty(RANGE) * context.getWandUpgrade(WizardryItems.RANGE_UPGRADE.get());
        if (!projectile.doGravity()) {
            if (projectile.getLifetime() <= 0) {
                return FALLBACK_VELOCITY;
            }
            return range / projectile.getLifetime();
        } else {
            float g = 0.05f;
            return range / Mth.sqrt(2 * launchHeight / g);
        }
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    public boolean cast(@NonNull Level level, Player caster, InteractionHand hand, int ticksInUse, SpellContext context) {
        if (!level.isClientSide()) {
            T projectile = arrowFactory.apply(entityTypeSupplier.get(), level);
            projectile.aim(caster, this.calculateVelocity(projectile, context, caster.getEyeHeight()
                    - (float) MagicArrowEntity.LAUNCH_Y_OFFSET));
            projectile.damageMultiplier = context.potency();
            addArrowExtras(projectile, caster, context);
            level.addFreshEntity(projectile);
        }

        this.playSound(level, caster, ticksInUse, -1, context);

        return true;
    }

    @Override
    public boolean cast(Level level, Mob caster, InteractionHand hand, int ticksInUse, LivingEntity target, SpellContext context) {

        if (target != null) {

            if (!level.isClientSide()) {
                T projectile = arrowFactory.apply(entityTypeSupplier.get(), level);
                int aimingError = caster instanceof ISpellCaster spellCaster ? spellCaster.getAimingError(level.getDifficulty())
                        : EntityUtils.getDefaultAimingError(level.getDifficulty());
                projectile.aim(caster, target, calculateVelocity(projectile, context, caster.getEyeHeight()
                        - (float) MagicProjectileEntity.LAUNCH_Y_OFFSET), aimingError);
                projectile.damageMultiplier = context.potency();
                addArrowExtras(projectile, caster, context);
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
            T projectile = arrowFactory.apply(entityTypeSupplier.get(), level);
            projectile.setPos(x, y, z);
            Vec3i vec = direction.getUnitVec3i();
            projectile.shoot(vec.getX(), vec.getY(), vec.getZ(), calculateVelocity(projectile, context,
                    0.375f), DISPENSER_INACCURACY);
            projectile.damageMultiplier = context.potency();
            addArrowExtras(projectile, null, context);
            level.addFreshEntity(projectile);
        }
        this.playSound(level, x - direction.getStepX(), y - direction.getStepY(), z - direction.getStepZ(), ticksInUse, duration, context);

        return true;
    }

    protected void addArrowExtras(T arrow, @Nullable LivingEntity caster, SpellContext context) {
    }
}
