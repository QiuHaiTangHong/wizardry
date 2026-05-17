package top.begonia.wizardry.common.item.impl;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.begonia.wizardry.common.data.spell.constants.ElementEnum;
import top.begonia.wizardry.common.item.IManaStoringItem;
import top.begonia.wizardry.common.item.IWorkbenchItem;

import javax.annotation.Nullable;

public class WizardArmourItem extends Item implements IWorkbenchItem, IManaStoringItem {

    private final ElementEnum element;

    public WizardArmourItem(@Nullable ElementEnum element, Item.Properties properties) {
        super(properties);
        this.element = element;
    }

    @Override
    public int getMana(ItemStack stack) {
        return 0;
    }

    @Override
    public void setMana(ItemStack stack, int mana) {

    }

    @Override
    public int getManaCapacity(ItemStack stack) {
        return 0;
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        return false;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return false;
    }
}
