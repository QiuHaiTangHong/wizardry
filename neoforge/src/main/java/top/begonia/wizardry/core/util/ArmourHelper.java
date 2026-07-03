package top.begonia.wizardry.core.util;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.model.RobeArmourModel;
import top.begonia.wizardry.client.model.SageArmourModel;
import top.begonia.wizardry.client.model.WizardArmourModel;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.item.impl.ArmourUpgradeItem;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardrySounds;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import top.begonia.wizardry.client.event.ClientEvents;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 盔甲系统的核心工具类。
 * <p>
 * 负责管理盔甲材质的动态构建、客户端高级骨骼模型的缓存与延迟烘焙,
 * 以及基于 26.1.1 现代化组件（Data Components）系统的强类型装备生成。
 * </p>
 * 自 2026.6.14 替代原模组的{@code ArmourClass}
 *
 * @author 秋海棠红
 * @since 1.0.0
 */
public final class ArmourHelper {
    /**
     * 装备资源资产(EquipmentAsset)的根注册表 Key
     */
    public static final ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = EquipmentAssets.ROOT_ID;
    /**
     * 标记无法通过传统铁砧修复的物品标签
     */
    private static final TagKey<Item> NO_REPAIR = ItemTags.create(Identifier.fromNamespaceAndPath(Wizardry.MODID, "non_repairable"));
    /**
     * 客户端已烘焙实体模型的全局并发缓存.
     * 以装备资产的 {@link ResourceKey} 为键, 无缝映射到标准四件套模型集合 {@link ArmorModelSet}.
     */
    private static final Map<ResourceKey<EquipmentAsset>, ArmorModelSet<? extends HumanoidModel<?>>> BAKED_ARMOUR_REGISTRY = new ConcurrentHashMap<>();

    /**
     * 静态内部类,用于集中声明和管理盔甲在资源包中的物理渲染图层定位({@link ModelLayerLocation})
     */
    public static final class ModelLayers {
        /**
         * 存储所有合法注册的模型图层, 用于防漏校验.
         */
        private static final Set<ModelLayerLocation> ALL_MODELS = Sets.newHashSet();
        public static final ArmorModelSet<ModelLayerLocation> ROBE = registerArmorSet("robe");
        public static final ArmorModelSet<ModelLayerLocation> SAGE = registerArmorSet("sage");
        public static final ArmorModelSet<ModelLayerLocation> WIZARD = registerArmorSet("wizard");

