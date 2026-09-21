package top.begonia.wizardry.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class CustomAdvancementTrigger extends SimpleCriterionTrigger<CustomAdvancementTrigger.TriggerInstance> {
    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void triggerFor(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            this.trigger(serverPlayer, instance -> true);
        }
    }

    public record TriggerInstance(
            Optional<Holder<LootItemCondition>> player) implements SimpleCriterionTrigger.SimpleInstance {
        private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
                ).apply(instance, TriggerInstance::new)
        );
    }
}
