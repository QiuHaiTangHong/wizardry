package top.begonia.wizardry.core.spell.impl.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.living.ISummonedCreature;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.BlockUtils;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class MinionSpell<T extends LivingEntity & ISummonedCreature> extends AbstractSpell {
    public static final String MINION_LIFETIME = "minion_lifetime";
    public static final String MINION_COUNT = "minion_count";
    public static final String SUMMON_RADIUS = "summon_radius";
    public static final String HEALTH_MODIFIER = "minion_health";
    public static final Identifier POTENCY_ATTRIBUTE_MODIFIER = Identifier.fromNamespaceAndPath(Wizardry.MODID, "potency");
    protected final Function<Level, T> minionFactory;
    protected boolean flying = false;

    public MinionSpell(Identifier identifier, Function<Level, T> minionFactory) {
        super(identifier, ItemUseAnimation.NONE, false);
        this.minionFactory = minionFactory;
        this.npcSelector((e, o) -> true);
    }

    public MinionSpell<T> flying(boolean flying) {
        this.flying = flying;
        return this;
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
    public boolean cast(Level level, Player caster, InteractionHand hand, int ticksInUse, SpellContext context) {
        if (!this.spawnMinions(level, caster, context)) {
            return false;
        }
        this.playSound(level, caster, ticksInUse, -1, context);
        return true;
    }

    @Override
    public boolean cast(Level level, Mob caster, InteractionHand hand, int ticksInUse, LivingEntity target, SpellContext context) {
        if (!this.spawnMinions(level, caster, context)) {
            return false;
        }
        this.playSound(level, caster, ticksInUse, -1, context);
        return true;
    }

    @Override
    public boolean cast(@NonNull Level level, double x, double y, double z, Direction direction, int ticksInUse, int duration, SpellContext context) {
        BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
        if (!level.isClientSide()) {
            for (int i = 0; i < this.getBaseProperty(MINION_COUNT); i++) {
                T minion = minionFactory.apply(level);
                minion.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                minion.setLifetime((int) (this.getBaseProperty(MINION_LIFETIME) * context.getWandUpgrade(WizardryItems.DURATION_UPGRADE.get())));
                this.addMinionExtras(minion, pos, null, context, i);
                level.addFreshEntity(minion);
            }
        }
        this.playSound(level, x - direction.getStepX(), y - direction.getStepY(), z - direction.getStepZ(), ticksInUse, duration, context);

        return true;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean spawnMinions(@NonNull Level level, LivingEntity owner, SpellContext context) {
        if (!level.isClientSide()) {
            for (int i = 0; i < this.getBaseProperty(MINION_COUNT); i++) {
                int range = (int) this.getBaseProperty(SUMMON_RADIUS);
                BlockPos pos = BlockUtils.findNearbyFloorSpace(owner, range, range * 2);
                if (flying) {
                    if (pos != null) {
                        pos = pos.above(2);
                    } else {
                        pos = owner.blockPosition().north(level.getRandom().nextInt(range * 2) - range)
                                .east(level.getRandom().nextInt(range * 2) - range);
                    }
                } else {
                    if (pos == null) {
                        return false;
                    }
                }
                T minion = createMinion(level, owner, context);
                minion.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                minion.setOwner(owner);
                minion.setLifetime((int) (this.getBaseProperty(MINION_LIFETIME) * context.getWandUpgrade(WizardryItems.DURATION_UPGRADE.get())));
                Optional.ofNullable(minion.getAttribute(Attributes.ATTACK_DAMAGE))
                        .ifPresent(attributeInstance -> attributeInstance.addTransientModifier(
                                new AttributeModifier(
                                        POTENCY_ATTRIBUTE_MODIFIER,
                                        context.potency() - 1,
                                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                                )
                        ));
                Optional.ofNullable(minion.getAttribute(Attributes.MAX_HEALTH))
                        .ifPresent((attributeInstance) -> attributeInstance.addTransientModifier(
                                new AttributeModifier(
                                        Identifier.fromNamespaceAndPath(Wizardry.MODID, HEALTH_MODIFIER),
//TODO
//                                        context.multiplyProperties().get(HEALTH_MODIFIER) - 1,
                                        10,
                                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                )
                        ));
                minion.setHealth(minion.getMaxHealth());

                this.addMinionExtras(minion, pos, owner, context, i);

                level.addFreshEntity(minion);
            }
        }

        return true;
    }

    protected T createMinion(Level level, @Nullable LivingEntity caster, SpellContext context) {
        return minionFactory.apply(level);
    }

    protected void addMinionExtras(T minion, BlockPos pos, @Nullable LivingEntity caster, SpellContext context, int alreadySpawned) {
    }

}
