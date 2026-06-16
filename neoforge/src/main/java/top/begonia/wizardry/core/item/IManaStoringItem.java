package top.begonia.wizardry.core.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;


public interface IManaStoringItem {
    int getMana(@NonNull ItemStack itemStack);

    void setMana(@NonNull ItemStack itemStack, int mana);

    int getManaCapacity(@NonNull ItemStack itemStack);

    default boolean showManaInWorkbench(Player player, ItemStack itemStack) {
        return true;
    }

    default void consumeMana(ItemStack itemStack, int mana, @Nullable LivingEntity wielder) {
        if (wielder instanceof Player player && player.getAbilities().instabuild) {
            return;
        }
        setMana(itemStack, Math.max(getMana(itemStack) - mana, 0));
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
