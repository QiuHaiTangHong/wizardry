package top.begonia.wizardry.core.entity;

import net.minecraft.world.Difficulty;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import javax.annotation.Nonnull;
import java.util.List;

public interface ISpellCaster {
    @Nonnull
    List<AbstractSpell> getSpells();

    @Nonnull
    default SpellContext getSpellContext() {
        return SpellContext.DEFAULT;
    }

    @Nonnull
    default AbstractSpell getContinuousSpell() {
        return WizardrySpells.NONE.get();
    }

    default void setContinuousSpell(AbstractSpell spell) {
        // Do nothing
    }

    default int getSpellCounter() {
        return 0;
    }

    default void setSpellCounter(int count) {
        // Do nothing
    }

    default int getAimingError(Difficulty difficulty) {
//        return EntityUtils.getDefaultAimingError(difficulty);
        return 0;
    }
}
