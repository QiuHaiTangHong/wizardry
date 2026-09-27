package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record BookshelfMarkerProcessor(
) implements StructureProcessor {
    public static final MapCodec<BookshelfMarkerProcessor> CODEC = MapCodec.unit(new BookshelfMarkerProcessor());

    @Override
    public @NonNull MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }

    @Override
    public StructureTemplate.@NonNull StructureBlockInfo process(
            @NonNull LevelReader level,
            @NonNull BlockPos targetPosition,
            @NonNull BlockPos referencePos,
            StructureTemplate.@NonNull StructureBlockInfo originalBlockInfo,
            StructureTemplate.@NonNull StructureBlockInfo processedBlockInfo,
            @NonNull StructurePlaceSettings settings,
            @Nullable StructureTemplate template
    ) {
        if (processedBlockInfo.nbt() != null) {
            CompoundTag tag = processedBlockInfo.nbt();
            tag.putBoolean("Natural", true);
            return new StructureTemplate.StructureBlockInfo(
                    processedBlockInfo.pos(),
                    processedBlockInfo.state(),
                    tag
            );
        }
        return processedBlockInfo;
    }
}
