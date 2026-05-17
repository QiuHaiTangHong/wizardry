package top.begonia.wizardry.common.potion;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.config.CommonConfig;

public class IronFleshPotion extends MagicEffectPotion {
    public IronFleshPotion(MobEffectCategory category, int color) {
        super(category, color);
        if (CommonConfig.fleshSpellsCauseSlowness) {
            this.addAttributeModifier(
                    Attributes.MOVEMENT_SPEED,
                    Identifier.fromNamespaceAndPath(Wizardry.MODID, "effect.iron_flesh.movement_speed"),
                    -0.1D,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );
        }

        this.addAttributeModifier(
                Attributes.KNOCKBACK_RESISTANCE,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "effect.iron_flesh.knockback_resistance"),
                0.3D,
                AttributeModifier.Operation.ADD_VALUE
        );

        this.addAttributeModifier(
                Attributes.ARMOR,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "effect.iron_flesh.armor"),
                CommonConfig.ironFleshArmorBonus,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
