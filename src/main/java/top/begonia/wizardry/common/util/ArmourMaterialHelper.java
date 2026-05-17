package top.begonia.wizardry.common.util;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.data.spell.constants.ElementEnum;
import top.begonia.wizardry.common.registry.WizardrySounds;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

public final class ArmourMaterialHelper {
    public static final ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = EquipmentAssets.ROOT_ID;
    private static final TagKey<Item> NO_REPAIR = ItemTags.create(Identifier.fromNamespaceAndPath(Wizardry.MODID, "non_repairable"));

    public static final class ModelLayer {
        public static final ModelLayerLocation ROBE = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "robe"), "main");
        public static final ModelLayerLocation SAGE = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "sage"), "main");
        public static final ModelLayerLocation WIZARD = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Wizardry.MODID, "wizard"), "main");
    }

    public static final MaterialBuilder WIZARD = new MaterialBuilder("wizard")
            .defense(2, 4, 5, 2)
            .enchantment(15)
            .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_SILK);
    //贤者
    public static final MaterialBuilder SAGE = new MaterialBuilder("sage")
            .defense(2, 5, 6, 3)
            .enchantment(25)
            .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_SAGE);
    //战斗巫师
    public static final MaterialBuilder BATTLEMAGE = new MaterialBuilder("battlemage")
            .defense(3, 8, 6, 3)
            .enchantment(15)
            .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_BATTLEMAGE)
            .toughness(1.0F);
    //术士
    public static final MaterialBuilder WARLOCK = new MaterialBuilder("warlock")
            .defense(2, 5, 4, 2)
            .enchantment(15)
            .sound(WizardrySounds.ITEM_ARMOUR_EQUIP_WARLOCK);

    public static @Nullable ModelLayerLocation getModelLayer(Optional<ResourceKey<EquipmentAsset>> assetId) {
        if (assetId.isEmpty()) {
            return null;
        }
        String path = assetId.get().identifier().getPath();
        if (path.contains("wizard")) {
            return ModelLayer.WIZARD;
        }
        else if (path.contains("sage")) {
            return ModelLayer.SAGE;
        }
        else if (path.contains("battlemage") || path.contains("warlock")) {
            return ModelLayer.ROBE;
        }
        return null;
    }

    public static class MaterialBuilder {
        private final String baseName;
        private Map<ArmorType, Integer> defense = makeDefense(0, 0, 0, 0);
        ;
        private int enchantment = 10;
        private Holder<SoundEvent> sound = SoundEvents.ARMOR_EQUIP_LEATHER;
        private float toughness = 0.0F;
        private float knockback = 0.0F;
        private int durabilityMultiplier = 15;

        public MaterialBuilder(String baseName) {
            this.baseName = baseName;
        }

        public MaterialBuilder defense(int boots, int legs, int chest, int helm) {
            this.defense = makeDefense(boots, legs, chest, helm);
            return this;
        }

        public MaterialBuilder enchantment(int enchantment) {
            this.enchantment = enchantment;
            return this;
        }

        public MaterialBuilder sound(Holder<SoundEvent> sound) {
            this.sound = sound;
            return this;
        }

        public MaterialBuilder toughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        public MaterialBuilder knockback(float knockback) {
            this.knockback = knockback;
            return this;
        }

        public MaterialBuilder durability(int multiplier) {
            this.durabilityMultiplier = multiplier;
            return this;
        }

        public ArmorMaterial build(@Nullable ElementEnum element) {
            String suffix = element == null ? "" : "_" + element.getSerializedName();
            ResourceKey<EquipmentAsset> assetId = ResourceKey.create(ROOT_ID,
                    Identifier.fromNamespaceAndPath(Wizardry.MODID, baseName + "_armour" + suffix));
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

    private static Map<ArmorType, Integer> makeDefense(int boots, int legs, int chest, int helm) {
        return Map.of(
                ArmorType.BOOTS, boots,
                ArmorType.LEGGINGS, legs,
                ArmorType.CHESTPLATE, chest,
                ArmorType.HELMET, helm,
                ArmorType.BODY, 0
        );
    }
}
