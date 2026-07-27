package top.begonia.wizardry.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.item.impl.SpellBookItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

public class SpellBookScreen extends SpellInfoScreen {

    private final SpellBookItem book;
    private final AbstractSpell spell;

    public SpellBookScreen(@NonNull ItemStack stack) {

        super(Component.empty(), 288, 180);

        if (!(stack.getItem() instanceof SpellBookItem)) {
            throw new ClassCastException("Cannot create spell book GUI for item that does not extend ItemSpellBook!");
        }

        this.book = (SpellBookItem) stack.getItem();
        this.spell = stack.getOrDefault(WizardryComponents.SPELL.get(), WizardrySpells.NONE).value();
    }

    @Override
    public AbstractSpell getSpell() {
        return spell;
    }

    @Override
    public Identifier getTexture() {
        return book.getGuiTexture(spell);
    }

}
