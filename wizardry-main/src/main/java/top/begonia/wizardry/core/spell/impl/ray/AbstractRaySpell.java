package top.begonia.wizardry.core.spell.impl.ray;

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
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.util.ClientHelper;
import top.begonia.wizardry.core.data.constant.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.RayTracer;

import javax.annotation.Nullable;

public abstract class AbstractRaySpell extends AbstractSpell {
    public static final double Y_OFFSET = 0.25;
    protected double particleSpacing = 0.85;
    protected double particleJitter = 0.1;
    protected double particleVelocity = 0;
    protected boolean ignoreLivingEntities = false;
    protected boolean hitLiquids = false;
    protected boolean ignoreUncollidables = true;
    protected float aimAssist = 0;

    public AbstractRaySpell(Identifier identifier, ItemUseAnimation action, boolean isContinuous) {
        super(identifier, action, isContinuous);
    }

    public AbstractSpell particleSpacing(double particleSpacing) {
        this.particleSpacing = particleSpacing;
        return this;
    }

    public AbstractSpell particleJitter(double particleJitter) {
        this.particleJitter = particleJitter;
        return this;
    }

    public AbstractSpell particleVelocity(double particleVelocity) {
        this.particleVelocity = particleVelocity;
        return this;
    }

    public AbstractSpell ignoreLivingEntities(boolean ignoreLivingEntities) {
        this.ignoreLivingEntities = ignoreLivingEntities;
        return this;
    }

    public AbstractSpell hitLiquids(boolean hitLiquids) {
        this.hitLiquids = hitLiquids;
        return this;
    }

    public AbstractSpell ignoreUncollidables(boolean ignoreUncollidables) {
        this.ignoreUncollidables = ignoreUncollidables;
        return this;
    }

    public AbstractSpell aimAssist(float aimAssist) {
        this.aimAssist = aimAssist;
        return this;
    }

    @Override
    public boolean canBeCastBy(DispenserBlockEntity dispenser) {
        return true;
    }

    @Override
    public boolean cast(Level level, @NonNull Player caster, InteractionHand hand, int ticksInUse, SpellContext context) {
        Vec3 look = caster.getViewVector(1.0F);
        Vec3 origin = new Vec3(
                caster.getX(),
                caster.getY() + caster.getEyeHeight() - Y_OFFSET,
                caster.getZ()
        );
        if (!this.isContinuous && level.isClientSide() && !ClientHelper.isFirstPerson(caster)) {
            origin = origin.add(look.scale(1.2));
        }

        if (!shootSpell(level, origin, look, caster, ticksInUse, context)) {
            return false;
        }

        if (casterSwingsArm(level, caster, hand, ticksInUse, context)) {
            caster.swing(hand);
        }
        this.playSound(level, caster, ticksInUse, -1, context);
        return true;
    }

    @Override
    public boolean cast(Level level, @NonNull Mob caster, InteractionHand hand, int ticksInUse, LivingEntity target, SpellContext context) {
        // IDEA: Add in an aiming error and trigger onMiss accordingly
        Vec3 origin = new Vec3(
                caster.getX(),
                caster.getY() + caster.getEyeHeight() - Y_OFFSET,
                caster.getZ()
        );
        Vec3 targetPos = null;
        if (target != null) {
            if (!ignoreLivingEntities) {
                targetPos = target.getBoundingBox().getCenter();
            } else {

                BlockPos pos = BlockPos.containing(
                        target.getX(),
                        target.getY() - 1,
                        target.getZ()
                );
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && (!state.liquid() || hitLiquids)) {
                    targetPos = Vec3.atCenterOf(pos.above());
                }
            }
        }

        if (targetPos == null) {
            return false;
        }

        if (!shootSpell(level, origin, targetPos.subtract(origin).normalize(), caster, ticksInUse, context)) {
            return false;
        }

        if (casterSwingsArm(level, caster, hand, ticksInUse, context)) {
            caster.swing(hand);
        }
        this.playSound(level, caster, ticksInUse, -1, context);
        return true;
    }

    @Override
    public boolean cast(Level level, double x, double y, double z, @NonNull Direction direction, int ticksInUse, int duration, SpellContext context) {
        Vec3 vec = Vec3.atLowerCornerOf(direction.getUnitVec3i());
        Vec3 origin = new Vec3(x, y, z);
        if (!shootSpell(level, origin, vec, null, ticksInUse, context)) {
            return false;
        }
        this.playSound(level, x - direction.getStepX(), y - direction.getStepY(), z - direction.getStepZ(), ticksInUse, duration, context);
        return true;
    }

    protected double getRange(Level world, Vec3 origin, Vec3 direction, @Nullable LivingEntity caster, int ticksInUse, @NonNull SpellContext context) {
        return this.getBaseProperty(RANGE) * context.getWandUpgrade(WizardryItems.RANGE_UPGRADE.get());
    }

    protected boolean casterSwingsArm(Level level, LivingEntity caster, InteractionHand hand, int ticksInUse, SpellContext context) {
        return !this.isContinuous && this.action == ItemUseAnimation.NONE;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean shootSpell(Level level, Vec3 origin, Vec3 direction, @Nullable LivingEntity caster, int ticksInUse, SpellContext context) {
        double range = getRange(level, origin, direction, caster, ticksInUse, context);
        Vec3 endpoint = origin.add(direction.scale(range));

        HitResult rayTrace = RayTracer.rayTrace(
                level,
                caster,
                origin,
                endpoint,
                aimAssist,
                ignoreUncollidables ? ClipContext.Block.COLLIDER : ClipContext.Block.OUTLINE,
                hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
                Entity.class,
                ignoreLivingEntities
                        ? e -> e instanceof LivingEntity
                        : RayTracer.ignoreEntityFilter(caster)
        );

        boolean flag = false;
        if (rayTrace != null) {
            if (rayTrace instanceof EntityHitResult entityHit) {
                flag = onEntityHit(level, entityHit.getEntity(), entityHit.getLocation(), caster, origin, ticksInUse, context);
                if (flag) {
                    range = origin.distanceTo(entityHit.getLocation());
                }

            } else if (rayTrace instanceof BlockHitResult blockHit) {
                flag = onBlockHit(level, blockHit.getBlockPos(), blockHit.getDirection(), blockHit.getLocation(), caster, origin, ticksInUse, context);
                range = origin.distanceTo(blockHit.getLocation());
            }
        }

        if (!flag && !onMiss(level, caster, origin, direction, ticksInUse, context)) return false;

        if (level.isClientSide()) {
            spawnParticleRay(level, origin, direction, caster, range);
        }

        return true;
    }

    protected abstract boolean onEntityHit(Level level, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse, SpellContext context);

    protected abstract boolean onBlockHit(Level level, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse, SpellContext context);

    protected abstract boolean onMiss(Level level, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse, SpellContext context);

    protected void spawnParticleRay(Level level, Vec3 origin, @NonNull Vec3 direction, @Nullable LivingEntity caster, double distance) {

        Vec3 velocity = direction.scale(particleVelocity);

        for (double d = particleSpacing; d <= distance; d += particleSpacing) {
            double x = origin.x + d * direction.x + particleJitter * (level.getRandom().nextDouble() * 2 - 1);
            double y = origin.y + d * direction.y + particleJitter * (level.getRandom().nextDouble() * 2 - 1);
            double z = origin.z + d * direction.z + particleJitter * (level.getRandom().nextDouble() * 2 - 1);
            spawnParticle(level, x, y, z, velocity.x, velocity.y, velocity.z);
        }
    }

    protected void spawnParticle(Level level, double x, double y, double z, double vx, double vy, double vz) {
    }
}
