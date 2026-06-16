package top.begonia.wizardry.core.inventory;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.registry.WizardryItems;

import java.util.Set;

public class InventoryUtil {
    private static final Set<Item> validItems = Set.of(
            Items.BOOK,
            Items.WRITTEN_BOOK,
            Items.WRITABLE_BOOK,
            Items.ENCHANTED_BOOK,
            WizardryItems.SPELL_BOOK.get(),
            WizardryItems.ARCANE_TOME.get(),
            WizardryItems.WIZARD_HANDBOOK.get(),
//            WizardryItems.ruined_spell_book,
            WizardryItems.SCROLL.get(),
//            WizardryItems.blank_scroll,
//            WizardryItems.identification_scroll,
            WizardryItems.STORAGE_UPGRADE.get(),
            WizardryItems.SIPHON_UPGRADE.get(),
            WizardryItems.CONDENSER_UPGRADE.get(),
            WizardryItems.RANGE_UPGRADE.get(),
            WizardryItems.DURATION_UPGRADE.get(),
            WizardryItems.COOLDOWN_UPGRADE.get(),
            WizardryItems.BLAST_UPGRADE.get(),
            WizardryItems.ATTUNEMENT_UPGRADE.get(),
            WizardryItems.MELEE_UPGRADE.get()
    );

    public static boolean isBook(@NonNull ItemStack stack) {
        return validItems.contains(stack.getItem());
    }
}
