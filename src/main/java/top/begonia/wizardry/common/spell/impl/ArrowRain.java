package top.begonia.wizardry.common.spell.impl;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemUseAnimation;
import top.begonia.wizardry.common.spell.AbstractSpell;

public class ArrowRain extends AbstractSpell {
    public ArrowRain(Identifier identifier) {
        super(identifier, ItemUseAnimation.BOW, false); // 默认设置为弓箭动画
    }
}
