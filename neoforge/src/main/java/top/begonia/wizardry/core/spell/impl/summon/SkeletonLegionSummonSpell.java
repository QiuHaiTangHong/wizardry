package top.begonia.wizardry.core.spell.impl.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.entity.living.minion.SkeletonMinionEntity;
import top.begonia.wizardry.core.entity.living.minion.StrayMinionEntity;
import top.begonia.wizardry.core.item.impl.ArtefactItem;
import top.begonia.wizardry.core.registry.WizardryItems;

import java.util.function.Function;

public class SkeletonLegionSummonSpell extends MinionSpell<SkeletonMinionEntity> {
    public SkeletonLegionSummonSpell(Identifier identifier, Function<Level, SkeletonMinionEntity> minionFactory) {
        super(identifier, minionFactory);
        this.soundValues(7, 0.6f, 0);
    }

    @Override
    protected SkeletonMinionEntity createMinion(Level level, LivingEntity caster, SpellContext context) {
        if (caster instanceof Player player && ArtefactItem.isArtefactActive(player, WizardryItems.CHARM_MINION_VARIANTS.get())) {
            return new StrayMinionEntity(level);
        } else {
            return super.createMinion(level, caster, context);
        }
    }

    @Override
    protected void addMinionExtras(SkeletonMinionEntity minion, BlockPos pos, LivingEntity owner, SpellContext context, int alreadySpawned) {
        if (alreadySpawned % 2 == 0) {
            // Archers
            minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
        } else {
            // Swordsmen
            minion.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        }
        minion.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
        minion.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
        minion.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        minion.setDropChance(EquipmentSlot.HEAD, 0.0f);
        minion.setDropChance(EquipmentSlot.CHEST, 0.0f);
    }
}
