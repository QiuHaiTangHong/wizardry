package top.begonia.wizardry.core;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.util.NativeMethodRouter;
import top.begonia.wizardry.core.util.WandHelper;

@Mod(Wizardry.MODID)
@EventBusSubscriber(modid = Wizardry.MODID)
public class WizardryCommon {
    public WizardryCommon(IEventBus modEventBus, @NonNull ModContainer modContainer) {
        WizardrySounds.register(modEventBus);
        WizardryBlocks.register(modEventBus);
        WizardryItems.register(modEventBus);
        WizardryCreativeTabs.register(modEventBus);
        WizardryBlockEntities.register(modEventBus);
        WizardryMenus.register(modEventBus);
        WizardrySpells.register(modEventBus);
        WizardryComponents.register(modEventBus);
        WizardryAttachment.register(modEventBus);
        WizardryMobEffects.register(modEventBus);
        WizardryNetworkPackage.register(modEventBus);
        WizardryParticles.register(modEventBus);
        WizardryEntities.register(modEventBus);
        WizardryLoots.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        modEventBus.addListener(this::commonSetup);
        NativeMethodRouter.SAY_HELLO.get();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(WandHelper::populateUpgradeMap);
    }
}
