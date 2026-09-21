package top.begonia.wizardry.core.data.constant.definition.currency.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.resources.Identifier;

public record CurrencyEntry(
        Identifier id,
        DataComponentExactPredicate components,
        int value
) {
    public static final Codec<CurrencyEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("id").forGetter(CurrencyEntry::id),
                    DataComponentExactPredicate.CODEC
                            .optionalFieldOf("components", DataComponentExactPredicate.EMPTY)
                            .forGetter(CurrencyEntry::components),
                    Codec.INT.fieldOf("value").forGetter(CurrencyEntry::value)
            ).apply(instance, CurrencyEntry::new)
    );
}
