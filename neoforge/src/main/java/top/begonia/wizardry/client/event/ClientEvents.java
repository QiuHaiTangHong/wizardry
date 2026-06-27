package top.begonia.wizardry.client.event;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.particle.ParticleParserContextData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.client.data.parser.*;
import top.begonia.wizardry.client.gui.BookshelfScreen;
import top.begonia.wizardry.client.gui.SpellHud;
import top.begonia.wizardry.client.model.loader.EmissionModelLoader;
import top.begonia.wizardry.client.network.ClientPayloadHandler;
import top.begonia.wizardry.client.particle.impl.*;
import top.begonia.wizardry.client.render.*;
import top.begonia.wizardry.client.render.entity.*;
import top.begonia.wizardry.client.model.unbaked.item.EmissionUnbakedItemModel;
import top.begonia.wizardry.client.gui.ArcaneWorkbenchScreen;
import top.begonia.wizardry.client.model.RobeArmourModel;
import top.begonia.wizardry.client.model.SageArmourModel;
import top.begonia.wizardry.client.model.WizardArmourModel;
import top.begonia.wizardry.core.api.event.data.ClientRegisterDataParserEvent;
import top.begonia.wizardry.core.api.event.data.DataParserBefore;
import top.begonia.wizardry.core.api.event.data.RegisterParticleEvent;
import top.begonia.wizardry.core.config.ClientConfig;
import top.begonia.wizardry.core.item.ISpellCastingItem;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.util.ArmourHelper;
import top.begonia.wizardry.core.data.network.handbook.HandbookRecipesRequest;

