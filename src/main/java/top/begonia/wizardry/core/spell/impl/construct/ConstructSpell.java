package top.begonia.wizardry.core.spell.impl.construct;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.data.constant.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.construct.MagicConstructEntity;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.BlockUtils;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ConstructSpell<T extends MagicConstructEntity> extends AbstractSpell {
    protected final Function<Level, T> constructFactory;
    protected final boolean permanent;
    protected boolean requiresFloor = false;
    protected boolean allowOverlap = false;

    public ConstructSpell(Identifier identifier, ItemUseAnimation action, Function<Level, T> constructFactory, boolean permanent) {
        super(identifier, action, false);
        this.constructFactory = constructFactory;
        this.permanent = permanent;
        this.npcSelector((e, o) -> true);
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    public boolean canBeCastBy(DispenserBlockEntity dispenser) {
        return true;
    }

    @Override
    public boolean cast(Level level, @NonNull Player caster, InteractionHand hand, int ticksInUse, SpellContext context) {
        if (caster.onGround() || !requiresFloor) {
            if (!spawnConstruct(level, caster.getX(), caster.getY(), caster.getZ(), caster.onGround() ? Direction.UP : null, caster, context)) {
                return false;
            }
            this.playSound(level, caster, ticksInUse, -1, context);
            return true;
        }

        return false;
    }

    @Override
    public boolean cast(Level level, Mob caster, InteractionHand hand, int ticksInUse, LivingEntity target, SpellContext context) {

        if (target != null) {
            if (caster.onGround() || !requiresFloor) {
                if (!spawnConstruct(level, caster.getX(), caster.getY(), caster.getZ(), caster.onGround() ? Direction.UP : null,
                        caster, context)) return false;
                this.playSound(level, caster, ticksInUse, -1, context);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean cast(Level level, double x, double y, double z, Direction direction, int ticksInUse, int duration, SpellContext context) {

        Integer floor = (int) y;

        if (requiresFloor) {
            floor = BlockUtils.getNearestFloor(level, new BlockPos((int) x, (int) y, (int) z), 1);
            direction = Direction.UP;
        }

        if (floor != null) {
            if (!spawnConstruct(level, x, floor, z, direction, null, context)) {
                return false;
            }
            this.playSound(level, x - direction.getStepX(), y - direction.getStepY(), z - direction.getStepZ(), ticksInUse, duration, context);
            return true;
        }

        return false;
    }

    protected boolean spawnConstruct(@NonNull Level level, double x, double y, double z, @Nullable Direction side, @Nullable LivingEntity caster, SpellContext context) {

        if (!level.isClientSide()) {
            T construct = constructFactory.apply(level);
            construct.setPos(x, y, z);
            construct.setOwner(caster);
            construct.setLifetime(permanent ? -1 : (int) ((int) this.getBaseProperty(DURATION) * context.getWandUpgrade(WizardryItems.DURATION_UPGRADE.get())));
            construct.setDamageMultiplier(context.potency());
            if (construct instanceof ScaledConstructEntity scaledConstructEntity) {
                scaledConstructEntity.setDamageMultiplier(context.getWandUpgrade(WizardryItems.BLAST_UPGRADE.get()));
            }
            addConstructExtras(construct, side, caster, context);
            if (!allowOverlap && !level.getEntitiesOfClass(construct.getClass(), construct.getBoundingBox()).isEmpty()) {
                return false;
            }
            level.addFreshEntity(construct);
        }

        return true;
    }

    protected void addConstructExtras(T construct, Direction side, @Nullable LivingEntity caster, SpellContext context) {
    }

    public ConstructSpell<T> floor(boolean requiresFloor) {
        this.requiresFloor = requiresFloor;
        return this;
    }

    public ConstructSpell<T> overlap(boolean allowOverlap) {
        this.allowOverlap = allowOverlap;
        return this;
    }
}
