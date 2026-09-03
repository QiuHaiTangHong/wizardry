package top.begonia.wizardry.client;

import com.mojang.blaze3d.opengl.GlProgram;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.event.data.RegisterParticleEvent;
import top.begonia.wizardry.api.particle.manager.WizardryParticleManager;
import top.begonia.wizardry.api.particle.manager.WizardryParticleProvider;
import top.begonia.wizardry.api.particle.options.IParticleOptionsExtension;
import top.begonia.wizardry.api.particle.type.ParticleTypeExtension;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;

import java.util.HashMap;
import java.util.Map;

@Mod(value = Wizardry.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Wizardry.MODID, value = Dist.CLIENT)
public class WizardryClient {
    public static WizardryParticleManager particleManager;
    public WizardryClient(@NonNull IEventBus modEventBus, @NonNull ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(final @NonNull FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            WizardryClientDataManager.getInstance().fireRegisterEvents();
            GlProgram.BUILT_IN_UNIFORMS.add("MouseInfo");
            Map<ParticleTypeExtension<?>, WizardryParticleProvider<? extends IParticleOptionsExtension>> particleProviders = new HashMap<>();
            NeoForge.EVENT_BUS.post(new RegisterParticleEvent(particleProviders));
            WizardryClient.particleManager = new WizardryParticleManager(particleProviders);
            Wizardry.LOGGER.info("aaa");
        });
    }
}
