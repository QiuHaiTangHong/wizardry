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
import top.begonia.wizardry.core.util.BlockUtils;

/**
 * 来自流浪地球的小苔藓(MOSS (^_^))处理器
 *
 * @param mossiness
 * @param heightWeight
 */
public record Processor550W(
        float mossiness,
        float heightWeight
) implements StructureProcessor {
    public static final MapCodec<Processor550W> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("mossiness").forGetter(Processor550W::mossiness),
            Codec.FLOAT.fieldOf("height_weight").forGetter(Processor550W::heightWeight)
    ).apply(instance, Processor550W::new));

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
        int groundLevel = referencePos.getY() + 1;
        float chance = this.mossiness - this.heightWeight * (targetPos.getY() - groundLevel);
        if (random.nextFloat() < chance) {
            if (state.is(Blocks.COBBLESTONE)) {
                return new StructureTemplate.StructureBlockInfo(
                        targetPos,
                        BlockUtils.copyPropertiesIf(state, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), _ -> true),
                        processedBlockInfo.nbt()
                );
            } else if (state.is(Blocks.STONE_BRICKS)) {
                return new StructureTemplate.StructureBlockInfo(
                        targetPos,
                        BlockUtils.copyPropertiesIf(state, Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), _ -> true),
                        processedBlockInfo.nbt()
                );
            }
        }
        return processedBlockInfo;
    }
}
