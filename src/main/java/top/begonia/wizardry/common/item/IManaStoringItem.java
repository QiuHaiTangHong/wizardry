package top.begonia.wizardry.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;


public interface IManaStoringItem {
    int getMana(ItemStack stack);

    void setMana(ItemStack stack, int mana);

    int getManaCapacity(ItemStack stack);

    default boolean showManaInWorkbench(Player player, ItemStack stack) {
        return true;
    }

    default void consumeMana(ItemStack stack, int mana, @Nullable LivingEntity wielder) {
        if (wielder instanceof Player player && player.getAbilities().instabuild) {
            return;
        }
        setMana(stack, Math.max(getMana(stack) - mana, 0));
    }

    default void rechargeMana(ItemStack stack, int mana) {
        setMana(stack, Math.min(getMana(stack) + mana, getManaCapacity(stack)));
    }

    default boolean isManaFull(ItemStack stack) {
        return getMana(stack) == getManaCapacity(stack);
    }

    default boolean isManaEmpty(ItemStack stack) {
        return getMana(stack) == 0;
    }

    default float getFullness(ItemStack stack) {
        return (float) getMana(stack) / getManaCapacity(stack);
    }

}
