package top.begonia.wizardry.core.spell.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemUseAnimation;
import top.begonia.wizardry.core.data.spell.definition.spell.SpellProperties;
import top.begonia.wizardry.core.spell.AbstractSpell;

public class None extends AbstractSpell {
    public None(Identifier identifier) {
        super(identifier, ItemUseAnimation.NONE, false);
    }
}
