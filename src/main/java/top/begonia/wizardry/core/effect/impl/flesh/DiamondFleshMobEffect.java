package top.begonia.wizardry.core.effect.impl.flesh;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.effect.MagicMobEffect;

public class DiamondFleshMobEffect extends MagicMobEffect {
    public DiamondFleshMobEffect(MobEffectCategory category, int color) {
        super(category, color);
        if (CommonConfig.fleshSpellsCauseSlowness) {
            this.addAttributeModifier(
                    Attributes.MOVEMENT_SPEED,
                    Identifier.fromNamespaceAndPath(Wizardry.MODID, "diamond_flesh_speed"),
                    -0.1,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
        }
        this.addAttributeModifier(
                Attributes.ARMOR_TOUGHNESS,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "diamond_flesh_toughness"),
                CommonConfig.diamondFleshArmorToughnessBonus,
                AttributeModifier.Operation.ADD_VALUE
        );
        this.addAttributeModifier(
                Attributes.ARMOR,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "diamond_flesh_armor"),
                CommonConfig.diamondFleshArmorBonus,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
