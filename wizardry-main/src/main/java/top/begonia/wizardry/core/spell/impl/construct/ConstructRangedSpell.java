package top.begonia.wizardry.core.spell.impl.construct;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.data.constant.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.construct.MagicConstructEntity;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.BlockUtils;
import top.begonia.wizardry.core.util.RayTracer;

import java.util.function.Function;

public class ConstructRangedSpell<T extends MagicConstructEntity> extends ConstructSpell<T> {
    protected boolean hitLiquids = false;
    protected boolean ignoreUncountables = false;

    public ConstructRangedSpell(Identifier identifier, Function<Level, T> constructFactory, boolean permanent) {
        super(identifier, ItemUseAnimation.NONE, constructFactory, permanent);
    }

    public AbstractSpell hitLiquids(boolean hitLiquids) {
        this.hitLiquids = hitLiquids;
        return this;
    }

    public AbstractSpell ignoreUncountables(boolean ignoreUncountables) {
        this.ignoreUncountables = ignoreUncountables;
        return this;
    }

    @Override
    public boolean cast(Level level, @NonNull Player caster, InteractionHand hand, int ticksInUse, @NonNull SpellContext context) {

        double range = this.getBaseProperty(RANGE) * context.getWandUpgrade(WizardryItems.RANGE_UPGRADE.get());
        HitResult rayTrace = RayTracer.standardBlockRayTrace(level, caster, range, hitLiquids, ignoreUncountables, false);

        if (rayTrace instanceof BlockHitResult blockHitResult && (blockHitResult.getDirection() == Direction.UP || !requiresFloor)) {

            if (!level.isClientSide()) {

                double x = rayTrace.getLocation().x;
                double y = rayTrace.getLocation().y;
                double z = rayTrace.getLocation().z;

                if (!spawnConstruct(level, x, y, z, blockHitResult.getDirection(), caster, context)) {
                    return false;
                }
            }

        } else if (!requiresFloor) {

            if (!level.isClientSide()) {

                Vec3 look = caster.getLookAngle();

                double x = caster.getX() + look.x * range;
                double y = caster.getY() + caster.getEyeHeight() + look.y * range;
                double z = caster.getZ() + look.z * range;

                if (!spawnConstruct(level, x, y, z, null, caster, context)) {
                    return false;
                }
            }

        } else {
            return false;
        }

        this.playSound(level, caster, ticksInUse, -1, context);
        return true;
    }

    @Override
    public boolean cast(Level level, @NonNull Mob caster, InteractionHand hand, int ticksInUse, LivingEntity target, @NonNull SpellContext context) {

        double range = this.getBaseProperty(RANGE) * context.getWandUpgrade(WizardryItems.RANGE_UPGRADE.get());
        Vec3 origin = caster.getEyePosition(1.0F);

        if (target != null && caster.distanceTo(target) <= range) {

            if (!level.isClientSide()) {
                double x = target.getX();
                double y = target.getY();
                double z = target.getZ();
                ClipContext clipContext = new ClipContext(
                        origin, new Vec3(x, y, z),
                        ignoreUncountables ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE,
                        hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
                        caster
                );
                BlockHitResult hit = level.clip(clipContext);
                if (hit.getType() == HitResult.Type.BLOCK && !hit.getBlockPos().equals(new BlockPos((int) x, (int) y, (int) z))) {
                    return false;
                }
                Direction side = null;
                if (!target.onGround() && requiresFloor) {
                    Integer floor = BlockUtils.getNearestFloor(level, new BlockPos((int) x, (int) y, (int) z), 3);
                    if (floor == null) return false;
                    y = floor;
                    side = Direction.UP;
                }

                if (!spawnConstruct(level, x, y, z, side, caster, context)) return false;
            }

            caster.swing(hand);
            this.playSound(level, caster, ticksInUse, -1, context);
            return true;
        }

        return false;
    }

    @Override
    public boolean cast(@NonNull Level level, double x, double y, double z, @NonNull Direction direction, int ticksInUse, int duration, @NonNull SpellContext context) {

        double range = this.getBaseProperty(RANGE) * context.getWandUpgrade(WizardryItems.RANGE_UPGRADE.get());
        Vec3 origin = new Vec3(x, y, z);
        Vec3 endpoint = origin.add(direction.getUnitVec3().scale(range));
        ClipContext clipContext = new ClipContext(
                origin, endpoint,
                ignoreUncountables ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE,
                hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
                (Entity) null
        );
        BlockHitResult rayTrace = level.clip(clipContext);
        if (rayTrace.getType() == HitResult.Type.BLOCK && (rayTrace.getDirection() == Direction.UP || !requiresFloor)) {

            if (!level.isClientSide()) {
                double x1 = rayTrace.getLocation().x;
                double y1 = rayTrace.getLocation().y;
                double z1 = rayTrace.getLocation().z;
                if (!spawnConstruct(level, x1, y1, z1, rayTrace.getDirection(), null, context)) {
                    return false;
                }
            }
        } else if (!requiresFloor) {
            if (!level.isClientSide()) {
                if (!spawnConstruct(level, endpoint.x, endpoint.y, endpoint.z, null, null, context)) return false;
            }

        } else {
            return false;
        }
        this.playSound(
                level,
                x - direction.getStepX(),
                y - direction.getStepY(),
                z - direction.getStepZ(),
                ticksInUse,
                duration,
                context
        );
        return true;
    }
}
