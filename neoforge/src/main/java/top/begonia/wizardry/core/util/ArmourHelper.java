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
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantable;
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
import top.begonia.wizardry.core.item.impl.WizardArmourItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
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
                .defense(2, 4, 5, 2)
                .enchantment(15)
                .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_SILK)
                .magicBonus(0.1f, 0.0f)
        ),
        SAGE(new MaterialBuilder<>("sage", ModelLayers.SAGE, SageArmourModel::new)
                .defense(2, 5, 6, 3)
                .enchantment(25)
                .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_SAGE)
                .magicBonus(0.2f, 0.0f)
                .upgradeItem(WizardryItems.RESPLENDENT_THREAD)
        ),
        BATTLEMAGE(new MaterialBuilder<>("battlemage", ModelLayers.ROBE, RobeArmourModel::new)
                .defense(3, 8, 6, 3)
                .enchantment(15)
                .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_BATTLEMAGE).toughness(1.0F)
                .magicBonus(0.05f, 0.05f)
                .upgradeItem(WizardryItems.CRYSTAL_SILVER_PLATING)
        ),
        WARLOCK(new MaterialBuilder<>("warlock", ModelLayers.ROBE, RobeArmourModel::new)
                .defense(2, 5, 4, 2)
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
        private Map<ArmorType, Integer> defense = makeDefense(0, 0, 0, 0);
        DeferredHolder<Item, ArmourUpgradeItem> upgradeItem = null;
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

        public MaterialBuilder<S, M> defense(int boots, int legs, int chest, int helm) {
            this.defense = makeDefense(boots, legs, chest, helm);
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

    private static @NonNull Map<ArmorType, Integer> makeDefense(int boots, int legs, int chest, int helm) {
        Map<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, boots);
        map.put(ArmorType.LEGGINGS, legs);
        map.put(ArmorType.CHESTPLATE, chest);
        map.put(ArmorType.HELMET, helm);
        map.put(ArmorType.BODY, 0);
        return map;
    }

    /**
     * 装备生成工厂。
     * <p>
     * 遵循 26.1.1 的现代化全面数据组件化标准。不再依赖硬编码，而是将材质基类、子元素、最大耐久度、
     * 可附魔状态、属性修饰符以及最重要的 {@link Equippable} 可穿戴组件(包含渲染资产 ID 映射)
     * 统一作为独立 Component 编译写入生成的 {@link ItemStack} 中.
     * </p>
     *
     * @param armourItem         模组盔甲物品基类实例
     * @param element            装备绑定的元素核心属性
     * @param armourMaterialType 装备所属的法袍骨骼材质分类
     * @param armorType          装备的具体槽位物理形态(HELMET, CHESTPLATE 等)
     * @return 包含完整数据驱动上下文组件、可直接给予玩家的 {@link ItemStack} 实例
     */
    public static @NonNull ItemStack generateArmour(
            WizardArmourItem armourItem,
            ElementEnum element,
            ArmourHelper.@NonNull ArmourMaterialType armourMaterialType,
            @NonNull ArmorType armorType
    ) {
        ItemStack itemStack = new ItemStack(armourItem);
        ArmorMaterial armorMaterial = armourMaterialType.getBuilder().build(element);
        itemStack.set(WizardryComponents.ARMOR_MATERIAL_TYPE, armourMaterialType);
        itemStack.set(WizardryComponents.ARMOR_TYPE, armorType);
        itemStack.set(WizardryComponents.ELEMENT, element);
        itemStack.set(DataComponents.MAX_DAMAGE, armorType.getDurability(armorMaterial.durability()));
        itemStack.set(DataComponents.MAX_STACK_SIZE, 1);
        itemStack.set(DataComponents.DAMAGE, 0);
        itemStack.set(DataComponents.ENCHANTABLE, new Enchantable(armorMaterial.enchantmentValue()));
        itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, armorMaterial.createAttributes(armorType));
        itemStack.set(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).setEquipSound(armorMaterial.equipSound()).setAsset(armorMaterial.assetId()).setAllowedEntities(EntityTypes.PLAYER).build());
        return itemStack;
    }
}
