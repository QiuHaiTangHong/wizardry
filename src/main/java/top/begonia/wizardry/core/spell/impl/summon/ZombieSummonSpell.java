package top.begonia.wizardry.core.spell.impl.summon;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import top.begonia.wizardry.core.data.constant.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.living.minion.HuskMinionEntity;
import top.begonia.wizardry.core.entity.living.minion.ZombieMinionEntity;
import top.begonia.wizardry.core.item.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryItems;

import java.util.function.Function;

public class ZombieSummonSpell extends MinionSpell<ZombieMinionEntity> {
    public ZombieSummonSpell(Identifier identifier, Function<Level, ZombieMinionEntity> minionFactory) {
        super(identifier, minionFactory);
        this.soundValues(7, 0.6f, 0);
    }

    @Override
    protected ZombieMinionEntity createMinion(Level level, LivingEntity caster, SpellContext context) {
        if (caster instanceof Player player && ArtefactItem.isArtefactActive(player, WizardryItems.CHARM_MINION_VARIANTS.get())) {
            return new HuskMinionEntity(level);
        } else {
            return super.createMinion(level, caster, context);
        }
    }
}
