package top.begonia.wizardry.client.event;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.constants.WizardryKeyMappings;
import top.begonia.wizardry.client.data.definition.handbook.HandbookData;
import top.begonia.wizardry.client.data.manager.WizardryClientDataManager;
import top.begonia.wizardry.client.data.parser.*;
import top.begonia.wizardry.client.gui.ArcaneWorkbenchScreen;
import top.begonia.wizardry.client.gui.BookshelfScreen;
import top.begonia.wizardry.client.gui.SpellHud;
import top.begonia.wizardry.client.handle.WizardryControlHandler;
import top.begonia.wizardry.client.model.RobeArmourModel;
import top.begonia.wizardry.client.model.SageArmourModel;
import top.begonia.wizardry.client.model.WizardArmourModel;
import top.begonia.wizardry.client.model.block.RunestoneUnbakedBlockModel;
import top.begonia.wizardry.client.model.conditional.DiscoveredConditional;
import top.begonia.wizardry.client.model.conditional.FestivalConditional;
import top.begonia.wizardry.client.model.item.RunestoneUnbakedItemModel;
import top.begonia.wizardry.client.model.item.SpellBookUnbakedItemModel;
import top.begonia.wizardry.client.model.loader.WizardryModelLoader;
import top.begonia.wizardry.client.network.ClientPayloadHandler;
import top.begonia.wizardry.client.particle.impl.BeamParticle;
import top.begonia.wizardry.client.renderer.WizardryPotionRender;
import top.begonia.wizardry.client.renderer.entity.BlackHoleRender;
import top.begonia.wizardry.client.renderer.entity.BubbleRender;
import top.begonia.wizardry.client.renderer.entity.DecayRender;
import top.begonia.wizardry.client.renderer.entity.MagicArrowRenderer;
import top.begonia.wizardry.client.renderer.entity.block.ArcaneWorkbenchRender;
import top.begonia.wizardry.client.renderer.entity.block.BookshelfRender;
import top.begonia.wizardry.client.renderer.entity.block.ImbuementAltarRender;
import top.begonia.wizardry.client.renderer.entity.block.LecternRender;
import top.begonia.wizardry.client.renderer.particle.CustomParticleGroup;
import top.begonia.wizardry.client.renderer.uniform.MouseUniform;
import top.begonia.wizardry.core.api.event.data.RegisterDataParserEvent;
import top.begonia.wizardry.core.api.event.data.RegisterDelegateUnbakedModelEvent;
import top.begonia.wizardry.core.api.event.data.RegisterParticleEvent;
import top.begonia.wizardry.core.api.particle.WizardryParticle;
import top.begonia.wizardry.core.network.data.HandbookRecipesRequestPayload;
import top.begonia.wizardry.core.registry.*;
import top.begonia.wizardry.core.util.ArmourHelper;

@EventBusSubscriber(modid = Wizardry.MODID)
public class ClientEvents {
    private static MouseUniform mouseUniform;

    @SubscribeEvent
    public static void onRenderFramePre(RenderFrameEvent.Pre event) {
        if (mouseUniform == null) {
            mouseUniform = new MouseUniform();
        }
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        MouseHandler mouseHandler = minecraft.mouseHandler;
        float mouseX = (float) mouseHandler.xpos();
        float mouseY = (float) (window.getHeight() - mouseHandler.ypos());
        mouseUniform.update(mouseX, mouseY, window.getGuiScaledWidth(), window.getScreenHeight());
    }

