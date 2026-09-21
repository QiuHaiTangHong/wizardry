package top.begonia.wizardry.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class WizardryContainerTrigger extends SimpleCriterionTrigger<WizardryContainerTrigger.TriggerInstance> {

    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player, instance -> instance.matches(stack));
    }

    public record TriggerInstance(
            Optional<Holder<LootItemCondition>> player,
            Optional<ItemPredicate> item
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)
                ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(ItemStack stackIn) {
            return this.item.isEmpty() || this.item.get().test(stackIn);
        }
    }
}
