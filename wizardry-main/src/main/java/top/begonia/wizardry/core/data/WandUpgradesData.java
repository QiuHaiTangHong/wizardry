package top.begonia.wizardry.core.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public record WandUpgradesData(Map<Holder<Item>, Integer> counts) {
    public static final WandUpgradesData EMPTY = new WandUpgradesData(Map.of());
    public static final Codec<WandUpgradesData> CODEC = Codec
            .unboundedMap(Item.CODEC, Codec.INT)
            .xmap(WandUpgradesData::new, WandUpgradesData::counts);
    public static final StreamCodec<RegistryFriendlyByteBuf, WandUpgradesData> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new,
                    Item.STREAM_CODEC,
                    ByteBufCodecs.VAR_INT
            ).map(
                    WandUpgradesData::new,
                    obj -> new HashMap<>(obj.counts())
            ).cast();

    @Contract("_ -> new")
    public @NonNull WandUpgradesData withUpgrade(Holder<Item> holderItem) {
        Map<Holder<Item>, Integer> newCounts = new HashMap<>(this.counts);
        newCounts.put(holderItem, newCounts.getOrDefault(holderItem, 0) + 1);
        return new WandUpgradesData(Map.copyOf(newCounts));
    }
}