        @Contract("_, _ -> new")
        private static @NonNull ModelLayerLocation createLocation(String model, String layer) {
            return new ModelLayerLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, model), layer);
        }

        /**
         * 为指定的盔甲模型标识符批量注册标准的头部、胸部、腿部和脚部 4 个物理渲染位置.
         *
         * @param modelId 模型的根标识符(例如 "wizard")
         * @return 包含 4 个物理槽位定位的 {@link ArmorModelSet} 实例,
         * 具体的槽位名参考{@link net.minecraft.world.item.equipment.ArmorType}
         */
        @Contract("_ -> new")
        private static @NonNull ArmorModelSet<ModelLayerLocation> registerArmorSet(String modelId) {
            return new ArmorModelSet<>(
                    register(modelId, ArmorType.HELMET.getName()),
                    register(modelId, ArmorType.CHESTPLATE.getName()),
                    register(modelId, ArmorType.LEGGINGS.getName()),
                    register(modelId, ArmorType.BOOTS.getName())
            );
        }

        private static @NonNull ModelLayerLocation register(String model, String layer) {
            ModelLayerLocation result = createLocation(model, layer);
            if (!ALL_MODELS.add(result)) {
                throw new IllegalStateException("Duplicate registration for " + result);
            } else {
                return result;
            }
        }
    }

    /**
     * <p>
     * 供客户端渲染拦截管线 ({@link IClientItemExtensions#getHumanoidArmorModel(ItemStack, EquipmentClientInfo.LayerType, Model)}) 调用的核心检索方法.
     * </p>
     *
     * <p>
     * 参考 {@link ClientEvents#onRegisterClientExtensions}
     * </p>
     *
     * @param <S>     渲染状态泛型，必须继承自 {@link HumanoidRenderState}
     * @param <A>     实体模型泛型，必须继承自 {@link HumanoidModel}
     * @param assetId 物品持有的唯一 {@link EquipmentAsset} 资源键
     * @param slot    当前正在渲染的装备槽位 {@link EquipmentSlot}
     * @return 对应的已烘焙并实例化的动态模型，若未烘焙则返回 {@code null}
     */
    @SuppressWarnings("unchecked")
    public static @Nullable <S extends HumanoidRenderState, A extends HumanoidModel<S>> A getModelLayer(
            @NonNull ResourceKey<EquipmentAsset> assetId,
            EquipmentSlot slot
    ) {
        ArmorModelSet<? extends HumanoidModel<?>> modelSet = BAKED_ARMOUR_REGISTRY.get(assetId);
        if (modelSet != null) {
            return (A) modelSet.get(slot);
        }
        return null;
    }

    /**
     * 巫术盔甲内置的基础材质枚举.
     * 包装了各自对应的属性构建器, 并通过 {@link StringRepresentable} 自动适配编解码.
     */
    public enum ArmourMaterialType implements StringRepresentable {
        WIZARD(new MaterialBuilder<>("wizard", ModelLayers.WIZARD, WizardArmourModel::new)
                .defense(2, 4, 5, 2, 0)
                .enchantment(15)
                .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_SILK)
                .magicBonus(0.1f, 0.0f)
        ),
        SAGE(new MaterialBuilder<>("sage", ModelLayers.SAGE, SageArmourModel::new)
                .defense(2, 5, 6, 3, 0)
                .enchantment(25)
                .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_SAGE)
                .magicBonus(0.2f, 0.0f)
                .upgradeItem(WizardryItems.RESPLENDENT_THREAD)
        ),
        BATTLEMAGE(new MaterialBuilder<>("battlemage", ModelLayers.ROBE, RobeArmourModel::new)
                .defense(3, 8, 6, 3, 0)
                .enchantment(15)
                .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_BATTLEMAGE).toughness(1.0F)
                .magicBonus(0.05f, 0.05f)
                .upgradeItem(WizardryItems.CRYSTAL_SILVER_PLATING)
        ),
        WARLOCK(new MaterialBuilder<>("warlock", ModelLayers.ROBE, RobeArmourModel::new)
                .defense(2, 5, 4, 2, 0)
                .enchantment(15)
                .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_WARLOCK)
                .magicBonus(0.1f, 0.1f)
                .upgradeItem(WizardryItems.ETHEREAL_CRYSTALWEAVE)
        );
        private final MaterialBuilder<?, ?> builder;

        public static final Codec<ArmourMaterialType> CODEC = StringRepresentable.fromValues(ArmourMaterialType::values);

        ArmourMaterialType(MaterialBuilder<?, ?> builder) {
            this.builder = builder;
        }

        public MaterialBuilder<?, ?> getBuilder() {
            return this.builder;
        }

        @Override
        public @NonNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    /**
     * 流式材质构建器
     * 支持泛型约束, 确保传入的模型构造器与渲染状态完全解耦, 并负责触发烘焙.
     *
     * @param <S> 该材质模型所需的 {@link HumanoidRenderState} 子类
     * @param <M> 该材质对应的 {@link HumanoidModel} 实体模型实现类
     */
    public static class MaterialBuilder<S extends HumanoidRenderState, M extends HumanoidModel<S>> {
        private final String baseName;
        private final ArmorModelSet<ModelLayerLocation> layerLocations;
        private final Function<ModelPart, M> modelConstructor;
        private Map<ArmorType, Integer> defense = makeDefense(0, 0, 0, 0, 0);
        private DeferredHolder<Item, ArmourUpgradeItem> upgradeItem = null;
        private int enchantment = 10;
        private Holder<SoundEvent> sound = SoundEvents.ARMOR_EQUIP_LEATHER;
        private float toughness = 0.0F;
        private float knockback = 0.0F;
        private int durabilityMultiplier = 15;
        private float elementalCostReduction = 0.0f;
        private float cooldownReduction = 0.0f;

        public MaterialBuilder(String baseName, ArmorModelSet<ModelLayerLocation> layerLocations, Function<ModelPart, M> modelConstructor) {
            this.baseName = baseName;
            this.layerLocations = layerLocations;
            this.modelConstructor = modelConstructor;
        }

        public MaterialBuilder<S, M> upgradeItem(DeferredHolder<Item, ArmourUpgradeItem> upgradeItem) {
            this.upgradeItem = upgradeItem;
            return this;
        }

        public MaterialBuilder<S, M> defense(int boots, int legs, int chest, int helm, int body) {
            this.defense = makeDefense(boots, legs, chest, helm, body);
            return this;
        }

        public MaterialBuilder<S, M> enchantment(int enchantment) {
            this.enchantment = enchantment;
            return this;
        }

        public MaterialBuilder<S, M> sound(Holder<SoundEvent> sound) {
            this.sound = sound;
            return this;
        }

        public MaterialBuilder<S, M> toughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        public MaterialBuilder<S, M> knockback(float knockback) {
            this.knockback = knockback;
            return this;
        }

        public MaterialBuilder<S, M> durability(int multiplier) {
            this.durabilityMultiplier = multiplier;
            return this;
        }

        /**
         * 设置巫术盔甲特有的魔法加成系数.
         *
         * @param elementalCostReduction 元素魔法消耗减免百分比(如 0.1 代表减免 10%)
         * @param cooldownReduction      技能冷却缩减百分比
         * @return 当前构建器实例以支持链式调用
         */
        public MaterialBuilder<S, M> magicBonus(float elementalCostReduction, float cooldownReduction) {
            this.elementalCostReduction = elementalCostReduction;
            this.cooldownReduction = cooldownReduction;
            return this;
        }

        public String getBaseName() {
            return this.baseName;
        }

        public float getElementalCostReduction() {
            return this.elementalCostReduction;
        }

        public float getCooldownReduction() {
            return this.cooldownReduction;
        }

        public Item getUpgradeItem() {
            return upgradeItem != null ? upgradeItem.get() : Items.AIR;
        }

        /**
         * 融合核心元素属性，在注册表编译期动态构建并返回原版的 {@link ArmorMaterial}。
         * <p>
         * <b>核心机制：</b> 此方法利用 {@code computeIfAbsent} 实现了按需延迟烘焙。当某种元素的材质
         * 首次被调用构建时，若检测到客户端实例已就绪，会立刻将 4 个物理图层一并烘焙并塞入全局模型缓存中，
         * 从而完美规避了资源包未完成加载时过早烘焙导致的空指针闪退。
         * </p>
         *
         * @param element 注入的法术核心元素枚举 {@link ElementEnum}
         * @return 最终生成的具有完整组件绑定引用的 {@link ArmorMaterial} 实例
         */
        public ArmorMaterial build(@NonNull ElementEnum element) {
            String suffix = "_" + element.getSerializedName();
            Identifier loc = Identifier.fromNamespaceAndPath(Wizardry.MODID, baseName + "_armour" + suffix);
            ResourceKey<EquipmentAsset> assetId = ResourceKey.create(ROOT_ID, loc);

            BAKED_ARMOUR_REGISTRY.computeIfAbsent(assetId, _ -> ArmorModelSet.bake(
                    this.layerLocations,
                    Minecraft.getInstance().getEntityModels(),
                    this.modelConstructor
            ));

            return new ArmorMaterial(
                    this.durabilityMultiplier,
                    this.defense,
                    this.enchantment,
                    this.sound,
                    this.toughness,
                    this.knockback,
                    NO_REPAIR,
                    assetId
            );
        }
    }
    
    private static @NonNull Map<ArmorType, Integer> makeDefense(int boots, int legs, int chest, int helm, int body) {
        Map<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, boots);
        map.put(ArmorType.LEGGINGS, legs);
        map.put(ArmorType.CHESTPLATE, chest);
        map.put(ArmorType.HELMET, helm);
        map.put(ArmorType.BODY, body);
        return map;
    }
}
