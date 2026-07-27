package top.begonia.wizardry.core.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.entity.ISpellCaster;
import top.begonia.wizardry.core.item.impl.ScrollItem;
import top.begonia.wizardry.core.item.impl.SpellBookItem;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardryLoots;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.List;

public class WizardSpell extends LootItemConditionalFunction {
    public static final MapCodec<WizardSpell> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance).apply(instance, WizardSpell::new)
    );

    protected WizardSpell(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    public @NonNull MapCodec<WizardSpell> codec() {
        return WizardryLoots.WIZARD_SPELL.get();
    }

    @Override
    protected @NonNull ItemStack run(@NonNull ItemStack itemStack, @NonNull LootContext lootContext) {
        if (!(itemStack.getItem() instanceof SpellBookItem) && !(itemStack.getItem() instanceof ScrollItem)) {
            Wizardry.LOGGER.warn("Applying the wizard_spell loot function to an item that isn't a spell book or scroll.");
        }

        if (lootContext.getParameter(LootContextParams.THIS_ENTITY) instanceof ISpellCaster caster) {
            List<AbstractSpell> spells = caster.getSpells();
            spells.remove(WizardrySpells.MAGIC_MISSILE.get());
            spells.removeIf(s -> !s.applicableForItem(itemStack.getItem()));
            if (spells.isEmpty()) {
                Wizardry.LOGGER.warn("Tried to apply the wizard_spell loot function to an item, but none of the looted entity's spells were applicable for that item. This is probably a bug!");
            } else {
                List<? extends DeferredHolder<AbstractSpell, ? extends AbstractSpell>> filteredSpells = WizardrySpells.SPELLS.getEntries().stream()
                        .filter(holder -> holder != WizardrySpells.NONE)
                        .filter(holder -> holder != WizardrySpells.MAGIC_MISSILE)
                        .toList();
                if (!filteredSpells.isEmpty()) {
                    itemStack.set(WizardryComponents.SPELL.get(), filteredSpells.get(lootContext.getRandom().nextInt()));
                }
            }
        } else {
            Wizardry.LOGGER.warn("Applying the wizard_spell loot function to an entity that isn't a spell caster.");
        }

        return itemStack;
    }

}
