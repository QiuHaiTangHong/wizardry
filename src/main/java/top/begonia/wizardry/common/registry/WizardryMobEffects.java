package top.begonia.wizardry.common.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.common.potion.CurseEnfeeblement;
import top.begonia.wizardry.common.potion.CurseUndeath;
import top.begonia.wizardry.common.potion.IronFleshPotion;
import top.begonia.wizardry.common.potion.MagicEffectPotion;

public class WizardryMobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Wizardry.MODID);

    public static final DeferredHolder<MobEffect, MagicEffectPotion> FROST = register("frost", MobEffectCategory.HARMFUL, 0x38ddec);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> TRANSIENCE = register("transience", MobEffectCategory.BENEFICIAL, 0xffe89b);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> FIRE_SKIN = register("fire_skin", MobEffectCategory.BENEFICIAL, 0xff2f02);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> ICE_SHROUD = register("ice_shroud", MobEffectCategory.BENEFICIAL, 0x52f1ff);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> STATIC_AURA = register("static_aura", MobEffectCategory.BENEFICIAL, 0x0070ff);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> DECAY = register("decay", MobEffectCategory.HARMFUL, 0x3c006c);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> SIXTH_SENSE = register("sixth_sense", MobEffectCategory.BENEFICIAL, 0xc6ff01);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> ARCANE_JAMMER = register("arcane_jammer", MobEffectCategory.HARMFUL, 0xcf4aa2);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> MIND_TRICK = register("mind_trick", MobEffectCategory.HARMFUL, 0x601683);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> MIND_CONTROL = register("mind_control", MobEffectCategory.HARMFUL, 0x320b44);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> FONT_OF_MANA = register("font_of_mana", MobEffectCategory.BENEFICIAL, 0xffe5bb);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> FEAR = register("fear", MobEffectCategory.HARMFUL, 0xbd0100);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> CURSE_OF_SOUL_BINDING = register("curse_of_soul_binding", MobEffectCategory.HARMFUL, 0x0f000f);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> PARALYSIS = register("paralysis", MobEffectCategory.HARMFUL, 0xFFFF00);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> MUFFLE = register("muffle", MobEffectCategory.BENEFICIAL, 0x4464d9);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> WARD = register("ward", MobEffectCategory.BENEFICIAL, 0xc991d0);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> SLOW_TIME = register("slow_time", MobEffectCategory.BENEFICIAL, 0x5be3bb);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> EMPOWERMENT = register("empowerment", MobEffectCategory.BENEFICIAL, 0x8367bd);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> CURSE_OF_ENFEEBLEMENT = EFFECTS.register("curse_of_enfeeblement", () -> new CurseEnfeeblement(MobEffectCategory.HARMFUL, 0x36000b));
    public static final DeferredHolder<MobEffect, MagicEffectPotion> CURSE_OF_UNDEATH = EFFECTS.register("curse_of_undeath", () -> new CurseUndeath(MobEffectCategory.HARMFUL, 0x685c00));
    public static final DeferredHolder<MobEffect, MagicEffectPotion> CONTAINMENT = register("containment", MobEffectCategory.HARMFUL, 0x7988cc);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> FROST_STEP = register("frost_step", MobEffectCategory.BENEFICIAL, 0x88E1FF);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> MARK_OF_SACRIFICE = register("mark_of_sacrifice", MobEffectCategory.HARMFUL, 0xe90e48);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> MIRAGE = register("mirage", MobEffectCategory.BENEFICIAL, 0xEE82EE);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> OAK_FLESH = register("oak_flesh", MobEffectCategory.BENEFICIAL, 0x7d5d3d);
    public static final DeferredHolder<MobEffect, MagicEffectPotion> IRON_FLESH = EFFECTS.register("iron_flesh", () -> new IronFleshPotion(MobEffectCategory.BENEFICIAL, 0xd8d8d8));
    public static final DeferredHolder<MobEffect, MagicEffectPotion> DIAMOND_FLESH = register("diamond_flesh", MobEffectCategory.BENEFICIAL, 0x5decf5);

    private static DeferredHolder<MobEffect, MagicEffectPotion> register(String name, MobEffectCategory category, int color) {
        return EFFECTS.register(name, () -> new MagicEffectPotion(category, color));
    }

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
