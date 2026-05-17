package top.begonia.wizardry.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import top.begonia.wizardry.common.item.impl.SpellBookItem;
import top.begonia.wizardry.common.registry.WizardryComponents;
import top.begonia.wizardry.common.spell.AbstractSpell;

import java.util.Objects;

public class SpellBookScreen extends SpellInfoScreen {

    private final SpellBookItem book;
    private final AbstractSpell spell;

    public SpellBookScreen(ItemStack stack) {

        super(Component.empty(), 288, 180);

        if (!(stack.getItem() instanceof SpellBookItem)) {
            throw new ClassCastException("Cannot create spell book GUI for item that does not extend ItemSpellBook!");
        }

        this.book = (SpellBookItem) stack.getItem();
        this.spell = Objects.requireNonNull(stack.get(WizardryComponents.SPELL_BOOK_KEY.get())).value();
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
