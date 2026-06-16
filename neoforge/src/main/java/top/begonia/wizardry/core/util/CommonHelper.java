package top.begonia.wizardry.core.util;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

public final class CommonHelper {
    public static MutableComponent getScrollDisplayName(ItemStack scroll) {
        Holder<AbstractSpell> holder = scroll.get(WizardryComponents.SPELL.get());
        AbstractSpell spell = WizardrySpells.NONE.get();
        if (holder != null) {
            spell = holder.value();
        }
        return Component.translatable("item." + Wizardry.MODID + ".scroll", spell.getDisplayName());
    }
}
