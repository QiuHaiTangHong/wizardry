package top.begonia.wizardry.core.registry;

import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.advancement.*;

public final class WizardryAdvancementTriggers {

    private WizardryAdvancementTriggers() {
    }

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Wizardry.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, CustomAdvancementTrigger> MAX_OUT_WAND =
            TRIGGERS.register("trigger_max_out_wand", CustomAdvancementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, CustomAdvancementTrigger> SPECIAL_UPGRADE =
            TRIGGERS.register("trigger_special_upgrade", CustomAdvancementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, CustomAdvancementTrigger> ANGER_WIZARD =
            TRIGGERS.register("trigger_anger_wizard", CustomAdvancementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, CustomAdvancementTrigger> BUY_MASTER_SPELL =
            TRIGGERS.register("trigger_buy_master_spell", CustomAdvancementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, CustomAdvancementTrigger> WIZARD_TRADE =
            TRIGGERS.register("trigger_wizard_trade", CustomAdvancementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, CustomAdvancementTrigger> SPELL_FAILURE =
            TRIGGERS.register("trigger_spell_failure", CustomAdvancementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, CustomAdvancementTrigger> WAND_LEVELUP =
            TRIGGERS.register("trigger_wand_levelup", CustomAdvancementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, CustomAdvancementTrigger> RESTORE_IMBUEMENT_ALTAR =
            TRIGGERS.register("restore_imbuement_altar", CustomAdvancementTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, StructureTrigger> VISIT_STRUCTURE =
            TRIGGERS.register("visit_structure", StructureTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, WizardryContainerTrigger> ARCANE_WORKBENCH =
            TRIGGERS.register("arcane_workbench", WizardryContainerTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, SpellCastTrigger> CAST_SPELL =
            TRIGGERS.register("cast_spell", SpellCastTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, SpellDiscoveryTrigger> DISCOVER_SPELL =
            TRIGGERS.register("discover_spell", SpellDiscoveryTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, WizardryContainerTrigger> IMBUEMENT_ALTAR =
            TRIGGERS.register("imbuement_altar", WizardryContainerTrigger::new);

    public static void register(IEventBus modBus) {
        TRIGGERS.register(modBus);
    }
}