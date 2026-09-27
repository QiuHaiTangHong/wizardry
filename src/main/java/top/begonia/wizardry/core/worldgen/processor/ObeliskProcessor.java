package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.api.constants.ElementEnum;
import top.begonia.wizardry.api.constants.WizardryBlockStateProperties;
import top.begonia.wizardry.core.registry.WizardryBlocks;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.util.BlockUtils;

public record ObeliskProcessor(
) implements StructureProcessor {
    public static final MapCodec<ObeliskProcessor> CODEC = MapCodec.unit(new ObeliskProcessor());

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
        RandomSource fixedRandom = settings.getRandom(referencePos);
        int elementIndex = 1 + fixedRandom.nextInt(ElementEnum.values().length - 1);
        final ElementEnum element = ElementEnum.values()[elementIndex];
        BlockState rawState = processedBlockInfo.state();
        CompoundTag nbt = processedBlockInfo.nbt();
        BlockPos pos = processedBlockInfo.pos();
        if (rawState.is(WizardryBlocks.RUNESTONE)) {
            BlockState randomElementRunestone = WizardryBlocks.RUNESTONE.get()
                    .defaultBlockState()
                    .trySetValue(WizardryBlockStateProperties.ELEMENT, element);
            BlockUtils.copyPropertiesIf(rawState, randomElementRunestone, property -> property != WizardryBlockStateProperties.ELEMENT);
            return new StructureTemplate.StructureBlockInfo(pos, randomElementRunestone, nbt);
        } else if (nbt != null && nbt.getString("target").isPresent()) {
            Identifier targetIdentifier = Identifier.parse(nbt.getString("target").get());
            if (BuiltInRegistries.BLOCK.get(targetIdentifier).isPresent()) {
                BlockState targetState = BuiltInRegistries.BLOCK.get(targetIdentifier).get().value().defaultBlockState();
                CompoundTag spawnerTag = new CompoundTag();
                CompoundTag spawnData = new CompoundTag();
                CompoundTag entity = new CompoundTag();
                entity.putString("id", EntityType.getKey(WizardryEntities.REMNANT.get()).toString());
                entity.putInt("Element", elementIndex);
                spawnData.put("entity", entity);
                spawnerTag.put("SpawnData", spawnData);
                return new StructureTemplate.StructureBlockInfo(pos, targetState, spawnerTag);
            }
        }
        return processedBlockInfo;
    }
}
