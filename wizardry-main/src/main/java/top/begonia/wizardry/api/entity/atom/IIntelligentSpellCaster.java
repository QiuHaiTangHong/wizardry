package top.begonia.wizardry.api.entity.atom;

import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.List;

public interface IIntelligentSpellCaster extends ISpellCaster {
    void setCurrentSpells(List<AbstractSpell> spells);

    List<AbstractSpell> getKnownSpells();
}
