package top.begonia.wizardry.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

@Mod(value = Wizardry.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Wizardry.MODID, value = Dist.CLIENT)
public class WizardryClient {
    public WizardryClient(@NonNull ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
