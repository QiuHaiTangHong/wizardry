package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import org.jspecify.annotations.Nullable;

public record CrackedStoneProcessor(
        float chance
) implements StructureProcessor {
    public static final MapCodec<CrackedStoneProcessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("chance").forGetter(CrackedStoneProcessor::chance)
            ).apply(instance, CrackedStoneProcessor::new)
    );

    @Override
    public @NonNull MapCodec<CrackedStoneProcessor> codec() {
        return CrackedStoneProcessor.CODEC;
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
        BlockPos pos = processedBlockInfo.pos();
        CompoundTag nbt = processedBlockInfo.nbt();
        BlockState state = processedBlockInfo.state();
        RandomSource random = settings.getRandom(referencePos);
        if (state.is(Blocks.STONE_BRICKS) && random.nextFloat() < this.chance) {
            return new StructureTemplate.StructureBlockInfo(pos, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), nbt);
        }
        return processedBlockInfo;
    }
}
