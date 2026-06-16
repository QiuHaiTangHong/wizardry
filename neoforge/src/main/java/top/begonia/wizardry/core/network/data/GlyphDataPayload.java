package top.begonia.wizardry.core.network.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public record GlyphDataPayload(Map<Identifier, String> names,
                               Map<Identifier, String> descriptions) implements CustomPacketPayload {
    public static final Type<GlyphDataPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("wizardry", "glyph_data"));
    public static final StreamCodec<ByteBuf, GlyphDataPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.STRING_UTF8), GlyphDataPayload::names,
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.STRING_UTF8), GlyphDataPayload::descriptions,
            GlyphDataPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
