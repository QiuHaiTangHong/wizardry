package top.begonia.wizardry.common.item.impl;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.common.data.spell.part.SpellModifiersData;
import top.begonia.wizardry.common.item.ISpellCastingItem;
import top.begonia.wizardry.common.item.IWorkbenchItem;
import top.begonia.wizardry.common.registry.WizardryComponents;
import top.begonia.wizardry.common.registry.WizardrySpells;
import top.begonia.wizardry.common.spell.AbstractSpell;
import top.begonia.wizardry.common.util.CommonHelper;

public class ScrollItem extends Item implements ISpellCastingItem, IWorkbenchItem {
    public static final int CASTING_TIME = 120;

    public ScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull AbstractSpell getCurrentSpell(ItemStack stack) {
        Holder<AbstractSpell> holder = stack.get(WizardryComponents.SPELL_BOOK_KEY);
        return (holder != null) ? holder.value() : WizardrySpells.NONE.get();
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return true;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        return CommonHelper.getScrollDisplayName(stack);
    }

    @Override
    public boolean showSpellHUD(Player player, ItemStack stack) {
        return false;
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
