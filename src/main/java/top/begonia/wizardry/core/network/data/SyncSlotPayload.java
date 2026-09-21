package top.begonia.wizardry.core.network.data;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public record SyncSlotPayload(
        int index,
        ItemStack stack,
        BlockPos blockPos
) implements CustomPacketPayload {
    public static final Type<SyncSlotPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "sync_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSlotPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SyncSlotPayload::index,
            ItemStack.OPTIONAL_STREAM_CODEC, SyncSlotPayload::stack,
            BlockPos.STREAM_CODEC, SyncSlotPayload::blockPos,
            SyncSlotPayload::new
    );
    public static final String VERSION = "1.0.0";

    @Override
    public @NonNull Type<SyncSlotPayload> type() {
        return TYPE;
    }
}
