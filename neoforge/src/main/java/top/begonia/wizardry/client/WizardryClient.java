package top.begonia.wizardry.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;

@Mod(value = Wizardry.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Wizardry.MODID, value = Dist.CLIENT)
public class WizardryClient {
    public WizardryClient(IEventBus modEventBus, @NonNull ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(final @NonNull FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            WizardryClientDataManager.getInstance().fireRegisterEvents();
        });
    }
}
