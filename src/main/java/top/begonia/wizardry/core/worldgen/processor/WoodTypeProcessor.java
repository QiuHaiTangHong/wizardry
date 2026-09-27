package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.constants.WizardryBlockStateProperties;
import top.begonia.wizardry.api.constants.WoodTypeEnum;
import top.begonia.wizardry.core.registry.WizardryBlocks;
import top.begonia.wizardry.core.util.BlockUtils;

public record WoodTypeProcessor() implements StructureProcessor {
    public static final MapCodec<WoodTypeProcessor> CODEC = MapCodec.unit(new WoodTypeProcessor());

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
            StructureTemplate template
    ) {
        BlockState state = processedBlockInfo.state();
        BlockPos rawPos = processedBlockInfo.pos();
        CompoundTag rawNbt = processedBlockInfo.nbt();
        WoodTypeEnum woodType = BlockUtils.getBiomeWoodType(level.getBiome(targetPosition));
        // Why do these each have their own property key?
        if (state.is(BlockTags.PLANKS)) { // This covers gilded wood too
            BlockState finalState = BlockUtils.generateBlockState(
                    woodType,
                    typeEnum -> Identifier.fromNamespaceAndPath(
                            BlockTags.PLANKS.location().getNamespace(),
                            typeEnum.getSerializedName().concat("_" + BlockTags.PLANKS.location().getPath())
                    ),
                    (_, blockState) -> BlockUtils.copyPropertiesIf(state, blockState, _ -> true)
            );
            return new StructureTemplate.StructureBlockInfo(rawPos, finalState, rawNbt);
        } else if (state.is(BlockTags.SLABS)) {
            // This is a mess, no wonder the flattening happened
            BlockState finalState = BlockUtils.generateBlockState(
                    woodType,
                    typeEnum -> Identifier.fromNamespaceAndPath(
                            BlockTags.SLABS.location().getNamespace(),
                            typeEnum.getSerializedName().concat("_" + BlockTags.SLABS.location().getPath())
                    ),
                    (_, blockState) -> BlockUtils.copyPropertiesIf(state, blockState, _ -> true)
            );
            return new StructureTemplate.StructureBlockInfo(rawPos, finalState, rawNbt);
        } else if (state.is(BlockTags.DOORS)) {
            BlockState finalState = BlockUtils.generateBlockState(
                    woodType,
                    typeEnum -> Identifier.fromNamespaceAndPath(
                            BlockTags.DOORS.location().getNamespace(),
                            typeEnum.getSerializedName().concat("_" + BlockTags.DOORS.location().getPath())
                    ),
                    (_, blockState) -> BlockUtils.copyPropertiesIf(state, blockState, _ -> true)
            );
            return new StructureTemplate.StructureBlockInfo(rawPos, finalState, rawNbt);
        } else if (state.is(BlockTags.STAIRS)) {
            BlockState finalState = BlockUtils.generateBlockState(
                    woodType,
                    typeEnum -> Identifier.fromNamespaceAndPath(
                            BlockTags.STAIRS.location().getNamespace(),
                            typeEnum.getSerializedName().concat("_" + BlockTags.STAIRS.location().getPath())
                    ),
                    (_, blockState) -> BlockUtils.copyPropertiesIf(state, blockState, _ -> true)
            );
            return new StructureTemplate.StructureBlockInfo(rawPos, finalState, rawNbt);
        } else if (state.is(BlockTags.FENCES)) {
            BlockState finalState = BlockUtils.generateBlockState(
                    woodType,
                    typeEnum -> Identifier.fromNamespaceAndPath(
                            BlockTags.FENCES.location().getNamespace(),
                            typeEnum.getSerializedName().concat("_" + BlockTags.FENCES.location().getPath())
                    ),
                    (_, blockState) -> BlockUtils.copyPropertiesIf(state, blockState, _ -> true)
            );
            return new StructureTemplate.StructureBlockInfo(rawPos, finalState, rawNbt);
        } else if (state.is(BlockTags.FENCE_GATES)) {
            BlockState finalState = BlockUtils.generateBlockState(
                    woodType,
                    typeEnum -> Identifier.fromNamespaceAndPath(
                            BlockTags.FENCE_GATES.location().getNamespace(),
                            typeEnum.getSerializedName().concat("_" + BlockTags.FENCE_GATES.location().getPath())
                    ),
                    (_, blockState) -> BlockUtils.copyPropertiesIf(state, blockState, _ -> true)
            );
            return new StructureTemplate.StructureBlockInfo(rawPos, finalState, rawNbt);
        } else if (state.is(WizardryBlocks.BOOKSHELF)) {
            BlockState finalState = BlockUtils.generateBlockState(
                    woodType,
                    _ -> Identifier.fromNamespaceAndPath(
                            Wizardry.MODID,
                            "bookshelf"
                    ),
                    (_, blockState) -> BlockUtils.copyPropertiesIf(state, blockState, property -> !property.equals(WizardryBlockStateProperties.WOOD_TYPE))
            );
            return new StructureTemplate.StructureBlockInfo(rawPos, finalState, rawNbt);
        } else if (state.is(WizardryBlocks.LECTERN)) {
            BlockState finalState = BlockUtils.generateBlockState(
                    woodType,
                    _ -> Identifier.fromNamespaceAndPath(
                            Wizardry.MODID,
                            "lectern"
                    ),
                    (_, blockState) -> BlockUtils.copyPropertiesIf(state, blockState, property -> !property.equals(WizardryBlockStateProperties.WOOD_TYPE))
            );
            return new StructureTemplate.StructureBlockInfo(rawPos, finalState, rawNbt);
        }
        return processedBlockInfo;
    }
}
