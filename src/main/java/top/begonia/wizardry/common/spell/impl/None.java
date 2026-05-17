package top.begonia.wizardry.common.spell.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemUseAnimation;
import top.begonia.wizardry.common.spell.AbstractSpell;

public class None extends AbstractSpell {
    public None(Identifier identifier) {
        super(identifier, ItemUseAnimation.NONE, false);
    }
}
