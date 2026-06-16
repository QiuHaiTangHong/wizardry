package top.begonia.wizardry.core.item.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.util.GlyphGenerator;
import top.begonia.wizardry.core.config.CommonConfig;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.data.runtime.SpellContextFlow;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.item.IManaStoringItem;
import top.begonia.wizardry.core.item.ISpellCastingItem;
import top.begonia.wizardry.core.item.IWorkbenchItem;
import top.begonia.wizardry.core.registry.WizardryAttachment;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.TextHelper;
import top.begonia.wizardry.core.util.WandHelper;

import java.util.function.Consumer;

public class WandItem extends Item implements IWorkbenchItem, ISpellCastingItem, IManaStoringItem {

    public WandItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack itemStack) {
        TierEnum tier = WandItem.getTier(itemStack);
        ElementEnum element = WandItem.getElement(itemStack);
        return Component.translatable("item." + Wizardry.MODID + "." + tier.getSerializedName() + "_" + element.getSerializedName() + "_wand").withStyle(element.getStyle());
    }

    public static @NonNull TierEnum getTier(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.TIER, TierEnum.NOVICE);
    }

    public static @NonNull ElementEnum getElement(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.ELEMENT, ElementEnum.MAGIC);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext context, @NonNull TooltipDisplay display, @NonNull Consumer<Component> builder, @NonNull TooltipFlag tooltipFlag) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ElementEnum element = getElement(itemStack);
        TierEnum tier = getTier(itemStack);
        builder.accept(Component.translatable("item." + Wizardry.MODID + ".wand.buff",
                (int) ((tier.level + 1) * ServerConfig.Constants.POTENCY_INCREASE_PER_TIER * 100 + 0.5f), element.getDisplayName()).withStyle(ChatFormatting.DARK_GRAY));

        AbstractSpell spell = WandHelper.getCurrentSpell(itemStack);

        boolean discovered = !CommonConfig.discoveryMode || player.isCreative() || player.getData(WizardryAttachment.WIZARD_PLAYER_DATA).hasSpellBeenDiscovered(spell);
        builder.accept(TextHelper.componentWithComponent("item." + Wizardry.MODID + ".wand.spell", discovered ? spell.getDisplayNameWithFormatting() : GlyphGenerator.getGlyphName(spell).withStyle(ChatFormatting.BLUE)));

        //当且仅当Debug时启用
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            builder.accept(Component.translatable("item." + Wizardry.MODID + ".wand.mana", this.getMana(itemStack), this.getManaCapacity(itemStack)).withStyle(ChatFormatting.BLUE));
            builder.accept(Component.translatable("item." + Wizardry.MODID + ".wand.progression", WandHelper.getProgression(itemStack), tier.level < TierEnum.MASTER.level ? tier.next().getProgression() : 0).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NonNull ItemUseAnimation getUseAnimation(@NonNull ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public @NonNull AbstractSpell getCurrentSpell(ItemStack stack) {
        return WandHelper.getCurrentSpell(stack);
    }

    @Override
    public @NonNull AbstractSpell getNextSpell(ItemStack stack) {
        return WandHelper.getNextSpell(stack);
    }

    @Override
    public @NonNull AbstractSpell getPreviousSpell(ItemStack stack) {
        return WandHelper.getPreviousSpell(stack);
    }

    @Override
    public AbstractSpell[] getSpells(ItemStack stack) {
        return WandHelper.getSpells(stack);
    }

    @Override
    public void selectNextSpell(ItemStack stack) {
        WandHelper.selectNextSpell(stack);
    }

    @Override
    public void selectPreviousSpell(ItemStack stack) {
        WandHelper.selectPreviousSpell(stack);
    }

    @Override
    public boolean selectSpell(ItemStack stack, int index) {
        return WandHelper.selectSpell(stack, index);
    }

    @Override
    public int getCurrentCooldown(ItemStack stack) {
        return WandHelper.getCurrentCooldown(stack);
    }

    @Override
    public int getCurrentMaxCooldown(ItemStack stack) {
        return WandHelper.getCurrentMaxCooldown(stack);
    }

    @Override
    public boolean showSpellHUD(Player player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean showTooltip(ItemStack stack) {
        return true;
    }

    @Override
    public final void setDamage(@NonNull ItemStack stack, int damage) {
    }

    @Override
    public void setMana(@NonNull ItemStack stack, int mana) {
        super.setDamage(stack, getManaCapacity(stack) - mana);
    }

    @Override
    public int getMana(@NonNull ItemStack stack) {
        return getManaCapacity(stack) - getDamage(stack);
    }

    @Override
    public int getManaCapacity(@NonNull ItemStack stack) {
        return this.getMaxDamage(stack);
    }

    @Override
    public boolean canCast(int castingTick, SpellContextFlow spellContextFlow) {
        return false;
    }

    @Override
    public boolean cast(ItemStack stack, AbstractSpell spell, Player caster, InteractionHand hand, int castingTick, SpellContext spellContextFlow) {
        return false;
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 5;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
        return false;
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return false;
    }
}
