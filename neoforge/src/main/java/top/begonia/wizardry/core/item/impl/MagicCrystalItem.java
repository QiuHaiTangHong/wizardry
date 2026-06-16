package top.begonia.wizardry.core.item.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.item.IMultiTexturedItem;
import top.begonia.wizardry.core.registry.WizardryComponents;

public class MagicCrystalItem extends Item implements IMultiTexturedItem {

    public MagicCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public Identifier getModelName(ItemStack stack) {
        return null;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        ElementEnum type = stack.getOrDefault(WizardryComponents.ELEMENT.get(), ElementEnum.MAGIC);
        return Component.translatable("item." + Wizardry.MODID + ".crystal_" + type.getSerializedName());
    }
}
