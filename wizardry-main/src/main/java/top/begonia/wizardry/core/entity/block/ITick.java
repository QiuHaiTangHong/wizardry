package top.begonia.wizardry.core.entity.block;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public interface ITick {
    default <T extends BlockEntity> void tick(@NonNull Level level, BlockPos pos, BlockState state, @NonNull T blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            this.serverTick(serverLevel, pos, state, blockEntity);
        } else if (level instanceof ClientLevel clientLevel) {
            this.clientTick(clientLevel, pos, state, blockEntity);
        }
    }

    <T extends BlockEntity> void serverTick(@NonNull ServerLevel level, BlockPos pos, BlockState state, @NonNull T blockEntity);

    <T extends BlockEntity> void clientTick(@NonNull ClientLevel level, BlockPos pos, BlockState state, @NonNull T blockEntity);
}
