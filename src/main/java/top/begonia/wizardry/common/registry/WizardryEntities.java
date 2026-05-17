package top.begonia.wizardry.common.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;

public class WizardryEntities {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(Wizardry.MODID);

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
