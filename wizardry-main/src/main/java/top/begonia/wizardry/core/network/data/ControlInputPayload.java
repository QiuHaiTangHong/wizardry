package top.begonia.wizardry.core.network.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ControlType;

public record ControlInputPayload(ControlType controlType) implements CustomPacketPayload {
    public static final Type<ControlInputPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "control_input"));
    public static final StreamCodec<ByteBuf, ControlInputPayload> CODEC = StreamCodec.composite(
            ControlType.STREAM_CODEC,
            ControlInputPayload::controlType,
            ControlInputPayload::new
    );
    public static final String VERSION = "1.0.0";

    @Override
    public @NonNull Type<ControlInputPayload> type() {
        return TYPE;
    }
}
