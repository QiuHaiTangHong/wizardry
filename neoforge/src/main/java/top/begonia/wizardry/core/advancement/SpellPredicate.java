package top.begonia.wizardry.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record SpellPredicate(
        Optional<Holder<AbstractSpell>> spell,
        List<TierEnum> tiers,
        List<ElementEnum> elements
) {
    public static final SpellPredicate ANY = new SpellPredicate(
            Optional.empty(),
            Arrays.asList(TierEnum.values()),
            Arrays.asList(ElementEnum.values())
    );

    public static final Codec<SpellPredicate> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    AbstractSpell.CODEC.optionalFieldOf("spell").forGetter(SpellPredicate::spell),
                    TierEnum.CODEC.listOf().optionalFieldOf("tiers", Arrays.asList(TierEnum.values())).forGetter(SpellPredicate::tiers),
                    ElementEnum.CODEC.listOf().optionalFieldOf("elements", Arrays.asList(ElementEnum.values())).forGetter(SpellPredicate::elements)
            ).apply(instance, SpellPredicate::new)
    );

    public boolean test(AbstractSpell spellIn) {
        if (this.spell.isPresent() && this.spell.get().value() != spellIn) {
            return false;
        }
        if (!this.tiers.contains(spellIn.getTier())) {
            return false;
        }
        return this.elements.contains(spellIn.getElement());
    }
}
