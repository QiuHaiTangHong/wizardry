package top.begonia.wizardry.core.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.entity.projectile.arrow.MagicMissileEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.FireBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.PoisonBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.SmokeBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.SparkBombEntity;
import top.begonia.wizardry.core.spell.AbstractSpell;
import top.begonia.wizardry.core.spell.impl.*;
import top.begonia.wizardry.core.spell.impl.projectile.ProjectileSpell;

public final class WizardrySpells {
    public static final ResourceKey<Registry<AbstractSpell>> SPELLS_KEY = ResourceKey.createRegistryKey(
            Identifier.fromNamespaceAndPath(Wizardry.MODID, "spells")
    );
    public static final DeferredRegister<AbstractSpell> SPELLS = DeferredRegister.create(SPELLS_KEY, Wizardry.MODID);

    public static final DeferredHolder<AbstractSpell, None> NONE = SPELLS.register("none", None::new);
    public static final DeferredHolder<AbstractSpell, ArrowSpell<MagicMissileEntity>> MAGIC_MISSILE = SPELLS.register(
            "magic_missile",
            () -> {
                ArrowSpell<MagicMissileEntity> spell = new ArrowSpell<>(
                        Identifier.fromNamespaceAndPath(Wizardry.MODID, "magic_missile"),
                        WizardryEntities.MAGIC_MISSILE,
                        MagicMissileEntity::new
                );
                spell.soundValues(1, 1.4f, 0.4f);
                return spell;
            }
    );
    public static final DeferredHolder<AbstractSpell, None> IGNITE = SPELLS.register("ignite", None::new);
    public static final DeferredHolder<AbstractSpell, None> FREEZE = SPELLS.register("freeze", None::new);
    public static final DeferredHolder<AbstractSpell, None> SNOWBALL = SPELLS.register("snowball", None::new);
    public static final DeferredHolder<AbstractSpell, None> ARC = SPELLS.register("arc", None::new);
    public static final DeferredHolder<AbstractSpell, None> THUNDERBOLT = SPELLS.register("thunderbolt", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_ZOMBIE = SPELLS.register("summon_zombie", None::new);
    public static final DeferredHolder<AbstractSpell, None> SNARE = SPELLS.register("snare", None::new);
    public static final DeferredHolder<AbstractSpell, None> DART = SPELLS.register("dart", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHT = SPELLS.register("light", None::new);
    public static final DeferredHolder<AbstractSpell, None> TELEKINESIS = SPELLS.register("telekinesis", None::new);
    public static final DeferredHolder<AbstractSpell, None> HEAL = SPELLS.register("heal", None::new);
    public static final DeferredHolder<AbstractSpell, None> FIREBALL = SPELLS.register("fireball", None::new);
    public static final DeferredHolder<AbstractSpell, None> FLAME_RAY = SPELLS.register("flame_ray", None::new);
    public static final DeferredHolder<AbstractSpell, ProjectileSpell<FireBombEntity>> FIRE_BOMB = SPELLS.register(
            "fire_bomb",
            () -> {
                ProjectileSpell<FireBombEntity> spell = new ProjectileSpell<>(
                        Identifier.fromNamespaceAndPath(Wizardry.MODID, "fire_bomb"),
                        WizardryEntities.FIRE_BOMB,
                        () -> new ItemStack(WizardryItems.FIRE_BOMB.get()),
                        FireBombEntity::new
                );
                spell.soundValues(0.5f, 0.4f, 0.2f);
                return spell;
            }
    );
    public static final DeferredHolder<AbstractSpell, None> FIRE_SIGIL = SPELLS.register("fire_sigil", None::new);
    public static final DeferredHolder<AbstractSpell, None> FIREBOLT = SPELLS.register("firebolt", None::new);
    public static final DeferredHolder<AbstractSpell, None> FROST_RAY = SPELLS.register("frost_ray", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_SNOW_GOLEM = SPELLS.register("summon_snow_golem", None::new);
    public static final DeferredHolder<AbstractSpell, None> ICE_SHARD = SPELLS.register("ice_shard", None::new);
    public static final DeferredHolder<AbstractSpell, None> ICE_STATUE = SPELLS.register("ice_statue", None::new);
    public static final DeferredHolder<AbstractSpell, None> FROST_SIGIL = SPELLS.register("frost_sigil", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHTNING_RAY = SPELLS.register("lightning_ray", None::new);
    public static final DeferredHolder<AbstractSpell, ProjectileSpell<SparkBombEntity>> SPARK_BOMB = SPELLS.register(
            "spark_bomb",
            () -> {
                ProjectileSpell<SparkBombEntity> spell = new ProjectileSpell<>(
                        Identifier.fromNamespaceAndPath(Wizardry.MODID, "spark_bomb"),
                        WizardryEntities.SPARK_BOMB,
                        () -> new ItemStack(WizardryItems.SPARK_BOMB.get()),
                        SparkBombEntity::new
                );
                spell.soundValues(0.5f, 0.4f, 0.2f);
                return spell;
            }
    );
    public static final DeferredHolder<AbstractSpell, None> HOMING_SPARK = SPELLS.register("homing_spark", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHTNING_SIGIL = SPELLS.register("lightning_sigil", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHTNING_ARROW = SPELLS.register("lightning_arrow", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIFE_DRAIN = SPELLS.register("life_drain", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_SKELETON = SPELLS.register("summon_skeleton", None::new);
    public static final DeferredHolder<AbstractSpell, None> METAMORPHOSIS = SPELLS.register("metamorphosis", None::new);
    public static final DeferredHolder<AbstractSpell, None> WITHER = SPELLS.register("wither", None::new);
    public static final DeferredHolder<AbstractSpell, None> POISON = SPELLS.register("poison", None::new);
    public static final DeferredHolder<AbstractSpell, None> GROWTH_AURA = SPELLS.register("growth_aura", None::new);
    public static final DeferredHolder<AbstractSpell, None> BUBBLE = SPELLS.register("bubble", None::new);
    public static final DeferredHolder<AbstractSpell, None> WHIRLWIND = SPELLS.register("whirlwind", None::new);
    public static final DeferredHolder<AbstractSpell, ProjectileSpell<PoisonBombEntity>> POISON_BOMB = SPELLS.register(
            "poison_bomb",
            () -> {
                ProjectileSpell<PoisonBombEntity> spell = new ProjectileSpell<>(
                        Identifier.fromNamespaceAndPath(Wizardry.MODID, "poison_bomb"),
                        WizardryEntities.POISON_BOMB,
                        () -> new ItemStack(WizardryItems.POISON_BOMB.get()),
                        PoisonBombEntity::new
                );
                spell.soundValues(0.5f, 0.4f, 0.2f);
                return spell;
            }
    );
    public static final DeferredHolder<AbstractSpell, None> SUMMON_SPIRIT_WOLF = SPELLS.register("summon_spirit_wolf", None::new);
    public static final DeferredHolder<AbstractSpell, None> BLINK = SPELLS.register("blink", None::new);
    public static final DeferredHolder<AbstractSpell, None> AGILITY = SPELLS.register("agility", None::new);
    public static final DeferredHolder<AbstractSpell, None> CONJURE_SWORD = SPELLS.register("conjure_sword", None::new);
    public static final DeferredHolder<AbstractSpell, None> CONJURE_PICKAXE = SPELLS.register("conjure_pickaxe", None::new);
    public static final DeferredHolder<AbstractSpell, None> CONJURE_BOW = SPELLS.register("conjure_bow", None::new);
    public static final DeferredHolder<AbstractSpell, None> FORCE_ARROW = SPELLS.register("force_arrow", None::new);
    public static final DeferredHolder<AbstractSpell, None> SHIELD = SPELLS.register("shield", None::new);
    public static final DeferredHolder<AbstractSpell, None> REPLENISH_HUNGER = SPELLS.register("replenish_hunger", None::new);
    public static final DeferredHolder<AbstractSpell, None> CURE_EFFECTS = SPELLS.register("cure_effects", None::new);
    public static final DeferredHolder<AbstractSpell, None> HEAL_ALLY = SPELLS.register("heal_ally", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_BLAZE = SPELLS.register("summon_blaze", None::new);
    public static final DeferredHolder<AbstractSpell, None> RING_OF_FIRE = SPELLS.register("ring_of_fire", None::new);
    public static final DeferredHolder<AbstractSpell, None> DETONATE = SPELLS.register("detonate", None::new);
    public static final DeferredHolder<AbstractSpell, None> FIRE_RESISTANCE = SPELLS.register("fire_resistance", None::new);
    public static final DeferredHolder<AbstractSpell, None> FIRESKIN = SPELLS.register("fireskin", None::new);
    public static final DeferredHolder<AbstractSpell, None> FLAMING_AXE = SPELLS.register("flaming_axe", None::new);
    public static final DeferredHolder<AbstractSpell, None> BLIZZARD = SPELLS.register("blizzard", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_ICE_WRAITH = SPELLS.register("summon_ice_wraith", None::new);
    public static final DeferredHolder<AbstractSpell, None> ICE_SHROUD = SPELLS.register("ice_shroud", None::new);
    public static final DeferredHolder<AbstractSpell, None> ICE_CHARGE = SPELLS.register("ice_charge", None::new);
    public static final DeferredHolder<AbstractSpell, None> FROST_AXE = SPELLS.register("frost_axe", None::new);
    public static final DeferredHolder<AbstractSpell, None> INVOKE_WEATHER = SPELLS.register("invoke_weather", None::new);
    public static final DeferredHolder<AbstractSpell, None> CHAIN_LIGHTNING = SPELLS.register("chain_lightning", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHTNING_BOLT = SPELLS.register("lightning_bolt", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_LIGHTNING_WRAITH = SPELLS.register("summon_lightning_wraith", None::new);
    public static final DeferredHolder<AbstractSpell, None> STATIC_AURA = SPELLS.register("static_aura", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHTNING_DISC = SPELLS.register("lightning_disc", None::new);
    public static final DeferredHolder<AbstractSpell, None> MIND_CONTROL = SPELLS.register("mind_control", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_WITHER_SKELETON = SPELLS.register("summon_wither_skeleton", None::new);
    public static final DeferredHolder<AbstractSpell, None> ENTRAPMENT = SPELLS.register("entrapment", None::new);
    public static final DeferredHolder<AbstractSpell, None> WITHER_SKULL = SPELLS.register("wither_skull", None::new);
    public static final DeferredHolder<AbstractSpell, None> DARKNESS_ORB = SPELLS.register("darkness_orb", None::new);
    public static final DeferredHolder<AbstractSpell, None> SHADOW_WARD = SPELLS.register("shadow_ward", None::new);
    public static final DeferredHolder<AbstractSpell, None> DECAY = SPELLS.register("decay", None::new);
    public static final DeferredHolder<AbstractSpell, None> WATER_BREATHING = SPELLS.register("water_breathing", None::new);
    public static final DeferredHolder<AbstractSpell, None> TORNADO = SPELLS.register("tornado", None::new);
    public static final DeferredHolder<AbstractSpell, None> GLIDE = SPELLS.register("glide", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_SPIRIT_HORSE = SPELLS.register("summon_spirit_horse", None::new);
    public static final DeferredHolder<AbstractSpell, None> SPIDER_SWARM = SPELLS.register("spider_swarm", None::new);
    public static final DeferredHolder<AbstractSpell, None> SLIME = SPELLS.register("slime", None::new);
    public static final DeferredHolder<AbstractSpell, None> PETRIFY = SPELLS.register("petrify", None::new);
    public static final DeferredHolder<AbstractSpell, None> INVISIBILITY = SPELLS.register("invisibility", None::new);
    public static final DeferredHolder<AbstractSpell, None> LEVITATION = SPELLS.register("levitation", None::new);
    public static final DeferredHolder<AbstractSpell, None> FORCE_ORB = SPELLS.register("force_orb", None::new);
    public static final DeferredHolder<AbstractSpell, None> TRANSPORTATION = SPELLS.register("transportation", None::new);
    public static final DeferredHolder<AbstractSpell, None> SPECTRAL_PATHWAY = SPELLS.register("spectral_pathway", None::new);
    public static final DeferredHolder<AbstractSpell, None> PHASE_STEP = SPELLS.register("phase_step", None::new);
    public static final DeferredHolder<AbstractSpell, None> VANISHING_BOX = SPELLS.register("vanishing_box", None::new);
    public static final DeferredHolder<AbstractSpell, None> GREATER_HEAL = SPELLS.register("greater_heal", None::new);
    public static final DeferredHolder<AbstractSpell, None> HEALING_AURA = SPELLS.register("healing_aura", None::new);
    public static final DeferredHolder<AbstractSpell, None> FORCEFIELD = SPELLS.register("forcefield", None::new);
    public static final DeferredHolder<AbstractSpell, None> IRONFLESH = SPELLS.register("ironflesh", None::new);
    public static final DeferredHolder<AbstractSpell, None> TRANSIENCE = SPELLS.register("transience", None::new);
    public static final DeferredHolder<AbstractSpell, None> METEOR = SPELLS.register("meteor", None::new);
    public static final DeferredHolder<AbstractSpell, None> FIRE_BREATH = SPELLS.register("fire_breath", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_PHOENIX = SPELLS.register("summon_phoenix", None::new);
    public static final DeferredHolder<AbstractSpell, None> ICE_AGE = SPELLS.register("ice_age", None::new);
    public static final DeferredHolder<AbstractSpell, None> WALL_OF_FROST = SPELLS.register("wall_of_frost", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_ICE_GIANT = SPELLS.register("summon_ice_giant", None::new);
    public static final DeferredHolder<AbstractSpell, None> THUNDERSTORM = SPELLS.register("thunderstorm", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHTNING_HAMMER = SPELLS.register("lightning_hammer", None::new);
    public static final DeferredHolder<AbstractSpell, None> PLAGUE_OF_DARKNESS = SPELLS.register("plague_of_darkness", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_SKELETON_LEGION = SPELLS.register("summon_skeleton_legion", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_SHADOW_WRAITH = SPELLS.register("summon_shadow_wraith", None::new);
    public static final DeferredHolder<AbstractSpell, None> FORESTS_CURSE = SPELLS.register("forests_curse", None::new);
    public static final DeferredHolder<AbstractSpell, None> FLIGHT = SPELLS.register("flight", None::new);
    public static final DeferredHolder<AbstractSpell, None> SILVERFISH_SWARM = SPELLS.register("silverfish_swarm", None::new);
    public static final DeferredHolder<AbstractSpell, None> BLACK_HOLE = SPELLS.register("black_hole", None::new);
    public static final DeferredHolder<AbstractSpell, None> SHOCKWAVE = SPELLS.register("shockwave", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_IRON_GOLEM = SPELLS.register("summon_iron_golem", None::new);
    public static final DeferredHolder<AbstractSpell, None> ARROW_RAIN = SPELLS.register("arrow_rain", None::new);
    public static final DeferredHolder<AbstractSpell, None> DIAMONDFLESH = SPELLS.register("diamondflesh", None::new);
    public static final DeferredHolder<AbstractSpell, None> FONT_OF_VITALITY = SPELLS.register("font_of_vitality", None::new);
    public static final DeferredHolder<AbstractSpell, ProjectileSpell<SmokeBombEntity>> SMOKE_BOMB = SPELLS.register(
            "smoke_bomb",
            () -> {
                ProjectileSpell<SmokeBombEntity> spell = new ProjectileSpell<>(
                        Identifier.fromNamespaceAndPath(Wizardry.MODID, "smoke_bomb"),
                        WizardryEntities.SMOKE_BOMB,
                        () -> new ItemStack(WizardryItems.SMOKE_BOMB.get()),
                        SmokeBombEntity::new
                );
                spell.soundValues(0.5f, 0.4f, 0.2f);
                return spell;
            }
    );
    public static final DeferredHolder<AbstractSpell, None> MIND_TRICK = SPELLS.register("mind_trick", None::new);
    public static final DeferredHolder<AbstractSpell, None> LEAP = SPELLS.register("leap", None::new);
    public static final DeferredHolder<AbstractSpell, None> POCKET_FURNACE = SPELLS.register("pocket_furnace", None::new);
    public static final DeferredHolder<AbstractSpell, None> INTIMIDATE = SPELLS.register("intimidate", None::new);
    public static final DeferredHolder<AbstractSpell, None> BANISH = SPELLS.register("banish", None::new);
    public static final DeferredHolder<AbstractSpell, None> SIXTH_SENSE = SPELLS.register("sixth_sense", None::new);
    public static final DeferredHolder<AbstractSpell, None> DARKVISION = SPELLS.register("darkvision", None::new);
    public static final DeferredHolder<AbstractSpell, None> CLAIRVOYANCE = SPELLS.register("clairvoyance", None::new);
    public static final DeferredHolder<AbstractSpell, None> POCKET_WORKBENCH = SPELLS.register("pocket_workbench", None::new);
    public static final DeferredHolder<AbstractSpell, None> IMBUE_WEAPON = SPELLS.register("imbue_weapon", None::new);
    public static final DeferredHolder<AbstractSpell, None> INVIGORATING_PRESENCE = SPELLS.register("invigorating_presence", None::new);
    public static final DeferredHolder<AbstractSpell, None> OAKFLESH = SPELLS.register("oakflesh", None::new);
    public static final DeferredHolder<AbstractSpell, None> GREATER_FIREBALL = SPELLS.register("greater_fireball", None::new);
    public static final DeferredHolder<AbstractSpell, None> FLAMING_WEAPON = SPELLS.register("flaming_weapon", None::new);
    public static final DeferredHolder<AbstractSpell, None> ICE_LANCE = SPELLS.register("ice_lance", None::new);
    public static final DeferredHolder<AbstractSpell, None> FREEZING_WEAPON = SPELLS.register("freezing_weapon", None::new);
    public static final DeferredHolder<AbstractSpell, None> ICE_SPIKES = SPELLS.register("ice_spikes", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHTNING_PULSE = SPELLS.register("lightning_pulse", None::new);
    public static final DeferredHolder<AbstractSpell, None> CURSE_OF_SOULBINDING = SPELLS.register("curse_of_soulbinding", None::new);
    public static final DeferredHolder<AbstractSpell, None> COBWEBS = SPELLS.register("cobwebs", None::new);
    public static final DeferredHolder<AbstractSpell, None> DECOY = SPELLS.register("decoy", None::new);
    public static final DeferredHolder<AbstractSpell, None> CONJURE_ARMOUR = SPELLS.register("conjure_armour", None::new);
    public static final DeferredHolder<AbstractSpell, None> ARCANE_JAMMER = SPELLS.register("arcane_jammer", None::new);
    public static final DeferredHolder<AbstractSpell, None> GROUP_HEAL = SPELLS.register("group_heal", None::new);
    public static final DeferredHolder<AbstractSpell, None> HAILSTORM = SPELLS.register("hailstorm", None::new);
    public static final DeferredHolder<AbstractSpell, None> LIGHTNING_WEB = SPELLS.register("lightning_web", None::new);
    public static final DeferredHolder<AbstractSpell, None> SUMMON_STORM_ELEMENTAL = SPELLS.register("summon_storm_elemental", None::new);
    public static final DeferredHolder<AbstractSpell, None> EARTHQUAKE = SPELLS.register("earthquake", None::new);
    public static final DeferredHolder<AbstractSpell, None> FONT_OF_MANA = SPELLS.register("font_of_mana", None::new);
    public static final DeferredHolder<AbstractSpell, None> MINE = SPELLS.register("mine", None::new);
    public static final DeferredHolder<AbstractSpell, None> CONJURE_BLOCK = SPELLS.register("conjure_block", None::new);
    public static final DeferredHolder<AbstractSpell, None> MUFFLE = SPELLS.register("muffle", None::new);
    public static final DeferredHolder<AbstractSpell, None> WARD = SPELLS.register("ward", None::new);
    public static final DeferredHolder<AbstractSpell, None> EVADE = SPELLS.register("evade", None::new);
    public static final DeferredHolder<AbstractSpell, None> ICEBALL = SPELLS.register("iceball", None::new);
    public static final DeferredHolder<AbstractSpell, None> CHARGE = SPELLS.register("charge", None::new);
    public static final DeferredHolder<AbstractSpell, None> REVERSAL = SPELLS.register("reversal", None::new);
    public static final DeferredHolder<AbstractSpell, None> GRAPPLE = SPELLS.register("grapple", None::new);
    public static final DeferredHolder<AbstractSpell, None> DIVINATION = SPELLS.register("divination", None::new);
    public static final DeferredHolder<AbstractSpell, None> EMPOWERING_PRESENCE = SPELLS.register("empowering_presence", None::new);
    public static final DeferredHolder<AbstractSpell, None> DISINTEGRATION = SPELLS.register("disintegration", None::new);
    public static final DeferredHolder<AbstractSpell, None> COMBUSTION_RUNE = SPELLS.register("combustion_rune", None::new);
    public static final DeferredHolder<AbstractSpell, None> FROST_STEP = SPELLS.register("frost_step", None::new);
    public static final DeferredHolder<AbstractSpell, None> PARALYSIS = SPELLS.register("paralysis", None::new);
    public static final DeferredHolder<AbstractSpell, None> SHULKER_BULLET = SPELLS.register("shulker_bullet", None::new);
    public static final DeferredHolder<AbstractSpell, None> CURSE_OF_UNDEATH = SPELLS.register("curse_of_undeath", None::new);
    public static final DeferredHolder<AbstractSpell, None> DRAGON_FIREBALL = SPELLS.register("dragon_fireball", None::new);
    public static final DeferredHolder<AbstractSpell, None> GREATER_TELEKINESIS = SPELLS.register("greater_telekinesis", None::new);
    public static final DeferredHolder<AbstractSpell, None> VEX_SWARM = SPELLS.register("vex_swarm", None::new);
    public static final DeferredHolder<AbstractSpell, None> ARCANE_LOCK = SPELLS.register("arcane_lock", None::new);
    public static final DeferredHolder<AbstractSpell, None> CONTAINMENT = SPELLS.register("containment", None::new);
    public static final DeferredHolder<AbstractSpell, None> SATIETY = SPELLS.register("satiety", None::new);
    public static final DeferredHolder<AbstractSpell, None> GREATER_WARD = SPELLS.register("greater_ward", None::new);
    public static final DeferredHolder<AbstractSpell, None> RAY_OF_PURIFICATION = SPELLS.register("ray_of_purification", None::new);
    public static final DeferredHolder<AbstractSpell, None> REMOVE_CURSE = SPELLS.register("remove_curse", None::new);
    public static final DeferredHolder<AbstractSpell, None> POSSESSION = SPELLS.register("possession", None::new);
    public static final DeferredHolder<AbstractSpell, None> CURSE_OF_ENFEEBLEMENT = SPELLS.register("curse_of_enfeeblement", None::new);
    public static final DeferredHolder<AbstractSpell, None> FOREST_OF_THORNS = SPELLS.register("forest_of_thorns", None::new);
    public static final DeferredHolder<AbstractSpell, None> SPEED_TIME = SPELLS.register("speed_time", None::new);
    public static final DeferredHolder<AbstractSpell, None> SLOW_TIME = SPELLS.register("slow_time", None::new);
    public static final DeferredHolder<AbstractSpell, None> RESURRECTION = SPELLS.register("resurrection", None::new);
    public static final DeferredHolder<AbstractSpell, None> FROST_BARRIER = SPELLS.register("frost_barrier", None::new);
    public static final DeferredHolder<AbstractSpell, None> BLINDING_FLASH = SPELLS.register("blinding_flash", None::new);
    public static final DeferredHolder<AbstractSpell, None> ENRAGE = SPELLS.register("enrage", None::new);
    public static final DeferredHolder<AbstractSpell, None> MARK_SACRIFICE = SPELLS.register("mark_sacrifice", None::new);
    public static final DeferredHolder<AbstractSpell, None> PERMAFROST = SPELLS.register("permafrost", None::new);
    public static final DeferredHolder<AbstractSpell, None> STORMCLOUD = SPELLS.register("stormcloud", None::new);
    public static final DeferredHolder<AbstractSpell, None> WITHERING_TOTEM = SPELLS.register("withering_totem", None::new);
    public static final DeferredHolder<AbstractSpell, None> FANGS = SPELLS.register("fangs", None::new);
    public static final DeferredHolder<AbstractSpell, None> GUARDIAN_BEAM = SPELLS.register("guardian_beam", None::new);
    public static final DeferredHolder<AbstractSpell, None> MIRAGE = SPELLS.register("mirage", None::new);
    public static final DeferredHolder<AbstractSpell, None> RADIANT_TOTEM = SPELLS.register("radiant_totem", None::new);
    public static final DeferredHolder<AbstractSpell, None> FIRESTORM = SPELLS.register("firestorm", None::new);
    public static final DeferredHolder<AbstractSpell, None> FLAMECATCHER = SPELLS.register("flamecatcher", None::new);
    public static final DeferredHolder<AbstractSpell, None> ZOMBIE_APOCALYPSE = SPELLS.register("zombie_apocalypse", None::new);
    public static final DeferredHolder<AbstractSpell, None> BOULDER = SPELLS.register("boulder", None::new);
    public static final DeferredHolder<AbstractSpell, None> CELESTIAL_SMITE = SPELLS.register("celestial_smite", None::new);

    public static void register(IEventBus modBus) {
        SPELLS.makeRegistry(builder -> builder
                .sync(true)
                .maxId(65535)
        );
        SPELLS.register(modBus);

    }
}