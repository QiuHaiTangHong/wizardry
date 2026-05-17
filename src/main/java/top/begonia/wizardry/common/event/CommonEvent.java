package top.begonia.wizardry.common.event;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.data.player.WizardPlayerData;
import top.begonia.wizardry.common.data.spell.part.SpellGlyphData;
import top.begonia.wizardry.common.data.spell.SpellPropertiesLoader;
import top.begonia.wizardry.common.registry.*;

@EventBusSubscriber(modid = Wizardry.MODID)
public class CommonEvent {
    @SubscribeEvent
    public static void onAddReloadListeners(@NonNull AddServerReloadListenersEvent event) {
        SpellPropertiesLoader loader = new SpellPropertiesLoader(event.getRegistryAccess());
        event.addListener(
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "spell_properties_loader"),
                loader
        );
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.@NonNull PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SpellGlyphData data = SpellGlyphData.get(player.level());
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
    public static void buildSpellBookItem(@NonNull BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == WizardryCreativeTabs.GEAR.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.GEAR);
        } else if (event.getTab() == WizardryCreativeTabs.SPELLS.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.SPELLS);
        } else if (event.getTab() == WizardryCreativeTabs.WIZARDRY.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.WIZARDRY);
        }
    }
}
