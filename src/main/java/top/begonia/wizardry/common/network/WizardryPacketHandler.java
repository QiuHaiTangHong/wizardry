package top.begonia.wizardry.common.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.ClientGlyphData;
import top.begonia.wizardry.common.network.data.GlyphDataPayload;

@EventBusSubscriber(modid = Wizardry.MODID)
public class WizardryPacketHandler {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Wizardry.MODID)
                .versioned("1.0.0");
        registrar.playToClient(
                GlyphDataPayload.TYPE,
                GlyphDataPayload.CODEC,
                WizardryPacketHandler::handleGlyphData
        );
    }

    private static void handleGlyphData(final GlyphDataPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientGlyphData.getInstance().update(payload.names(), payload.descriptions());
            Wizardry.LOGGER.info("已同步咒语乱码数据至客户端。");
        });
    }
}
