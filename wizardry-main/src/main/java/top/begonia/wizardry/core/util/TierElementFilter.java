package top.begonia.wizardry.core.util;

import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.EnabledEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.function.Predicate;

public final class TierElementFilter implements Predicate<AbstractSpell> {
    private final TierEnum tier;
    private final ElementEnum element;
    private final EnabledEnum[] contexts;

    public TierElementFilter(TierEnum tier, ElementEnum element, EnabledEnum... contexts){
        this.tier = tier;
        this.element = element;
        this.contexts = contexts;
    }

    @Override
    public boolean test(@NonNull AbstractSpell spell) {
        return spell.isEnabled(contexts)
                && (this.tier == null || spell.getTier() == this.tier)
                && (this.element == null || spell.getElement() == this.element);
    }
}
