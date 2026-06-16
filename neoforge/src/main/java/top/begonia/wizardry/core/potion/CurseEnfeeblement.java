package top.begonia.wizardry.core.potion;

import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.registry.WizardryMobEffects;

import java.lang.reflect.Field;

@EventBusSubscriber(modid = Wizardry.MODID)
public class CurseEnfeeblement extends Curse {
    private static final Field tickTimerField = ObfuscationReflectionHelper.findField(FoodData.class, "tickTimer");

    public CurseEnfeeblement(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(
                Attributes.MAX_HEALTH,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, "curse_of_enfeeblement"),
                -0.2D,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide() && player.hasEffect(WizardryMobEffects.CURSE_OF_ENFEEBLEMENT)) {
            if (player.getFoodData().getFoodLevel() >= 18) {
                try {
                    tickTimerField.setInt(player.getFoodData(), 0);
                } catch (IllegalAccessException e) {
                    Wizardry.LOGGER.error("无法重置玩家食物计时器: ", e);
                }
            }
        }
    }
}
