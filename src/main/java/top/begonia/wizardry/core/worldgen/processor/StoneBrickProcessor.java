package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;

public record StoneBrickProcessor(
) implements StructureProcessor {
    public static final MapCodec<StoneBrickProcessor> CODEC = MapCodec.unit(new StoneBrickProcessor());

    @Override
    public @NonNull MapCodec<StoneBrickProcessor> codec() {
        return StoneBrickProcessor.CODEC;
    }

    @Override
    public StructureTemplate.@NonNull StructureBlockInfo process(
            @NonNull LevelReader level,
            @NonNull BlockPos targetPosition,
            @NonNull BlockPos referencePos,
            StructureTemplate.@NonNull StructureBlockInfo originalBlockInfo,
            StructureTemplate.@NonNull StructureBlockInfo processedBlockInfo,
            @NonNull StructurePlaceSettings settings,
            StructureTemplate template
    ) {
        BlockPos pos = processedBlockInfo.pos();
        BlockState state = processedBlockInfo.state();
        CompoundTag nbt = processedBlockInfo.nbt();
        RandomSource variableRandom = settings.getRandom(targetPosition);
        RandomSource fixedRandom = settings.getRandom(referencePos);
        float stoneBrickChance = fixedRandom.nextFloat();
        if (variableRandom.nextFloat() > stoneBrickChance) {
            // Behold, three different ways of doing the same thing, because this is pre-flattening!
            // Also, stone bricks are about the least consistently-named thing in the entire game, so yay
            if (state.is(Blocks.COBBLESTONE)) {
                return new StructureTemplate.StructureBlockInfo(pos, Blocks.STONE_BRICKS.defaultBlockState().withPropertiesOf(state), nbt);
            } else if (state.is(Blocks.COBBLESTONE_SLAB)) {
                return new StructureTemplate.StructureBlockInfo(pos, Blocks.STONE_BRICK_SLAB.defaultBlockState().withPropertiesOf(state), nbt);
            } else if (state.is(Blocks.STONE_STAIRS)) { // "Stone" stairs are actually cobblestone
                return new StructureTemplate.StructureBlockInfo(pos, Blocks.STONE_BRICK_STAIRS.defaultBlockState().withPropertiesOf(state), nbt);
            }
        }
        return processedBlockInfo;
    }
}
