package top.begonia.wizardry.client.util;

import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.Comparator;

public interface ISpellSortable {
    SortType getSortType();

    boolean isSortDescending();

    enum SortType {
        TIER("tier", Comparator.naturalOrder()),
        ELEMENT("element", Comparator.comparing(AbstractSpell::getElement).thenComparing(AbstractSpell::getTier)),
        ALPHABETICAL("alphabetical", Comparator.comparing(s -> s.getIdentifier().getPath()));
        public final String name;
        public final Comparator<? super AbstractSpell> comparator;

        SortType(String name, Comparator<? super AbstractSpell> comparator) {
            this.name = name;
            this.comparator = comparator;
        }

    }

}
