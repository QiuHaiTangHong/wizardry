package top.begonia.wizardry.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.WizardryClientApi;
import top.begonia.wizardry.client.gui.config.WizardryConfigurationSectionScreen;

@Mod(value = Wizardry.MODID, dist = Dist.CLIENT)
public class WizardryClient {
    public WizardryClient(@NonNull IEventBus modEventBus, @NonNull ModContainer container) {
        container.registerExtensionPoint(
                IConfigScreenFactory.class,
                (modContainer, parent) -> new ConfigurationScreen(
                        modContainer,
                        parent,
                        WizardryConfigurationSectionScreen::new
                )
        );
        WizardryClientApi.getInstance().init(modEventBus, container);
    }
}
