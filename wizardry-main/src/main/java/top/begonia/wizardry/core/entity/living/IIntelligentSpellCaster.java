package top.begonia.wizardry.core.entity.living;

import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.List;

public interface IIntelligentSpellCaster extends ISpellCaster {
    void setCurrentSpells(List<AbstractSpell> spells);

    List<AbstractSpell> getKnownSpells();
}
