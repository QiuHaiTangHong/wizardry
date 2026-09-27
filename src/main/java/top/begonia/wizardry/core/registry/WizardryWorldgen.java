package top.begonia.wizardry.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class WizardryWorldgen {
    public static final DeferredRegister<Feature> FEATURE_TYPES =
            DeferredRegister.create(Registries.FEATURE, "wizardry");

    public static void register(IEventBus eventBus) {
        FEATURE_TYPES.register(eventBus);
    }
}