@EventBusSubscriber(modid = Wizardry.MODID)
public class ClientEvents {

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.@NonNull RegisterLayerDefinitions event) {
        final CubeDeformation OUTER_ARMOR_DEFORMATION = new CubeDeformation(1.0F);
        final CubeDeformation INNER_ARMOR_DEFORMATION = new CubeDeformation(0.5F);
        ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> result = ImmutableMap.builder();
        ArmourHelper.ModelLayers.WIZARD.putFrom(
                WizardArmourModel
                        .createArmorMeshSetExtension(INNER_ARMOR_DEFORMATION, OUTER_ARMOR_DEFORMATION)
                        .map(((meshDefinition, equipmentSlot) -> equipmentSlot == EquipmentSlot.LEGS
                                ? LayerDefinition.create(meshDefinition, 64, 32)
                                : LayerDefinition.create(meshDefinition, 64, 64))
                        ),
                result
        );
        ArmourHelper.ModelLayers.SAGE.putFrom(
                SageArmourModel
                        .createArmorMeshSetExtension(INNER_ARMOR_DEFORMATION, OUTER_ARMOR_DEFORMATION)
                        .map(((meshDefinition, equipmentSlot) -> equipmentSlot == EquipmentSlot.LEGS
                                ? LayerDefinition.create(meshDefinition, 64, 32)
                                : LayerDefinition.create(meshDefinition, 64, 64))
                        ),
                result
        );
        ArmourHelper.ModelLayers.ROBE.putFrom(
                RobeArmourModel
                        .createArmorMeshSetExtension(INNER_ARMOR_DEFORMATION, OUTER_ARMOR_DEFORMATION)
                        .map(((meshDefinition, equipmentSlot) -> equipmentSlot == EquipmentSlot.LEGS
                                ? LayerDefinition.create(meshDefinition, 64, 32)
                                : LayerDefinition.create(meshDefinition, 64, 64))
                        ),
                result
        );
        result.build().forEach((modelLayerLocation, layerDefinition) -> event.registerLayerDefinition(modelLayerLocation, () -> layerDefinition));
    }

    @SubscribeEvent
    public static void onRegisterItemModels(@NonNull RegisterItemModelsEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "special_item"),
                EmissionUnbakedItemModel.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        if (!ClientConfig.showSpellHUD && !ClientConfig.showChargeMeter) {
            return;
        }
        Player player = Minecraft.getInstance().player;

        if (player == null || player.isSpectator()) return;
        ItemStack wand = player.getMainHandItem();
        boolean mainHand = true;

        if (!(wand.getItem() instanceof ISpellCastingItem && ((ISpellCastingItem) wand.getItem()).showSpellHUD(player, wand))) {
            wand = player.getOffhandItem();
            mainHand = false;
            if (!(wand.getItem() instanceof ISpellCastingItem && ((ISpellCastingItem) wand.getItem()).showSpellHUD(player, wand))) {
                return;
            }
        }

        GuiGraphicsExtractor guiGraphics = event.getGuiGraphics();
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        float partialTicks = event.getPartialTick().getGameTimeDeltaTicks();

        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            SpellHud.renderChargeMeter(guiGraphics, player, wand, width, height, partialTicks);
        } else if (event.getName().equals(VanillaGuiLayers.HOTBAR)) {
            SpellHud.renderSpellHUD(guiGraphics, player, wand, mainHand, width, height, partialTicks, false);
        } else if (event.getName().equals(VanillaGuiLayers.OVERLAY_MESSAGE)) {
            SpellHud.renderSpellHUD(guiGraphics, player, wand, mainHand, width, height, partialTicks, true);
        }
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.@NonNull RegisterRenderers event) {
        event.registerEntityRenderer(WizardryEntities.FIRE_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(WizardryEntities.POISON_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(WizardryEntities.SMOKE_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(WizardryEntities.SPARK_BOMB.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(WizardryEntities.MAGIC_MISSILE.get(), (context) -> new MagicArrowRenderer(
                context,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/magic_missile.png"),
                false,
                8.0,
                4.0,
                16,
                9,
                false
        ));
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
        event.registerBlockEntityRenderer(
                WizardryBlockEntities.IMBUEMENT_ALTAR.get(),
                ImbuementAltarRender::new
        );
        event.registerBlockEntityRenderer(
                WizardryBlockEntities.LECTERN.get(),
                LecternRender::new
        );
    }

    @SubscribeEvent
    public static void onRegisterModelLoaders(ModelEvent.@NonNull RegisterLoaders event) {
        event.register(EmissionModelLoader.ID, EmissionModelLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerParticleFactories(@NonNull RegisterParticleEvent event) {
        event.register(WizardryParticles.BEAM.get(), BeamParticle::new);
        event.register(WizardryParticles.BLOCK_HIGHLIGHT.get(), BlockHighlightParticle::new);
        event.register(WizardryParticles.BUFF.get(), BuffParticle::new);
        event.register(WizardryParticles.CLOUD.get(), CloudParticle::new);
        event.register(WizardryParticles.DARK_MAGIC.get(), DarkMagicParticle::new);
        event.register(WizardryParticles.DUST.get(), DustParticle::new);
        event.register(WizardryParticles.FLASH.get(), FlashParticle::new);
        event.register(WizardryParticles.GUARDIAN_BEAM.get(), GuardianBeamParticle::new);
        event.register(WizardryParticles.ICE.get(), IceParticle::new);
        event.register(WizardryParticles.LEAF.get(), LeafParticle::new);
        event.register(WizardryParticles.LIGHTNING.get(), LightningParticle::new);
        event.register(WizardryParticles.LIGHTNING_PULSE.get(), LightningPulseParticle::new);
        event.register(WizardryParticles.MAGIC_BUBBLE.get(), MagicBubbleParticle::new);
        event.register(WizardryParticles.MAGIC_FIRE.get(), MagicFlameParticle::new);
        event.register(WizardryParticles.PATH.get(), PathParticle::new);
        event.register(WizardryParticles.SCORCH.get(), ScorchParticle::new);
        event.register(WizardryParticles.SNOW.get(), SnowParticle::new);
        event.register(WizardryParticles.SPARK.get(), SparkParticle::new);
        event.register(WizardryParticles.SPARKLE.get(), SparkleParticle::new);
        event.register(WizardryParticles.SPHERE.get(), SphereParticle::new);
        event.register(WizardryParticles.VINE.get(), VineParticle::new);
    }


    @SubscribeEvent
    public static void onDataParserBefore(@NonNull DataParserBefore event) {
        event.registry(ParticleParser.PARSER_NAME, (_) -> {
            ParticleParserContextData parserContext = new ParticleParserContextData();
            ModLoader.postEvent(new RegisterParticleEvent(parserContext.getParticleHolders()));
            return parserContext;
        });
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(@NonNull RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NonNull Model<?> getHumanoidArmorModel(@NonNull ItemStack itemStack, EquipmentClientInfo.@NonNull LayerType layerType, @NonNull Model original) {
                Equippable equippable = itemStack.get(DataComponents.EQUIPPABLE);
                if (equippable != null && equippable.assetId().isPresent()) {
                    Model<?> model = ArmourHelper.getModelLayer(equippable.assetId().get(), equippable.slot());
                    return model != null ? model : original;
                }
                return original;
            }
        }, WizardryItems.ARMOUR.get());
        for (DeferredHolder<MobEffect, ? extends MobEffect> effect : WizardryMobEffects.EFFECTS.getEntries()) {
            event.registerMobEffect(WizardryPotionRender.INSTANCE, effect.get());
        }
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(@NonNull AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(Wizardry.MODID, "data_manager"), WizardryClientDataManager.getInstance());
    }

    @SubscribeEvent
    public static void onClientRegisterDataParserEvent(@NonNull ClientRegisterDataParserEvent event) {
        event.register(new BookshelfBookSettingsParser());
        event.register(new HandbookDataParser());
        event.register(new BookshelfModelParser());
        event.register(new ParticleParser());
        event.register(new SpellHubConfigParser());
        event.register(new SpellHubDescriptionParser());
    }

    @SubscribeEvent
    public static void registerScreens(@NonNull RegisterMenuScreensEvent event) {
        event.register(
                WizardryMenus.ARCANE_WORKBENCH.get(),
                ArcaneWorkbenchScreen::new
        );
        event.register(
                WizardryMenus.BOOKSHELF.get(),
                BookshelfScreen::new
        );
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientPayloadHandler.clearCache();
        HandbookData handbookData = WizardryClientDataManager.getInstance().getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "handbook"), HandbookData.class).orElse(null);
        if (handbookData != null && Minecraft.getInstance().getConnection() != null) {
            Wizardry.LOGGER.info("正在向服务端发送手册配方同步请求...");
            ClientPacketDistributor.sendToServer(new HandbookRecipesRequest(handbookData.recipes()));
        }
    }
}
