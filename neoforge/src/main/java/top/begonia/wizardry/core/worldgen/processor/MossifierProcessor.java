package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record MossifierProcessor(
        float mossiness,
        float heightWeight,
        int groundLevel
) implements StructureProcessor {
    public static final MapCodec<MossifierProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("mossiness").forGetter(MossifierProcessor::mossiness),
            Codec.FLOAT.fieldOf("height_weight").forGetter(MossifierProcessor::heightWeight),
            Codec.INT.fieldOf("ground_level").forGetter(MossifierProcessor::groundLevel)
    ).apply(instance, MossifierProcessor::new));

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
        BlockState state = processedBlockInfo.state();
        BlockPos targetPos = processedBlockInfo.pos();
        RandomSource random = settings.getRandom(targetPos);
        float chance = this.mossiness - this.heightWeight * (targetPos.getY() - this.groundLevel);
        if (random.nextFloat() < chance) {
            if (state.is(Blocks.COBBLESTONE)) {
                return new StructureTemplate.StructureBlockInfo(targetPos, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), processedBlockInfo.nbt());
            } else if (state.is(Blocks.STONE_BRICKS)) {
                return new StructureTemplate.StructureBlockInfo(targetPos, Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), processedBlockInfo.nbt());
            }
        }
        return processedBlockInfo;
    }
}
