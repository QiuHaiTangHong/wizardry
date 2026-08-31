package top.begonia.wizardry.core;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.network.ClientPayloadHandler;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.data.spell.WizardryServerDataManager;
import top.begonia.wizardry.core.network.ServerPayloadHandler;
import top.begonia.wizardry.core.network.data.*;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.natives.MathUtils;

@Mod(Wizardry.MODID)
public class WizardryCore {
    public WizardryCore(@NonNull IEventBus modEventBus, @NonNull ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayload);
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
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
        WizardryParticles.register(modEventBus);
        WizardryEntities.register(modEventBus);
        WizardryLoots.register(modEventBus);
        WizardryAdvancementTriggers.register(modEventBus);
        WizardryWorldgen.register(modEventBus);
        WizardryEntityDataSerializers.register(modEventBus);
        Wizardry.onInit(modContainer);
        Wizardry.LOGGER.info("Fib(10)={}", MathUtils.fibonacci(10));
    }

    private void commonSetup(final @NonNull FMLCommonSetupEvent event) {
        event.enqueueWork(() -> WizardryServerDataManager.getInstance().fireRegisterEvents());
    }

    private void registerPayload(final @NonNull RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Wizardry.MODID);
        // 服务端
        registrar.versioned(ControlInputPayload.VERSION).playToServer(
                ControlInputPayload.TYPE,
                ControlInputPayload.CODEC,
                ServerPayloadHandler::handleControlInput
        );
        registrar.versioned(HandbookRecipesRequestPayload.VERSION).playToServer(
                HandbookRecipesRequestPayload.TYPE,
                HandbookRecipesRequestPayload.STREAM_CODEC,
                ServerPayloadHandler::handleRequest
        );
        registrar.versioned(SpellQuickAccessPayload.VERSION).playToServer(
                SpellQuickAccessPayload.TYPE,
                SpellQuickAccessPayload.STREAM_CODEC,
                ServerPayloadHandler::handleSpellQuickAccessPayload
        );

        // 客户端
        registrar.versioned(HandbookRecipesResultPayload.VERSION).playToClient(
                HandbookRecipesResultPayload.TYPE,
                HandbookRecipesResultPayload.STREAM_CODEC,
                ClientPayloadHandler::handleHandbookRecipesResultPayload
        );
        registrar.versioned(SyncSlotPayload.VERSION).playToServer(
                SyncSlotPayload.TYPE,
                SyncSlotPayload.STREAM_CODEC,
                ClientPayloadHandler::handleSyncSlotPayload
        );
        registrar.versioned(GlyphDataPayload.VERSION).playToClient(
                GlyphDataPayload.TYPE,
                GlyphDataPayload.STREAM_CODEC,
                ClientPayloadHandler::handleGlyphDataPayload
        );
    }
}
