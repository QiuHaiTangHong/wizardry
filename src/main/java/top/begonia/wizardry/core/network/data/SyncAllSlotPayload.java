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

import java.util.List;

public record SyncAllSlotPayload(
        List<ItemStack> stacks,
        BlockPos blockPos
) implements CustomPacketPayload {
    public static final Type<SyncAllSlotPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Wizardry.MODID, "sync_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAllSlotPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), SyncAllSlotPayload::stacks,
            BlockPos.STREAM_CODEC, SyncAllSlotPayload::blockPos,
            SyncAllSlotPayload::new
    );
    public static final String VERSION = "1.0.0";

    @Override
    public @NonNull Type<SyncAllSlotPayload> type() {
        return TYPE;
    }
}
