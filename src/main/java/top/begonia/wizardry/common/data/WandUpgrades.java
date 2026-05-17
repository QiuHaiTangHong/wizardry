package top.begonia.wizardry.common.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public record WandUpgrades(Map<String, Integer> counts) {
    public static final WandUpgrades EMPTY = new WandUpgrades(Map.of());
    public static final Codec<WandUpgrades> CODEC = Codec
            .unboundedMap(Codec.STRING, Codec.INT)
            .xmap(WandUpgrades::new, WandUpgrades::counts);
    public static final StreamCodec<FriendlyByteBuf, WandUpgrades> STREAM_CODEC =
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.stringUtf8(256),
                    ByteBufCodecs.VAR_INT
            ).map(
                    WandUpgrades::new,
                    obj -> new HashMap<>(obj.counts())
            ).cast();
    public WandUpgrades withUpgrade(String key) {
        Map<String, Integer> newCounts = new HashMap<>(this.counts);
        newCounts.put(key, newCounts.getOrDefault(key, 0) + 1);
        return new WandUpgrades(Map.copyOf(newCounts));
    }
}
