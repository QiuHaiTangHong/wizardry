package top.begonia.wizardry.core.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

public final class CommonHelper {
    public static @NonNull MutableComponent getScrollDisplayName(@NonNull ItemStack scroll) {
        AbstractSpell spell = scroll.getOrDefault(WizardryComponents.SPELL.get(), WizardrySpells.NONE).value();
        return Component.translatable("item." + Wizardry.MODID + ".scroll", spell.getDisplayName());
    }
}
