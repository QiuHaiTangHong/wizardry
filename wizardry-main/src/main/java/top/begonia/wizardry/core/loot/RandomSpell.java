package top.begonia.wizardry.core.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.ArrayList;
import java.util.List;

public class RandomSpell extends LootItemConditionalFunction {
    public static final MapCodec<RandomSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(
            instance.group(
                    Codec.list(AbstractSpell.CODEC).optionalFieldOf("spells", new ArrayList<>()).forGetter(f -> f.spells),
                    Codec.list(TierEnum.CODEC).optionalFieldOf("tiers", new ArrayList<>()).forGetter(f -> f.tiers),
                    Codec.list(ElementEnum.CODEC).optionalFieldOf("elements", new ArrayList<>()).forGetter(f -> f.elements),
                    Codec.BOOL.optionalFieldOf("ignore_weighting", false).forGetter(f -> f.ignoreWeighting),
                    Codec.FLOAT.optionalFieldOf("undiscovered_bias", 0.0f).forGetter(f -> f.undiscoveredBias)
            )
    ).apply(instance, RandomSpell::new));

    private final List<Holder<AbstractSpell>> spells;
    private final List<TierEnum> tiers;
    private final List<ElementEnum> elements;
    private final boolean ignoreWeighting;
    private final float undiscoveredBias;

    protected RandomSpell(List<LootItemCondition> predicates, List<Holder<AbstractSpell>> spells, List<TierEnum> tiers, List<ElementEnum> elements, boolean ignoreWeighting, float undiscoveredBias) {
        super(predicates);
        this.spells = spells;
        this.tiers = tiers;
        this.elements = elements;
        this.ignoreWeighting = ignoreWeighting;
        this.undiscoveredBias = undiscoveredBias;
    }

    @Override
    public @NonNull MapCodec<RandomSpell> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull ItemStack run(@NonNull ItemStack stack, @NonNull LootContext context) {
        return stack;
    }
}
