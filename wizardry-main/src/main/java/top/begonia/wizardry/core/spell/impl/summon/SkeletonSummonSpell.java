package top.begonia.wizardry.core.spell.impl.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.living.minion.SkeletonMinionEntity;
import top.begonia.wizardry.core.entity.living.minion.StrayMinionEntity;
import top.begonia.wizardry.core.item.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryItems;

import java.util.function.Function;

public class SkeletonSummonSpell extends MinionSpell<SkeletonMinionEntity> {
    public SkeletonSummonSpell(Identifier identifier, Function<Level, SkeletonMinionEntity> minionFactory) {
        super(identifier, minionFactory);
        this.soundValues(7, 0.6f, 0);
    }

    @Override
    protected SkeletonMinionEntity createMinion(Level level, LivingEntity owner, SpellContext context) {
        if (owner instanceof Player player && ArtefactItem.isArtefactActive(player, WizardryItems.CHARM_MINION_VARIANTS.get())) {
            return new StrayMinionEntity(level);
        } else {
            return super.createMinion(level, owner, context);
        }
    }

    @Override
    protected void addMinionExtras(@NonNull SkeletonMinionEntity minion, BlockPos pos, LivingEntity owner, SpellContext context, int alreadySpawned) {
        minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        minion.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }
}
