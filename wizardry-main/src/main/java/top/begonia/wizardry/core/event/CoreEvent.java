package top.begonia.wizardry.core.event;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.event.data.RegisterDataParserEvent;
import top.begonia.wizardry.core.commond.DebugCommond;
import top.begonia.wizardry.core.data.SpellGlyph;
import top.begonia.wizardry.core.data.constant.parser.CurrencyParser;
import top.begonia.wizardry.core.data.player.WizardPlayerData;
import top.begonia.wizardry.core.data.constant.WizardryServerDataManager;
import top.begonia.wizardry.core.data.constant.parser.SpellPropertiesParser;
import top.begonia.wizardry.core.effect.impl.DecayMobEffect;
import top.begonia.wizardry.core.entity.construct.BubbleEntity;
import top.begonia.wizardry.core.entity.living.wizard.impl.WizardEntity;
import top.begonia.wizardry.core.registry.WizardryAttachment;
import top.begonia.wizardry.core.registry.WizardryCreativeTabs;
import top.begonia.wizardry.core.registry.WizardryEntities;
import top.begonia.wizardry.core.util.DamageSafetyChecker;

@EventBusSubscriber(modid = Wizardry.MODID)
public class CoreEvent {
    @SubscribeEvent
    public static void onAddReloadListeners(@NonNull AddServerReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "server_data_manager"),
                WizardryServerDataManager.getInstance()
        );
    }

    /**
     * 在玩家登入时添加逻辑
     * @param event 玩家登入事件
     * @see PlayerEvent.PlayerLoggedInEvent
     */
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

    /**
     * 注册自定义命令
     * @param event 注册命令事件
     * @see RegisterCommandsEvent
     */
    @SubscribeEvent
    public static void onRegisterCommands(@NonNull RegisterCommandsEvent event) {
        DebugCommond.register(event.getDispatcher());
    }

    /**
     * 当方块被玩家破坏时触发
     * @param event 方块破坏事件
     * @see BreakBlockEvent
     */
    @SubscribeEvent
    public static void onBlockBreakEvent(BreakBlockEvent event) {
        WizardEntity.onBlockBreakEvent(event);
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

    /**
     * 注册数据解析器
     * @param event Wizardry的数据解析器注册事件
     * @see RegisterDataParserEvent.CommonRegisterDataParserEvent
     */
    @SubscribeEvent
    public static void onCommonRegisterDataParserEvent(RegisterDataParserEvent.@NonNull CommonRegisterDataParserEvent event) {
        event.register(new SpellPropertiesParser());
        event.register(new CurrencyParser());
    }

    /**
     * 创造模式标签页构造时触发
     * @param event 构建创造模式标签页事件
     * @see BuildCreativeModeTabContentsEvent
     */
    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(@NonNull BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == WizardryCreativeTabs.GEAR.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.GEAR);
        } else if (event.getTab() == WizardryCreativeTabs.SPELLS.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.SPELLS);
        } else if (event.getTab() == WizardryCreativeTabs.WIZARDRY.get()) {
            WizardryCreativeTabs.addItemsToEvent(event, WizardryCreativeTabs.TabsEnum.WIZARDRY);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamageEvent(@NonNull LivingIncomingDamageEvent event) {
        BubbleEntity.onLivingIncomingDamageEvent(event);
    }

    @SubscribeEvent
    public static void onEntityTickEventPre(EntityTickEvent.@NonNull Pre event) {
        DecayMobEffect.onEntityTickEventPre(event);
    }

    @SubscribeEvent
    public static void onTagsUpdated(@NonNull TagsUpdatedEvent event) {
        DamageSafetyChecker.updateVanillaDamages(event.getRegistries());
    }

    /**
     * 为实体创建默认属性
     * @param event 实体属性创建事件
     * @see EntityAttributeCreationEvent
     */
    @SubscribeEvent
    public static void onEntityAttributeCreation(@NonNull EntityAttributeCreationEvent event) {
        WizardryEntities.creationDefaultEntityAttribute(event);
    }
}
