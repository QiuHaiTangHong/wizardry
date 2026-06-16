package top.begonia.wizardry.core.network.data;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

import java.util.Locale;

public record ControlInputPayload(ControlType controlType) implements CustomPacketPayload {
    public static final Type<ControlInputPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "control_input"));

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final StreamCodec<ByteBuf, ControlInputPayload> CODEC = StreamCodec.composite(
            ControlType.STREAM_CODEC, ControlInputPayload::controlType,
            ControlInputPayload::new
    );

    public enum ControlType implements StringRepresentable {
        NONE,
        APPLY_BUTTON,
        NEXT_SPELL_KEY,
        PREVIOUS_SPELL_KEY,
        RESURRECT_BUTTON,
        CANCEL_RESURRECT,
        POSSESSION_PROJECTILE,
        CLEAR_BUTTON;

        private static final ControlType[] BY_ID = values();
        public static final Codec<ControlType> CODEC = StringRepresentable.fromEnum(ControlType::values);
        public static final StreamCodec<ByteBuf, ControlType> STREAM_CODEC =
                ByteBufCodecs.idMapper(
                        id -> (id >= 0 && id < BY_ID.length) ? BY_ID[id] : NONE,
                        ControlType::ordinal
                );

        @Override
        public @NonNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
