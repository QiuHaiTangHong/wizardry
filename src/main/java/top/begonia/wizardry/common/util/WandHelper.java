package top.begonia.wizardry.common.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.begonia.wizardry.common.data.WandUpgrades;
import top.begonia.wizardry.common.registry.WizardryComponents;
import top.begonia.wizardry.common.registry.WizardrySpells;
import top.begonia.wizardry.common.spell.AbstractSpell;

import java.util.*;

//(/\*([\s\S]*?)\*/)|(//.*)
public final class WandHelper {

    private static final HashMap<Item, String> upgradeMap = new HashMap<>();

    public static AbstractSpell[] getSpells(ItemStack wand) {

        List<Holder<AbstractSpell>> spellHolders = wand.getOrDefault(
                WizardryComponents.SPELL_ARRAY_KEY.get(),
                List.of()
        );
        return spellHolders.stream()
                .map(Holder::value)
                .toArray(AbstractSpell[]::new);
    }

    public static void setSpells(ItemStack wand, AbstractSpell[] spells) {
        if (spells == null || spells.length == 0) {
            wand.set(WizardryComponents.SPELL_ARRAY_KEY.get(), List.of());
            return;
        }
        Registry<AbstractSpell> registry = WizardrySpells.SPELLS.getRegistry().get();
        List<Holder<AbstractSpell>> spellHolders = Arrays.stream(spells)
                .map(spell -> {
                    AbstractSpell target = (spell != null) ? spell : WizardrySpells.NONE.get();
                    return registry.wrapAsHolder(target);
                })
                .toList();
        wand.set(WizardryComponents.SPELL_ARRAY_KEY.get(), spellHolders);
    }

    public static AbstractSpell getCurrentSpell(ItemStack wand) {
        AbstractSpell[] spells = getSpells(wand);
        int selectedSpell = wand.getOrDefault(WizardryComponents.SELECTED_SPELL_KEY.get(), 0);
        if (selectedSpell >= 0 && selectedSpell < spells.length) {
            return spells[selectedSpell];
        }
        return WizardrySpells.NONE.get();
    }

    public static AbstractSpell getNextSpell(ItemStack wand) {

        AbstractSpell[] spells = getSpells(wand);
        int index = getNextSpellIndex(wand);

        if (index >= 0 && index < spells.length) {
            return spells[index];
        }

        return WizardrySpells.NONE.get();
    }

    public static AbstractSpell getPreviousSpell(ItemStack wand) {

        AbstractSpell[] spells = getSpells(wand);
        int index = getPreviousSpellIndex(wand);

        if (index >= 0 && index < spells.length) {
            return spells[index];
        }

        return WizardrySpells.NONE.get();
    }

    public static void selectNextSpell(ItemStack wand) {
        if (!wand.has(WizardryComponents.SPELL_ARRAY_KEY.get())) {
//            AbstractSpell[] defaultSpells = new AbstractSpell[ItemWand.BASE_SPELL_SLOTS];
//            Arrays.fill(defaultSpells, WizardrySpells.NONE.get());
//            setSpells(wand, defaultSpells);
        }

        int nextIndex = getNextSpellIndex(wand);
        wand.set(WizardryComponents.SELECTED_SPELL_KEY.get(), nextIndex);
    }

    public static void selectPreviousSpell(ItemStack wand) {
        if (!wand.has(WizardryComponents.SPELL_ARRAY_KEY.get())) {
//            AbstractSpell[] defaultSpells = new AbstractSpell[ItemWand.BASE_SPELL_SLOTS];
//            java.util.Arrays.fill(defaultSpells, WizardrySpells.NONE.get());
//            setSpells(wand, defaultSpells);
        }
        int prevIndex = getPreviousSpellIndex(wand);
        wand.set(WizardryComponents.SELECTED_SPELL_KEY.get(), prevIndex);
    }

    public static boolean selectSpell(ItemStack wand, int index) {
        AbstractSpell[] spells = getSpells(wand);
        if (!wand.has(WizardryComponents.SPELL_ARRAY_KEY.get())) {
//            AbstractSpell[] defaultSpells = new AbstractSpell[ItemWand.BASE_SPELL_SLOTS];
//            java.util.Arrays.fill(defaultSpells, WizardrySpells.NONE.get());
//            setSpells(wand, defaultSpells);
//            spells = getSpells(wand);
        }
        if (index < 0 || index >= spells.length) {
            return false;
        }
        wand.set(WizardryComponents.SELECTED_SPELL_KEY.get(), index);
        return true;
    }

    private static int getNextSpellIndex(ItemStack wand) {
        int numberOfSpells = getSpells(wand).length;
        if (numberOfSpells <= 1) return 0;
        int currentIndex = wand.getOrDefault(WizardryComponents.SELECTED_SPELL_KEY.get(), 0);
        return (currentIndex + 1) % numberOfSpells;
    }

    private static int getPreviousSpellIndex(ItemStack wand) {
        int numberOfSpells = getSpells(wand).length;
        if (numberOfSpells <= 1) return 0;
        int currentIndex = wand.getOrDefault(WizardryComponents.SELECTED_SPELL_KEY.get(), 0);
        return (currentIndex - 1 + numberOfSpells) % numberOfSpells;
    }

    public static int[] getCooldowns(ItemStack wand) {
        List<Integer> cooldownList = wand.getOrDefault(WizardryComponents.COOLDOWN_ARRAY_KEY.get(), List.of());
        int[] cooldowns = new int[cooldownList.size()];
        for (int i = 0; i < cooldownList.size(); i++) {
            cooldowns[i] = cooldownList.get(i);
        }
        return cooldowns;
    }

