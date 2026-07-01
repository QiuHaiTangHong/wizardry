package top.begonia.wizardry.core.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.core.worldgen.feature.CrystalFlowerFeature;
import top.begonia.wizardry.core.worldgen.feature.CrystalOreFeature;
import top.begonia.wizardry.core.worldgen.feature.configuration.RandomDistributionConfiguration;

public final class WizardryWorldgen {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, "wizardry");
    public static final DeferredHolder<Feature<?>, Feature<RandomDistributionConfiguration>> CRYSTAL_FLOWER =
            FEATURES.register("crystal_flower", () -> new CrystalFlowerFeature(RandomDistributionConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, Feature<RandomDistributionConfiguration>> CRYSTAL_ORE =
            FEATURES.register("crystal_ore", () -> new CrystalOreFeature(RandomDistributionConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
