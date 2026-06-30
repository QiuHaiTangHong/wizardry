package top.begonia.wizardry.core.event;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.api.event.data.RegisterDataParserEvent;
import top.begonia.wizardry.core.data.spell.WizardryServerDataManager;
import top.begonia.wizardry.core.data.player.WizardPlayerData;
import top.begonia.wizardry.core.data.SpellGlyph;
import top.begonia.wizardry.core.data.spell.parser.SpellPropertiesParser;
import top.begonia.wizardry.core.network.ServerPayloadHandler;
import top.begonia.wizardry.core.network.data.ControlInputPayload;
import top.begonia.wizardry.core.network.data.GlyphDataPayload;
import top.begonia.wizardry.core.network.data.SpellQuickAccessPayload;
import top.begonia.wizardry.core.registry.*;

@EventBusSubscriber(modid = Wizardry.MODID)
public class CommonEvent {
    @SubscribeEvent
    public static void onAddReloadListeners(@NonNull AddServerReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "server_data_manager"),
                WizardryServerDataManager.getInstance()
        );
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.@NonNull PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SpellGlyph data = SpellGlyph.get(player.level());
            if (data != null) {
                data.sync(player);
            }
            WizardPlayerData playerData = player.getData(WizardryAttachment.WIZARD_PLAYER_DATA.get());
            player.setData(WizardryAttachment.WIZARD_PLAYER_DATA.get(), playerData);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.@NonNull PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WizardPlayerData playerData = player.getData(WizardryAttachment.WIZARD_PLAYER_DATA.get());
            player.setData(WizardryAttachment.WIZARD_PLAYER_DATA.get(), playerData);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.@NonNull PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            WizardPlayerData playerData = player.getData(WizardryAttachment.WIZARD_PLAYER_DATA.get());
            player.setData(WizardryAttachment.WIZARD_PLAYER_DATA.get(), playerData);
        }
    }

    @SubscribeEvent
    public static void onCommonRegisterDataParserEvent(RegisterDataParserEvent.@NonNull CommonRegisterDataParserEvent event) {
        event.register(new SpellPropertiesParser());
    }

    @SubscribeEvent
    public static void buildSpellBookItem(@NonNull BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == WizardryCreativeTabs.GEAR.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.GEAR);
        } else if (event.getTab() == WizardryCreativeTabs.SPELLS.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.SPELLS);
        } else if (event.getTab() == WizardryCreativeTabs.WIZARDRY.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.WIZARDRY);
        }
    }

    @SubscribeEvent
    public static void register(final @NonNull RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Wizardry.MODID).versioned("1.0.0");
        registrar.playToClient(
                GlyphDataPayload.TYPE,
                GlyphDataPayload.CODEC,
                ServerPayloadHandler::handleGlyphData
        );
        registrar.playToServer(
                ControlInputPayload.TYPE,
                ControlInputPayload.CODEC,
                ServerPayloadHandler::handleControlInput
        );
        registrar.playToServer(
                SpellQuickAccessPayload.TYPE,
                SpellQuickAccessPayload.CODEC,
                ServerPayloadHandler::handleSpellQuickAccess
        );
    }
}
