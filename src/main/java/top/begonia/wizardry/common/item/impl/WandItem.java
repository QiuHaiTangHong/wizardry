package top.begonia.wizardry.common.item.impl;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.common.data.spell.part.SpellModifiersData;
import top.begonia.wizardry.common.data.spell.constants.ElementEnum;
import top.begonia.wizardry.common.data.spell.constants.TierEnum;
import top.begonia.wizardry.common.item.IManaStoringItem;
import top.begonia.wizardry.common.item.ISpellCastingItem;
import top.begonia.wizardry.common.item.IWorkbenchItem;
import top.begonia.wizardry.common.spell.AbstractSpell;
import top.begonia.wizardry.common.util.WandHelper;

public class WandItem extends Item implements IWorkbenchItem, ISpellCastingItem, IManaStoringItem {
    public TierEnum tier;
    public ElementEnum element;

    public WandItem(TierEnum tier, ElementEnum element, Properties properties) {
        super(properties);
        this.tier = tier;
        this.element = element;
    }

    @Override
    public @NonNull AbstractSpell getCurrentSpell(ItemStack stack) {
        return WandHelper.getCurrentSpell(stack);
    }

    @Override
    public @NonNull AbstractSpell getNextSpell(ItemStack stack) {
        return WandHelper.getNextSpell(stack);
    }

    @Override
    public @NonNull AbstractSpell getPreviousSpell(ItemStack stack) {
        return WandHelper.getPreviousSpell(stack);
    }

    @Override
    public AbstractSpell[] getSpells(ItemStack stack) {
        return WandHelper.getSpells(stack);
    }

    @Override
    public void selectNextSpell(ItemStack stack) {
        WandHelper.selectNextSpell(stack);
    }

    @Override
    public void selectPreviousSpell(ItemStack stack) {
        WandHelper.selectPreviousSpell(stack);
    }

    @Override
    public boolean selectSpell(ItemStack stack, int index) {
        return WandHelper.selectSpell(stack, index);
    }

    @Override
    public int getCurrentCooldown(ItemStack stack) {
        return WandHelper.getCurrentCooldown(stack);
    }

    @Override
    public int getCurrentMaxCooldown(ItemStack stack) {
        return WandHelper.getCurrentMaxCooldown(stack);
    }

    @Override
    public boolean showSpellHUD(Player player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return true;
    }

    @Override
    public void setDamage(@NonNull ItemStack stack, int damage) {
    }

    @Override
    public void setMana(ItemStack stack, int mana) {
        super.setDamage(stack, getManaCapacity(stack) - mana);
    }

    @Override
    public int getMana(ItemStack stack) {
        return getManaCapacity(stack) - getDamage(stack);
    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return this.getMaxDamage(stack);
    }

    @Override
    public boolean canCast(ItemStack stack, AbstractSpell spell, Player caster, ItemUseAnimation hand, int castingTick, SpellModifiersData modifiers) {
        return false;
    }

    @Override
    public boolean cast(ItemStack stack, AbstractSpell spell, Player caster, ItemUseAnimation hand, int castingTick, SpellModifiersData modifiers) {
        return false;
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 5;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        return false;
    }

    // hasEffect
    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxDamage(@NonNull ItemStack stack) {
        return 1;
    }
}
