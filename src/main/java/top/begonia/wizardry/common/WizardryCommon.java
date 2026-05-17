package top.begonia.wizardry.common;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.config.CommonConfig;
import top.begonia.wizardry.common.registry.*;

@Mod(Wizardry.MODID)
@EventBusSubscriber(modid = Wizardry.MODID)
public class WizardryCommon {
    public WizardryCommon(IEventBus modEventBus, ModContainer modContainer) {
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
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    @SubscribeEvent
    static void onCommonSetup(FMLCommonSetupEvent event) {
    }
}
