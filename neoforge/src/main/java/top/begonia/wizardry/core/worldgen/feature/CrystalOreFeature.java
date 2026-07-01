package top.begonia.wizardry.core.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.worldgen.feature.configuration.RandomDistributionConfiguration;

import java.util.List;

public class CrystalOreFeature extends Feature<RandomDistributionConfiguration> {
    public CrystalOreFeature(Codec<RandomDistributionConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NonNull FeaturePlaceContext<RandomDistributionConfiguration> context) {
        WorldGenLevel level = context.level();
        String currentDimension = level.getLevel().dimension().identifier().toString();
        if (!ServerConfig.oreDimensions.contains(currentDimension)) {
            return false;
        }

        BlockPos origin = context.origin();
        RandomSource random = context.random();
        RandomDistributionConfiguration config = context.config();
        BlockState oreState = config.toPlace().getState(level, random, origin);
        OreConfiguration.TargetBlockState target = OreConfiguration.target(
                new BlockMatchTest(Blocks.STONE),
                oreState
        );
        OreConfiguration oreConfig = new OreConfiguration(List.of(target), config.groupSize());
        int diffY = Math.max(1, config.maxY() - config.minY());
        for (int x = 0; x < config.chancesToSpawn(); x++) {
            int posX = origin.getX() + random.nextInt(16);
            int posZ = origin.getZ() + random.nextInt(16);
            int posY = config.minY() + random.nextInt(diffY);
            BlockPos targetPos = new BlockPos(posX, posY, posZ);
            if (level.isOutsideBuildHeight(targetPos)) {
                continue;
            }
            Feature.ORE.place(new FeaturePlaceContext<>(context.topFeature(), level, context.chunkGenerator(), random, targetPos, oreConfig));
        }

        return true;
    }
}