    public static void setCooldowns(ItemStack wand, int[] cooldowns) {
        if (cooldowns == null) {
            wand.remove(WizardryComponents.COOLDOWN_ARRAY_KEY.get());
            return;
        }
        List<Integer> list = new java.util.ArrayList<>(cooldowns.length);
        for (int cooldown : cooldowns) {
            list.add(cooldown);
        }
        wand.set(WizardryComponents.COOLDOWN_ARRAY_KEY.get(), list);
    }

    public static void decrementCooldowns(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length == 0) return;
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) cooldowns[i]--;
            if (cooldowns[i] < 0) cooldowns[i] = 0;
        }
        setCooldowns(wand, cooldowns);
    }

    public static int getCurrentCooldown(ItemStack wand) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.SELECTED_SPELL_KEY.get(), 0);
        int[] cooldowns = getCooldowns(wand);
        if (selectedSpell < 0 || selectedSpell >= cooldowns.length) {
            return 0;
        }
        return cooldowns[selectedSpell];
    }

    public static int getNextCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int nextSpell = getNextSpellIndex(wand);
        if (nextSpell < 0 || cooldowns.length <= nextSpell) return 0;
        return cooldowns[nextSpell];
    }

    public static int getPreviousCooldown(ItemStack wand) {

        int[] cooldowns = getCooldowns(wand);

        int previousSpell = getPreviousSpellIndex(wand);

        if (previousSpell < 0 || cooldowns.length <= previousSpell) return 0;

        return cooldowns[previousSpell];
    }

    public static void setCurrentCooldown(ItemStack wand, int cooldown) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.SELECTED_SPELL_KEY.get(), 0);
        int spellCount = getSpells(wand).length;
        if (selectedSpell < 0 || selectedSpell >= spellCount) return;
        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length < spellCount) {
            cooldowns = java.util.Arrays.copyOf(cooldowns, spellCount);
        }
        int[] maxCooldowns = getMaxCooldowns(wand);
        if (maxCooldowns.length < spellCount) {
            maxCooldowns = java.util.Arrays.copyOf(maxCooldowns, spellCount);
        }
        int finalCooldown = Math.max(1, cooldown);
        cooldowns[selectedSpell] = finalCooldown;
        maxCooldowns[selectedSpell] = finalCooldown;
        setCooldowns(wand, cooldowns);
        setMaxCooldowns(wand, maxCooldowns);
    }

    public static int[] getMaxCooldowns(ItemStack wand) {
        List<Integer> list = wand.getOrDefault(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get(), List.of());
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void setMaxCooldowns(ItemStack wand, int[] cooldowns) {
        if (cooldowns == null) {
            wand.remove(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get());
            return;
        }
        List<Integer> list = java.util.stream.IntStream.of(cooldowns).boxed().toList();
        wand.set(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get(), list);
    }

    public static int getCurrentMaxCooldown(ItemStack wand) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.SELECTED_SPELL_KEY.get(), 0);
        int[] maxCooldowns = getMaxCooldowns(wand);

        if (selectedSpell < 0 || selectedSpell >= maxCooldowns.length) return 0;
        return maxCooldowns[selectedSpell];
    }

    public static int getUpgradeLevel(ItemStack wand, Item upgrade) {

        String key = upgradeMap.get(upgrade);
        if (key == null) return 0;
        WandUpgrades upgrades = wand.getOrDefault(
                WizardryComponents.UPGRADES_KEY.get(),
                WandUpgrades.EMPTY
        );
        return upgrades.counts().getOrDefault(key, 0);
    }

    public static int getTotalUpgrades(ItemStack wand) {

        int totalUpgrades = 0;

        for (Item item : upgradeMap.keySet()) {
            totalUpgrades += getUpgradeLevel(wand, item);
        }

        return totalUpgrades;
    }

    public static void applyUpgrade(ItemStack wand, Item upgrade) {
        String key = upgradeMap.get(upgrade);
        if (key == null) {
            return;
        }
        wand.update(
                WizardryComponents.UPGRADES_KEY.get(),
                WandUpgrades.EMPTY,
                current -> current.withUpgrade(key)
        );
    }

    public static boolean isWandUpgrade(Item upgrade) {
        return upgradeMap.containsKey(upgrade);
    }

    public static Set<Item> getSpecialUpgrades() {
        return Collections.unmodifiableSet(WandHelper.upgradeMap.keySet());
    }

    static String getIdentifier(Item upgrade) {
        if (!isWandUpgrade(upgrade)) throw new IllegalArgumentException(
                "Tried to get a wand upgrade key for an item" + "that is not a registered special wand upgrade.");
        return upgradeMap.get(upgrade);
    }

    public static void registerSpecialUpgrade(Item upgrade, String identifier) {
        if (upgradeMap.containsValue(identifier))
            throw new IllegalArgumentException("Duplicate wand upgrade identifier: " + identifier);
        upgradeMap.put(upgrade, identifier);
    }

    public static void populateUpgradeMap() {

    }

    public static void setProgression(ItemStack wand, int progression) {
        wand.set(WizardryComponents.PROGRESSION_KEY.get(), progression);
    }

    public static int getProgression(ItemStack wand) {
        return wand.getOrDefault(WizardryComponents.PROGRESSION_KEY.get(), 0);
    }

    public static void addProgression(ItemStack wand, int progression) {
        setProgression(wand, getProgression(wand) + progression);
    }

    //TODO
    public static boolean rechargeManaOnApplyButtonPressed(Slot centre, Slot crystals) {
        return false;
    }
}
