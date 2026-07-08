package top.begonia.wizardry.core.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.network.ClientPayloadHandler;
import top.begonia.wizardry.core.data.network.handbook.HandbookRecipesRequest;
import top.begonia.wizardry.core.data.network.handbook.HandbookRecipesResult;
import top.begonia.wizardry.core.network.ServerPayloadHandler;
import top.begonia.wizardry.core.network.data.SyncSlotPayload;

public final class WizardryNetworkPackage {
    private static void registerNetworkPackage(final @NonNull RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Wizardry.MODID).versioned("1.0.0");

        registrar.playToServer(
                HandbookRecipesRequest.TYPE,
                HandbookRecipesRequest.STREAM_CODEC,
                ServerPayloadHandler::handleRequest
        );

        registrar.playToClient(
                HandbookRecipesResult.TYPE,
                HandbookRecipesResult.STREAM_CODEC,
                ClientPayloadHandler::handleHandbookRecipesResult
        );

        registrar.playToClient(
                SyncSlotPayload.TYPE,
                SyncSlotPayload.STREAM_CODEC,
                ClientPayloadHandler::handleItemResourceHandlerPayload
        );
    }

    public static void register(@NonNull IEventBus modEventBus) {
        modEventBus.addListener(WizardryNetworkPackage::registerNetworkPackage);
    }
}
