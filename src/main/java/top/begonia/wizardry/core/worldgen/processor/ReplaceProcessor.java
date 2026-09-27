package top.begonia.wizardry.core.worldgen.processor;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.core.registry.WizardryBlocks;

import java.util.Set;

public record ReplaceProcessor(
) implements StructureProcessor {
    private static final Set<Block> NON_REPLACEABLE_BLOCKS = ImmutableSet.of(
            WizardryBlocks.IMBUEMENT_ALTAR.get(),
            WizardryBlocks.RECEPTACLE.get(),
            WizardryBlocks.BOOKSHELF.get()
    );
    public static final MapCodec<ReplaceProcessor> CODEC = MapCodec.unit(new ReplaceProcessor());

    @Override
    public @NonNull MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }

    public StructureTemplate.@NonNull StructureBlockInfo process(
            @NonNull LevelReader level,
            @NonNull BlockPos targetPosition,
            @NonNull BlockPos referencePos,
            StructureTemplate.@NonNull StructureBlockInfo originalBlockInfo,
            StructureTemplate.@NonNull StructureBlockInfo processedBlockInfo,
            @NonNull StructurePlaceSettings settings,
            @Nullable StructureTemplate template
    ) {
        BlockPos pos = processedBlockInfo.pos();
        BlockState worldState = level.getBlockState(pos);
        Block block = processedBlockInfo.state().getBlock();
        if (worldState.isAir() && !NON_REPLACEABLE_BLOCKS.contains(block)) {
            return new StructureTemplate.StructureBlockInfo(pos, worldState, null);
        }
        return processedBlockInfo;
    }
}
