package top.begonia.wizardry.core.item.impl;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
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
import top.begonia.wizardry.core.constants.EnabledEnum;
import top.begonia.wizardry.core.data.runtime.SpellContextFlow;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.spell.definition.spell.part.SpellContext;
import top.begonia.wizardry.core.item.IManaStoringItem;
import top.begonia.wizardry.core.item.ISpellCastingItem;
import top.begonia.wizardry.core.item.IWorkbenchItem;
import top.begonia.wizardry.core.registry.WizardryAttachment;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.util.TextHelper;
import top.begonia.wizardry.core.util.WandHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;

public class WandItem extends Item implements IWorkbenchItem, ISpellCastingItem, IManaStoringItem {
    /**
     * The number of spell slots a wand has with no attunement upgrades applied.
     */
    public static int BASE_SPELL_SLOTS = ServerConfig.baseSpellSlots;

    /**
     * The number of ticks between each time a continuous spell is added to the player's recently-cast spells.
     */
    private static final int CONTINUOUS_TRACKING_INTERVAL = 20;
    /**
     * The increase in progression for casting spells of the matching element.
     */
    private static final float ELEMENTAL_PROGRESSION_MODIFIER = 1.2f;
    /**
     * The increase in progression for casting an undiscovered spell (can only happen once per spell for each player).
     */
    private static final float DISCOVERY_PROGRESSION_MODIFIER = 5f;
    /**
     * The increase in progression for tiers that the player has already reached.
     */
    private static final float SECOND_TIME_PROGRESSION_MODIFIER = 1.5f;
    /**
     * The fraction of progression lost when all recently-cast spells are the same as the one being cast.
     */
    private static final float MAX_PROGRESSION_REDUCTION = 0.75f;

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

    public static void setTier(@NonNull ItemStack stack, TierEnum tier) {
        stack.set(WizardryComponents.TIER, tier);
    }

