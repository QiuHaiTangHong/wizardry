package top.begonia.wizardry.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.Optional;

public class SpellCastTrigger extends SimpleCriterionTrigger<SpellCastTrigger.TriggerInstance> {

    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, AbstractSpell spell, ItemStack stack) {
        this.trigger(player, instance -> instance.matches(spell, stack));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<SpellPredicate> spell,
            Optional<ItemPredicate> item
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        SpellPredicate.CODEC.optionalFieldOf("spell").forGetter(TriggerInstance::spell),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)
                ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(AbstractSpell spellIn, ItemStack stackIn) {
            if (this.spell.isPresent() && !this.spell.get().test(spellIn)) {
                return false;
            }
            return this.item.isEmpty() || this.item.get().test(stackIn);
        }
    }
}
