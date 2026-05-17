package top.begonia.wizardry.common.spell.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemUseAnimation;
import top.begonia.wizardry.common.spell.AbstractSpell;

public class CurseOfEnfeeblement extends AbstractSpell {
    public CurseOfEnfeeblement(Identifier identifier) {
        super(identifier, ItemUseAnimation.BOW, false); // 默认设置为弓箭动画
    }
}
