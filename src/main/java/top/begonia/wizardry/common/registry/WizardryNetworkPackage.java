package top.begonia.wizardry.common.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.network.ClientPayloadHandler;
import top.begonia.wizardry.server.data.handbook.HandbookRecipesRequest;
import top.begonia.wizardry.server.data.handbook.HandbookRecipesResult;
import top.begonia.wizardry.server.network.ServerPayloadHandler;

public final class WizardryNetworkPackage {
    private static void registerNetworkPackage(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Wizardry.MODID).versioned("1.0.0");

        registrar.playToServer(
                HandbookRecipesRequest.TYPE,
                HandbookRecipesRequest.STREAM_CODEC,
                ServerPayloadHandler::handleRequest
        );

        registrar.playToClient(
                HandbookRecipesResult.TYPE,
                HandbookRecipesResult.STREAM_CODEC,
                ClientPayloadHandler::handleResult
        );
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(WizardryNetworkPackage::registerNetworkPackage);
    }
}
