package top.begonia.wizardry.core.worldgen.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.util.GeometryUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class WizardSpawnProcessor implements StructureProcessor {
    private static final Identifier WIZARD_ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "wizard");
    private static final Identifier EVIL_WIZARD_ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "evil_wizard");
    private Set<BlockPos> blockPosSet = new HashSet<>();
    public static final MapCodec<WizardSpawnProcessor> CODEC = MapCodec.unit(new WizardSpawnProcessor());

    @NullMarked
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(
            ServerLevelAccessor level,
            BlockPos position,
            BlockPos referencePos,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfoList,
            List<StructureTemplate.StructureBlockInfo> processedBlockInfoList,
            StructurePlaceSettings settings
    ) {
        this.blockPosSet = processedBlockInfoList.stream()
                .filter(info -> !info.state().is(Blocks.AIR))
                .map(StructureTemplate.StructureBlockInfo::pos)
                .collect(Collectors.toSet());
        return processedBlockInfoList;
    }

    @NullMarked
    public StructureTemplate.StructureEntityInfo processEntity(
            LevelReader world,
            BlockPos seedPos,
            StructureTemplate.StructureEntityInfo rawEntityInfo,
            StructureTemplate.StructureEntityInfo entityInfo,
            StructurePlaceSettings placementSettings,
            StructureTemplate template
    ) {
        CompoundTag nbt = entityInfo.nbt;
        BlockPos blockPos = entityInfo.blockPos;
        if (nbt.getString("id").isPresent()) {
            Identifier entityId = Identifier.parse(nbt.getString("id").get());
            Vec3 calculatePos = GeometryUtils.getCentre(blockPos);
            if (entityId.equals(WIZARD_ID)) {
                nbt.store("towerBlocks", BlockPos.CODEC.listOf(), this.blockPosSet.stream().toList());
                return new StructureTemplate.StructureEntityInfo(calculatePos, blockPos, nbt);
            } else if (entityId.equals(EVIL_WIZARD_ID)) {
                return new StructureTemplate.StructureEntityInfo(calculatePos, blockPos, nbt);
            }
        }
        return entityInfo;
    }

    @Override
    public @NonNull MapCodec<? extends StructureProcessor> codec() {
        return CODEC;
    }
}
