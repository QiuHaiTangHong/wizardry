package top.begonia.wizardry.core.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.UUID;

public interface IConjuredItem {
    UUID POTENCY_MODIFIER = UUID.fromString("da067ea6-0b35-4140-8436-5476224de9dd");

    static void setDurationMultiplier(ItemStack stack, float multiplier) {
        stack.set(WizardryComponents.DURATION_MULTIPLIER_KEY.get(), multiplier);
    }


    static void setDamageMultiplier(ItemStack stack, float multiplier) {
        stack.set(WizardryComponents.DAMAGE_MULTIPLIER, multiplier);
    }


    static float getDamageMultiplier(ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.DAMAGE_MULTIPLIER.get(), 1.0f);
    }


    default int getMaxDamageFromNBT(ItemStack stack, AbstractSpell spell) {
        return 1;
    }


    default void addAnimationPropertyOverrides() {
    }


    default int getAnimationFrames() {
        return 8;
    }


    static int getTimerBarColour(ItemStack stack) {
        return 1;
    }

    @EventBusSubscriber(modid = Wizardry.MODID)
    class EventHandler {
        @SubscribeEvent
        static void onLivingDropsEvent(LivingDropsEvent event) {
            event.getDrops().removeIf(itemEntity -> {
                ItemStack stack = itemEntity.getItem();
                if (!stack.isEmpty() && stack.getItem() instanceof IConjuredItem) {
                    itemEntity.discard();
                    return true;
                }
                return false;
            });
        }

        @SubscribeEvent
        static void onItemTossEvent(ItemTossEvent event) {
            ItemEntity itemEntity = event.getEntity();
            ItemStack stack = itemEntity.getItem();
            if (!stack.isEmpty() && stack.getItem() instanceof IConjuredItem) {
                event.setCanceled(true);
                event.getPlayer().getInventory().add(stack);
            }
        }
    }
}
