package top.begonia.wizardry.common.potion;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.begonia.wizardry.Wizardry;

public class DecayPotion extends MagicEffectPotion {
    public DecayPotion(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "decay_slowness"),
                -0.1D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
