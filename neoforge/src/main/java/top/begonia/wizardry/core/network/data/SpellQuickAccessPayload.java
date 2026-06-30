package top.begonia.wizardry.core.network.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public record SpellQuickAccessPayload(int index) implements CustomPacketPayload {

    public static final Type<SpellQuickAccessPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "spell_quick_access"));

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static final StreamCodec<ByteBuf, SpellQuickAccessPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SpellQuickAccessPayload::index,
            SpellQuickAccessPayload::new
    );
}
