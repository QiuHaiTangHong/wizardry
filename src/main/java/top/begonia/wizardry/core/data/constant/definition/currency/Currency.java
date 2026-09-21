package top.begonia.wizardry.core.data.constant.definition.currency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.api.data.IResultData;
import top.begonia.wizardry.core.data.constant.definition.currency.part.CurrencyEntry;

import java.util.List;
import java.util.Objects;

public record Currency(
        List<CurrencyEntry> currency
) implements IResultData {
    public static final Codec<Currency> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(CurrencyEntry.CODEC).fieldOf("currency").forGetter(Currency::currency)
            ).apply(instance, Currency::new)
    );

    @Override
    public Class<? extends IResultData> getDataClass() {
        return IResultData.class;
    }

    public record UnpackEntry(
            Item item,
            int value,
            DataComponentExactPredicate components
    ) {
    }

    public @NonNull @Unmodifiable List<UnpackEntry> unpack() {
        return this.currency
                .stream()
                .map(entry -> {
                    if (BuiltInRegistries.ITEM.get(entry.id()).isPresent()) {
                        return new UnpackEntry(BuiltInRegistries.ITEM.get(entry.id()).get().value(), entry.value(), entry.components());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
