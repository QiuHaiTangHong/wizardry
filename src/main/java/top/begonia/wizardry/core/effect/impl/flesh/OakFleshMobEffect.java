package top.begonia.wizardry.core.effect.impl.flesh;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.effect.MagicMobEffect;

public class OakFleshMobEffect extends MagicMobEffect {
    public OakFleshMobEffect(MobEffectCategory category, int color) {
        super(category, color);
        if (CommonConfig.fleshSpellsCauseSlowness) {
            this.addAttributeModifier(
                    Attributes.MOVEMENT_SPEED,
                    Identifier.fromNamespaceAndPath(Wizardry.MODID, "oak_flesh_speed"),
                    -0.1,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            );
        }
        this.addAttributeModifier(
                Attributes.MAX_HEALTH,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "oak_flesh_health"),
                CommonConfig.oakFleshHealthBonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
        this.addAttributeModifier(
                Attributes.ARMOR,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "oak_flesh_armor"),
                CommonConfig.oakFleshArmorBonus,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
