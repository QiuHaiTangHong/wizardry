package top.begonia.wizardry.core.constants;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public enum ArtefactTypeEnum implements StringRepresentable {
    RING(2),
    AMULET(1),
    CHARM(1),
    BELT(1),
    BODY(1),
    HEAD(1);

    public static final Codec<ArtefactTypeEnum> CODEC = StringRepresentable.fromEnum(ArtefactTypeEnum::values);
    public static final StreamCodec<ByteBuf, ArtefactTypeEnum> STREAM_CODEC =
            ByteBufCodecs.idMapper(
                    id -> (id >= 0 && id < values().length) ? values()[id] : RING,
                    ArtefactTypeEnum::ordinal
            );
    public final int maxAtOnce;

    ArtefactTypeEnum(int maxAtOnce) {
        this.maxAtOnce = maxAtOnce;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
