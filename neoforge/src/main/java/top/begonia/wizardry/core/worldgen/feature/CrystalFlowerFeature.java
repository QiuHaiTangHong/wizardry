package top.begonia.wizardry.core.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.registry.WizardryBlocks;
import top.begonia.wizardry.core.worldgen.feature.configuration.RandomDistributionConfiguration;

public class CrystalFlowerFeature extends Feature<RandomDistributionConfiguration> {
    public CrystalFlowerFeature(Codec<RandomDistributionConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NonNull FeaturePlaceContext<RandomDistributionConfiguration> featurePlaceContext) {
        WorldGenLevel level = featurePlaceContext.level();
        String currentDimension = level.getLevel().dimension().identifier().toString();
        if (!ServerConfig.flowerDimensions.contains(currentDimension)) {
            return false;
        }

        BlockPos origin = featurePlaceContext.origin();
        RandomSource random = featurePlaceContext.random();
        RandomDistributionConfiguration config = featurePlaceContext.config();
        int chancesToSpawn = config.chancesToSpawn();
        int groupSize = config.groupSize();
        boolean isNether = currentDimension.contains("the_nether");
        BlockState state = WizardryBlocks.CRYSTAL_FLOWER.get().defaultBlockState();
        for (int i = 0; i < chancesToSpawn; i++) {
            int randPosX = origin.getX() + random.nextInt(16);
            int randPosY = random.nextInt(256);
            int randPosZ = origin.getZ() + random.nextInt(16);

            for (int l = 0; l < groupSize; ++l) {
                int i1 = randPosX + random.nextInt(8) - random.nextInt(8);
                int j1 = randPosY + random.nextInt(4) - random.nextInt(4);
                int k1 = randPosZ + random.nextInt(8) - random.nextInt(8);

                BlockPos pos = new BlockPos(i1, j1, k1);
                if (level.hasChunkAt(pos) && level.isEmptyBlock(pos) && (!isNether || j1 < 127) && state.canSurvive(level, pos)) {
                    level.setBlock(pos, state, 1);
                }
            }
        }
        return true;
    }
}
