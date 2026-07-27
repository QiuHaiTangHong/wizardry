package top.begonia.wizardry.core.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class StructureTrigger extends SimpleCriterionTrigger<StructureTrigger.TriggerInstance> {

    @Override
    public @NonNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(@NonNull ServerPlayer player) {
        ServerLevel level = player.level();
        this.trigger(player, instance -> instance.matches(level, player.blockPosition()));
    }

    public record TriggerInstance(
            Optional<ContextAwarePredicate> player,
            Optional<String> structureType
    ) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        Codec.STRING.optionalFieldOf("structure_type").forGetter(TriggerInstance::structureType)
                ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(ServerLevel level, BlockPos pos) {
            if (this.structureType.isPresent()) {
                var structureManager = level.structureManager();
                StructureStart start = structureManager.getStructureWithPieceAt(pos, (holder) -> {
                    var structureId = level.registryAccess()
                            .lookupOrThrow(Registries.STRUCTURE)
                            .getKey(holder.value());
                    return structureId != null && structureId.toString().equalsIgnoreCase(this.structureType.get());
                });
                return start.isValid();
            }
            return true;
        }
    }
}