    public static MouseUniform getMouseUniform() {
        return mouseUniform;
    }

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
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        SpellHud.onRenderGuiLayer(event);
    }

    @SubscribeEvent
    public static void onFrameGraphSetupEvent(@NonNull FrameGraphSetupEvent event) {
        FrameGraphBuilder frame = event.getFrameGrapBuilder();
        RenderTargetDescriptor screenSize = event.getRenderTargetDescriptor();
        ResourceHandle<RenderTarget> bloomTarget = frame.createInternal("bloom", screenSize);
        FramePass bloomPass = frame.addPass("render_glowing_items");
        bloomPass.readsAndWrites(bloomTarget);
        bloomPass.executes(() -> {
            Minecraft minecraft = Minecraft.getInstance();
        });
    }

    @SubscribeEvent
    public static void onRegisterParticleGroups(@NonNull RegisterParticleGroupsEvent event) {
        event.register(
                WizardryParticle.CUSTOM,
                CustomParticleGroup::new
        );
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.@NonNull RegisterRenderers event) {
        event.registerEntityRenderer(
                WizardryEntities.FIRE_BOMB.get(),
                ThrownItemRenderer::new
        );
        event.registerEntityRenderer(
                WizardryEntities.POISON_BOMB.get(),
                ThrownItemRenderer::new
        );
        event.registerEntityRenderer(
                WizardryEntities.SMOKE_BOMB.get(),
                ThrownItemRenderer::new
        );
        event.registerEntityRenderer(
                WizardryEntities.SPARK_BOMB.get(),
                ThrownItemRenderer::new
        );
        event.registerEntityRenderer(
                WizardryEntities.MAGIC_MISSILE.get(),
                (context) -> new MagicArrowRenderer(
                        context,
                        Identifier.fromNamespaceAndPath(Wizardry.MODID, "textures/entity/magic_missile.png"),
                        false,
                        8.0F,
                        4.0,
                        16,
                        9,
                        false
                )
        );
        event.registerEntityRenderer(
                WizardryEntities.DECAY.get(),
                DecayRender::new
        );
        event.registerEntityRenderer(
                WizardryEntities.BUBBLE.get(),
                BubbleRender::new
        );
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
        event.registerEntityRenderer(
                WizardryEntities.BLACK_HOLE.get(),
                BlackHoleRender::new
        );
        event.registerEntityRenderer(
                WizardryEntities.ZOMBIE_MINION.get(),
                ZombieRenderer::new
        );
        event.registerEntityRenderer(
                WizardryEntities.WITHER_SKELETON_MINION.get(),
                WitherSkeletonRenderer::new
        );
    }

    @SubscribeEvent
    public static void onRegisterConditionalItemModelProperty(@NonNull RegisterConditionalItemModelPropertyEvent event) {
        event.register(
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "festive"),
                FestivalConditional.CODEC
        );
        event.register(
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "discovered"),
                DiscoveredConditional.CODEC
        );
    }

    @SubscribeEvent
    public static void onRegisterModelLoaders(ModelEvent.@NonNull RegisterLoaders event) {
        event.register(WizardryModelLoader.ID, new WizardryModelLoader());
    }

    @SubscribeEvent
    public static void onRegisterItemModels(@NonNull RegisterItemModelsEvent event) {
        event.register(
                RunestoneUnbakedItemModel.ID,
                RunestoneUnbakedItemModel.MAP_CODEC
        );
        event.register(
                SpellBookUnbakedItemModel.ID,
                SpellBookUnbakedItemModel.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void onRegisterDelegateUnbakedModel(@NonNull RegisterDelegateUnbakedModelEvent event) {
        event.register(Identifier.fromNamespaceAndPath(Wizardry.MODID, "runestone_model"), RunestoneUnbakedBlockModel::new);
    }

    @SubscribeEvent
    public static void onRegisterParticle(@NonNull RegisterParticleEvent event) {
        event.register(WizardryParticles.BEAM.get(), (particleResourceAccessor, clientLevel, options, x, y, z) -> {
            return new BeamParticle(clientLevel, options, x, y, z);
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
            event.registerMobEffect(WizardryPotionRender.getInstance(), effect.get());
        }
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(@NonNull AddClientReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(Wizardry.MODID, "data_manager"), WizardryClientDataManager.getInstance());
    }

    @SubscribeEvent
    public static void onClientRegisterDataParserEvent(RegisterDataParserEvent.@NonNull ClientRegisterDataParserEvent event) {
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
            ClientPacketDistributor.sendToServer(new HandbookRecipesRequestPayload(handbookData.recipes()));
        }
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        WizardryKeyMappings.register(event);
    }

    @SubscribeEvent
    public static void onMouseScrollingEvent(InputEvent.MouseScrollingEvent event) {
        WizardryControlHandler.onMouseScrollingEvent(event);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        WizardryControlHandler.onClientTickEvent(event);
    }

    @SubscribeEvent
    public static void onPlayerTickPreEvent(PlayerTickEvent.@NonNull Pre event) {
        SpellHud.onPlayerTickPreEvent(event);
    }
}
