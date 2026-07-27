package top.begonia.wizardry.core.constants;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Rarity;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public enum ManaFlaskTypeEnum implements StringRepresentable {

    SMALL(75, 25, Rarity.COMMON),
    MEDIUM(350, 40, Rarity.COMMON),
    LARGE(1400, 60, Rarity.RARE);

    public final int capacity;
    public final int useDuration;
    public final Rarity rarity;

    public static final Codec<ManaFlaskTypeEnum> CODEC = StringRepresentable.fromEnum(ManaFlaskTypeEnum::values);
    public static final StreamCodec<ByteBuf, ManaFlaskTypeEnum> STREAM_CODEC =
            ByteBufCodecs.idMapper(
                    id -> (id >= 0 && id < values().length) ? values()[id] : SMALL,
                    ManaFlaskTypeEnum::ordinal
            );

    ManaFlaskTypeEnum(int capacity, int useDuration, Rarity rarity) {
        this.capacity = capacity;
        this.useDuration = useDuration;
        this.rarity = rarity;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
