package top.begonia.wizardry.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.WizardryDataManager;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.client.gui.BookshelfScreen;
import top.begonia.wizardry.client.network.ClientPayloadHandler;
import top.begonia.wizardry.client.render.ArcaneWorkbenchRender;
import top.begonia.wizardry.client.render.BookshelfRender;
import top.begonia.wizardry.client.render.WizardryPotionRender;
import top.begonia.wizardry.client.gui.ArcaneWorkbenchScreen;
import top.begonia.wizardry.client.model.RobeArmourModel;
import top.begonia.wizardry.client.model.SageArmourModel;
import top.begonia.wizardry.client.model.WizardArmourModel;
import top.begonia.wizardry.client.render.WizardryArmorRenderer;
import top.begonia.wizardry.common.registry.WizardryBlockEntities;
import top.begonia.wizardry.common.registry.WizardryItems;
import top.begonia.wizardry.common.registry.WizardryMenus;
import top.begonia.wizardry.common.registry.WizardryMobEffects;
import top.begonia.wizardry.common.util.ArmourMaterialHelper;
import top.begonia.wizardry.server.data.handbook.HandbookRecipesRequest;

@EventBusSubscriber(modid = Wizardry.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ArmourMaterialHelper.ModelLayer.WIZARD,
                () -> WizardArmourModel.createLayerDefinition(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(ArmourMaterialHelper.ModelLayer.SAGE,
                () -> SageArmourModel.createLayerDefinition(new CubeDeformation(0.5F)));
        event.registerLayerDefinition(ArmourMaterialHelper.ModelLayer.ROBE,
                () -> RobeArmourModel.createLayerDefinition(new CubeDeformation(0.5F)));
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        for (DeferredItem<? extends Item> item : WizardryItems.WIZARD_ARMOUR_ITEMS) {
            event.registerItem(new WizardryArmorRenderer(), item.get());
        }
        for (DeferredHolder<MobEffect, ? extends MobEffect> effect : WizardryMobEffects.EFFECTS.getEntries()) {
            event.registerMobEffect(WizardryPotionRender.INSTANCE, effect.get());
        }
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(@NonNull AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(Wizardry.MODID, "data_manager"), WizardryDataManager.INSTANCE);
    }

    @SubscribeEvent
    public static void registerScreens(@NonNull RegisterMenuScreensEvent event) {
        event.register(WizardryMenus.ARCANE_WORKBENCH.get(), ArcaneWorkbenchScreen::new);
        event.register(WizardryMenus.BOOKSHELF.get(), BookshelfScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.@NonNull RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                WizardryBlockEntities.ARCANE_WORKBENCH.get(),
                ArcaneWorkbenchRender::new
        );
        event.registerBlockEntityRenderer(
                WizardryBlockEntities.BOOKSHELF.get(),
                BookshelfRender::new
        );
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPayloadHandler.clearCache();
        HandbookData handbookData = WizardryDataManager.getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook"), HandbookData.class).orElse(null);
        if (handbookData != null && Minecraft.getInstance().getConnection() != null) {
            Wizardry.LOGGER.info("正在向服务端发送手册配方同步请求...");
            ClientPacketDistributor.sendToServer(new HandbookRecipesRequest(handbookData.recipes()));
        }
    }
}
