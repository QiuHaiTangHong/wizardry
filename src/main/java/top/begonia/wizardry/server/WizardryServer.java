package top.begonia.wizardry.server;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import top.begonia.wizardry.Wizardry;

@Mod(value = Wizardry.MODID, dist = Dist.DEDICATED_SERVER)
@EventBusSubscriber(modid = Wizardry.MODID, value = Dist.DEDICATED_SERVER)
public class WizardryServer {

    @SubscribeEvent
    static void onServerSetup(FMLDedicatedServerSetupEvent event) {

    }
}
