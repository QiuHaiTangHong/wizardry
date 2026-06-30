package top.begonia.wizardry.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.api.event.DiscoverSpellEvent;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.Optional;

public class SpellDiscoveryTrigger extends SimpleCriterionTrigger<SpellDiscoveryTrigger.TriggerInstance> {

    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, AbstractSpell spell, DiscoverSpellEvent.Source source) {
        this.trigger(player, instance -> instance.matches(spell, source));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<SpellPredicate> spell,
            Optional<DiscoverSpellEvent.Source> source
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        SpellPredicate.CODEC.optionalFieldOf("spell").forGetter(TriggerInstance::spell),
                        DiscoverSpellEvent.Source.CODEC.optionalFieldOf("source").forGetter(TriggerInstance::source)
                ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(AbstractSpell spellIn, DiscoverSpellEvent.Source sourceIn) {
            if (this.spell.isPresent() && !this.spell.get().test(spellIn)) {
                return false;
            }
            return this.source.isEmpty() || this.source.get() == sourceIn;
        }
    }
}
