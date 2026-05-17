package top.begonia.wizardry.common.util;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.registry.WizardryComponents;
import top.begonia.wizardry.common.registry.WizardrySpells;
import top.begonia.wizardry.common.spell.AbstractSpell;

public final class CommonHelper {
    public static MutableComponent getScrollDisplayName(ItemStack scroll) {
        Holder<AbstractSpell> holder = scroll.get(WizardryComponents.SPELL_BOOK_KEY.get());
        AbstractSpell spell = WizardrySpells.NONE.get();
        if (holder != null) {
            spell = holder.value();
        }
        return Component.translatable("item." + Wizardry.MODID + ".scroll", spell.getDisplayName());
    }
}
