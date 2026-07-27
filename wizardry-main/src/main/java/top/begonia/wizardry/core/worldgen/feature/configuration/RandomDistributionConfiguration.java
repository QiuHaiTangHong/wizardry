package top.begonia.wizardry.core.worldgen.feature.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record RandomDistributionConfiguration(
        BlockStateProvider toPlace,
        int chancesToSpawn,
        int groupSize,
        int minY,
        int maxY,
        boolean scheduleTick
) implements FeatureConfiguration {

    public static final Codec<RandomDistributionConfiguration> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockStateProvider.CODEC.fieldOf("to_place").forGetter(RandomDistributionConfiguration::toPlace),
                    Codec.INT.fieldOf("chances_to_spawn").forGetter(RandomDistributionConfiguration::chancesToSpawn),
                    Codec.INT.optionalFieldOf("group_size", 1).forGetter(RandomDistributionConfiguration::groupSize),
                    Codec.INT.optionalFieldOf("min_y", 1).forGetter(RandomDistributionConfiguration::minY),
                    Codec.INT.optionalFieldOf("max_y", 255).forGetter(RandomDistributionConfiguration::maxY),
                    Codec.BOOL.optionalFieldOf("schedule_tick", false).forGetter(RandomDistributionConfiguration::scheduleTick)
            ).apply(instance, RandomDistributionConfiguration::new)
    );
}
