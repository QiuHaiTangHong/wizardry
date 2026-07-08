package top.begonia.wizardry.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.effect.impl.*;
import top.begonia.wizardry.core.effect.impl.curse.CurseEnfeeblementMobEffect;
import top.begonia.wizardry.core.effect.impl.curse.CurseMobEffect;
import top.begonia.wizardry.core.effect.impl.curse.CurseUndeathMobEffect;
import top.begonia.wizardry.core.effect.MagicMobEffect;
import top.begonia.wizardry.core.effect.impl.flesh.DiamondFleshMobEffect;
import top.begonia.wizardry.core.effect.impl.flesh.IronFleshMobEffect;
import top.begonia.wizardry.core.effect.impl.flesh.OakFleshMobEffect;

public final class WizardryMobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Wizardry.MODID);

    public static final DeferredHolder<MobEffect, FrostPotion> FROST = EFFECTS.register("frost", () -> new FrostPotion(MobEffectCategory.HARMFUL, 0x38ddec));
    public static final DeferredHolder<MobEffect, MagicEffectParticlesMobEffect> TRANSIENCE = EFFECTS.register("transience", () -> new MagicEffectParticlesMobEffect(MobEffectCategory.BENEFICIAL, 0xffe89b));
    public static final DeferredHolder<MobEffect, FireSkinPotion> FIRE_SKIN = EFFECTS.register("fire_skin", () -> new FireSkinPotion(MobEffectCategory.BENEFICIAL, 0xff2f02));
    public static final DeferredHolder<MobEffect, MagicMobEffect> ICE_SHROUD = EFFECTS.register("ice_shroud", () -> new MagicEffectParticlesMobEffect(MobEffectCategory.BENEFICIAL, 0x52f1ff));
    public static final DeferredHolder<MobEffect, MagicMobEffect> STATIC_AURA = EFFECTS.register("static_aura", () -> new MagicEffectParticlesMobEffect(MobEffectCategory.BENEFICIAL, 0x0070ff));
    public static final DeferredHolder<MobEffect, MagicMobEffect> DECAY = EFFECTS.register("decay", () -> new DecayMobEffect(MobEffectCategory.HARMFUL, 0x3c006c));
    public static final DeferredHolder<MobEffect, MagicMobEffect> SIXTH_SENSE = EFFECTS.register("sixth_sense", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0xc6ff01));
    public static final DeferredHolder<MobEffect, MagicMobEffect> ARCANE_JAMMER = EFFECTS.register("arcane_jammer", () -> new MagicMobEffect(MobEffectCategory.HARMFUL, 0xcf4aa2));
    public static final DeferredHolder<MobEffect, MagicMobEffect> MIND_TRICK = EFFECTS.register("mind_trick", () -> new MagicMobEffect(MobEffectCategory.HARMFUL, 0x601683));
    public static final DeferredHolder<MobEffect, MagicMobEffect> MIND_CONTROL = EFFECTS.register("mind_control", () -> new MagicEffectParticlesMobEffect(MobEffectCategory.HARMFUL, 0x320b44));
    public static final DeferredHolder<MobEffect, MagicMobEffect> FONT_OF_MANA = EFFECTS.register("font_of_mana", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0xffe5bb));
    public static final DeferredHolder<MobEffect, MagicMobEffect> FEAR = EFFECTS.register("fear", () -> new MagicMobEffect(MobEffectCategory.HARMFUL, 0xbd0100));
    public static final DeferredHolder<MobEffect, MagicMobEffect> CURSE_OF_SOUL_BINDING = EFFECTS.register("curse_of_soul_binding", () -> new CurseMobEffect(MobEffectCategory.HARMFUL, 0x0f000f));
    public static final DeferredHolder<MobEffect, MagicMobEffect> PARALYSIS = EFFECTS.register("paralysis", () -> new MagicEffectParticlesMobEffect(MobEffectCategory.HARMFUL, 0xFFFF00));
    public static final DeferredHolder<MobEffect, MagicMobEffect> MUFFLE = EFFECTS.register("muffle", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0x4464d9));
    public static final DeferredHolder<MobEffect, MagicMobEffect> WARD = EFFECTS.register("ward", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0xc991d0));
    public static final DeferredHolder<MobEffect, MagicMobEffect> SLOW_TIME = EFFECTS.register("slow_time", () -> new SlowTimePotion(MobEffectCategory.BENEFICIAL, 0x5be3bb));
    public static final DeferredHolder<MobEffect, MagicMobEffect> EMPOWERMENT = EFFECTS.register("empowerment", () -> new MagicMobEffect(MobEffectCategory.BENEFICIAL, 0x8367bd));
    public static final DeferredHolder<MobEffect, CurseEnfeeblementMobEffect> CURSE_OF_ENFEEBLEMENT = EFFECTS.register("curse_of_enfeeblement", () -> new CurseEnfeeblementMobEffect(MobEffectCategory.HARMFUL, 0x36000b));
    public static final DeferredHolder<MobEffect, CurseUndeathMobEffect> CURSE_OF_UNDEATH = EFFECTS.register("curse_of_undeath", () -> new CurseUndeathMobEffect(MobEffectCategory.HARMFUL, 0x685c00));
    public static final DeferredHolder<MobEffect, MagicMobEffect> CONTAINMENT = EFFECTS.register("containment", () -> new ContainmentPotion(MobEffectCategory.HARMFUL, 0x7988cc));
    public static final DeferredHolder<MobEffect, MagicMobEffect> FROST_STEP = EFFECTS.register("frost_step", () -> new FrostStepPotion(MobEffectCategory.BENEFICIAL, 0x88E1FF));
    public static final DeferredHolder<MobEffect, MagicMobEffect> MARK_OF_SACRIFICE = EFFECTS.register("mark_of_sacrifice", () -> new MagicMobEffect(MobEffectCategory.HARMFUL, 0xe90e48));
    public static final DeferredHolder<MobEffect, MagicEffectParticlesMobEffect> MIRAGE = EFFECTS.register("mirage", () -> new MagicEffectParticlesMobEffect(MobEffectCategory.BENEFICIAL, 0xEE82EE));
    public static final DeferredHolder<MobEffect, MagicMobEffect> OAK_FLESH = EFFECTS.register("oak_flesh", () -> new OakFleshMobEffect(MobEffectCategory.BENEFICIAL, 0x7d5d3d));
    public static final DeferredHolder<MobEffect, MagicMobEffect> IRON_FLESH = EFFECTS.register("iron_flesh", () -> new IronFleshMobEffect(MobEffectCategory.BENEFICIAL, 0xd8d8d8));
    public static final DeferredHolder<MobEffect, MagicMobEffect> DIAMOND_FLESH = EFFECTS.register("diamond_flesh", () -> new DiamondFleshMobEffect(MobEffectCategory.BENEFICIAL, 0x5decf5));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