    public static @NonNull ElementEnum getElement(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.ELEMENT, ElementEnum.MAGIC);
    }

    public static void setElement(@NonNull ItemStack stack, ElementEnum element) {
        stack.set(WizardryComponents.ELEMENT, element);
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
                (int) ((tier.level + 1) * ServerConfig.Constants.potencyIncreasePerTier * 100 + 0.5f), element.getDisplayName()).withStyle(ChatFormatting.DARK_GRAY));

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

    public static @NonNull ItemStack getWand(TierEnum tier, ElementEnum element) {
        ItemStack itemStack = new ItemStack(WizardryItems.WAND);
        setTier(itemStack, tier);
        setElement(itemStack, element);
        itemStack.set(DataComponents.MAX_DAMAGE, tier.getMaxCharge());
        return itemStack;
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return 5;
    }

    @Override
    public ItemStack applyUpgrade(@Nullable Player player, ItemStack wand, @NonNull ItemStack upgrade) {

        // Upgrades wand if necessary. Damage is copied, preserving remaining durability,
        // and also the entire NBT tag compound.
        if (upgrade.getItem() == WizardryItems.ARCANE_TOME.get()) {

            TierEnum tier = TierEnum.values()[upgrade.getDamageValue()];
            TierEnum wandTier = getTier(wand);
            ElementEnum wandElement = getElement(wand);

            // Checks the wand upgrade is for the tier above the wand's tier, and that either the wand has enough
            // progression or the player is in creative mode.
            if ((player == null || player.isCreative() || ServerConfig.legacyWandLevelling
                    || WandHelper.getProgression(wand) >= tier.getProgression())
                    && tier == wandTier.next() && wandTier != TierEnum.MASTER) {

                if (ServerConfig.legacyWandLevelling) {
                    // Progression has little meaning with legacy upgrade mechanics so just reset it
                    // In theory, you can get 'free' progression when upgrading since progression can't be negative,
                    // so the flipside of that is you lose any excess
                    WandHelper.setProgression(wand, 0);
                } else {
                    // Carry excess progression over to the new stack
                    WandHelper.setProgression(wand, WandHelper.getProgression(wand) - tier.getProgression());
                }

                if (player != null) {
                }

                ItemStack newWand = getWand(tier, wandElement);
                newWand.applyComponents(wand.getComponents());
                // This needs to be done after copying the tag compound so the mana capacity for the new wand
                // takes storage upgrades into account
                // Note the usage of the new wand item and not 'this' to ensure the correct capacity is used
                ((IManaStoringItem) newWand.getItem()).setMana(newWand, this.getMana(wand));

                upgrade.shrink(1);

                return newWand;
            }

        } else if (WandHelper.isWandUpgrade(upgrade.getItem())) {

            // Special upgrades
            Item specialUpgrade = upgrade.getItem();
            TierEnum wandTier = getTier(wand);
            ElementEnum wandElement = getElement(wand);

            int maxUpgrades = wandTier.getUpgradeLimit();
            if (wandElement == ElementEnum.DEFAULT) {
                maxUpgrades += ServerConfig.Constants.nonElementalUpgradeBonus;
            }

            if (WandHelper.getTotalUpgrades(wand) < maxUpgrades
                    && WandHelper.getUpgradeLevel(wand, specialUpgrade) < ServerConfig.Constants.upgradeStackLimit) {

                // Used to preserve existing mana when upgrading storage rather than creating free mana.
                int prevMana = this.getMana(wand);

                WandHelper.applyUpgrade(wand, specialUpgrade);

                if (specialUpgrade == WizardryItems.STORAGE_UPGRADE.get()) {

                    this.setMana(wand, prevMana);

                } else if (specialUpgrade == WizardryItems.ATTUNEMENT_UPGRADE.get()) {

                    int newSlotCount = BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(wand, WizardryItems.ATTUNEMENT_UPGRADE.get());

                    AbstractSpell[] spells = WandHelper.getSpells(wand);
                    AbstractSpell[] newSpells = new AbstractSpell[newSlotCount];

                    for (int i = 0; i < newSpells.length; i++) {
                        newSpells[i] = i < spells.length && spells[i] != null ? spells[i] : WizardrySpells.NONE.get();
                    }

                    WandHelper.setSpells(wand, newSpells);

                    int[] cooldowns = WandHelper.getCooldowns(wand);
                    int[] newCooldowns = new int[newSlotCount];

                    if (cooldowns.length > 0) {
                        System.arraycopy(cooldowns, 0, newCooldowns, 0, cooldowns.length);
                    }

                    WandHelper.setCooldowns(wand, newCooldowns);
                }

                upgrade.shrink(1);

                if (player != null) {
                }

            }
        }

        return wand;
    }

    @Override
    public boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, @NonNull Slot upgrade, Slot[] spellBooks) {
        boolean changed = false;
        if (upgrade.hasItem()) {
            ItemStack original = centre.getItem().copy();
            centre.set(this.applyUpgrade(player, centre.getItem(), upgrade.getItem()));
            changed = !ItemStack.isSameItem(centre.getItem(), original);
        }

        AbstractSpell[] spells = WandHelper.getSpells(centre.getItem());

        if (spells.length == 0) {
            spells = new AbstractSpell[BASE_SPELL_SLOTS];
        }

        TierEnum tier = getTier(centre.getItem());

        for (int i = 0; i < spells.length; i++) {
            ItemStack itemStack = spellBooks[i].getItem();
            if (itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof SpellBookItem spellBookItem) {
                AbstractSpell spell = spellBookItem.getCurrentSpell(itemStack);
                if (!(spell.getTier().level > tier.level) && spells[i] != spell && spell.isEnabled(EnabledEnum.WANDS)) {
                    if (ServerConfig.preventBindingSameSpellTwiceToWands && Arrays.stream(spells).anyMatch(s -> s == spell)) {
                        continue;
                    }
                    spells[i] = spell;
                    changed = true;
                    if (ServerConfig.singleUseSpellBooks) {
                        spellBooks[i].getItem().shrink(1);
                    }
                }
            }
        }

        WandHelper.setSpells(centre.getItem(), spells);
        if (WandHelper.rechargeManaOnApplyButtonPressed(centre, crystals)) {
            changed = true;
        }
        if (changed) {
            centre.setChanged();
        }
        return changed;
    }

    @Override
    public boolean isFoil(@NonNull ItemStack stack) {
        return false;
    }
}
