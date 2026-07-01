package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record WoodTypeProcessor(
        String targetWood
) implements StructureProcessor {

    public static final MapCodec<WoodTypeProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("target_wood").forGetter(WoodTypeProcessor::targetWood)
    ).apply(instance, WoodTypeProcessor::new));

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
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if ("oak".equals(targetWood) || blockId.getNamespace().equals("minecraft") && !blockId.getPath().startsWith("oak_")) {
            if (!blockId.getNamespace().equals("wizardry") || !blockId.getPath().startsWith("oak_")) {
                return processedBlockInfo;
            }
        }
        if (blockId.getPath().startsWith("oak_")) {
            String newPath = blockId.getPath().replace("oak_", targetWood + "_");
            Block targetBlock = BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath(blockId.getNamespace(), newPath)).map(Holder.Reference::value).orElse(Blocks.AIR);
            if (targetBlock != Blocks.AIR) {
                BlockState newState = copyProperties(state, targetBlock.defaultBlockState());
                return new StructureTemplate.StructureBlockInfo(targetPos, newState, processedBlockInfo.nbt());
            }
        }

        return processedBlockInfo;
    }

    @SuppressWarnings("unchecked")
    private static BlockState copyProperties(BlockState from, BlockState state) {
        for (Property<?> property : from.getProperties()) {
            if (state.hasProperty(property)) {
                state = state.setValue((Property) property, from.getValue(property));
            }
        }
        return state;
    }
}
