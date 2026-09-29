package top.begonia.wizardry.core.worldgen.processor;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;

public interface IDelayedOperation {
    void processDelayedOperation(
            @NonNull LevelAccessor level,
            StructureTemplate.@NonNull StructureBlockInfo dataMarker,
            @NonNull BlockPos position,
            @NonNull Rotation rotation,
            @NonNull RandomSource random,
            @NonNull BoundingBox chunkBB
    );
}
