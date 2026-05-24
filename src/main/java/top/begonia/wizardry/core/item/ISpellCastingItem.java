package top.begonia.wizardry.core.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.begonia.wizardry.core.data.runtime.SpellContextFlow;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.spell.AbstractSpell;

import javax.annotation.Nonnull;

public interface ISpellCastingItem {

    @Nonnull
    AbstractSpell getCurrentSpell(ItemStack stack);

    @Nonnull
    default AbstractSpell getNextSpell(ItemStack stack) {
        return getCurrentSpell(stack);
    }

    @Nonnull
    default AbstractSpell getPreviousSpell(ItemStack stack) {
        return getCurrentSpell(stack);
    }

    default AbstractSpell[] getSpells(ItemStack stack) {
        return new AbstractSpell[]{getCurrentSpell(stack)};
    }

    default void selectNextSpell(ItemStack stack) {
        // If it doesn't need spell-switching then don't bother the implementor with it
    }

    default void selectPreviousSpell(ItemStack stack) {
        // Nothing here either
    }

    default boolean selectSpell(ItemStack stack, int index) {
        return false;
    }

    boolean showSpellHUD(Player player, ItemStack stack);

    default int getCurrentCooldown(ItemStack stack) {
        return 0;
    }

    default int getCurrentMaxCooldown(ItemStack stack) {
        return 0;
    }

    default boolean showSpellsInWorkbench(Player player, ItemStack stack) {
        return true;
    }

    boolean canCast(int castingTick, SpellContextFlow spellContextFlow);

    boolean cast(ItemStack stack, AbstractSpell spell, Player caster, InteractionHand hand, int castingTick, SpellContext spellContextFlow);

}
