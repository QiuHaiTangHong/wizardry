package top.begonia.wizardry.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.entity.*;
import top.begonia.wizardry.core.entity.construct.*;
import top.begonia.wizardry.core.entity.living.minion.*;
import top.begonia.wizardry.core.entity.projectile.arrow.MagicMissileEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.FireBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.PoisonBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.SmokeBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.SparkBombEntity;

public final class WizardryEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Wizardry.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<ZombieMinionEntity>> ZOMBIE_MINION = register(
            "zombie_minion",
            EntityType.Builder
                    .<ZombieMinionEntity>of(ZombieMinionEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .eyeHeight(1.74F)
                    .passengerAttachments(new float[]{2.0125F})
                    .ridingOffset(-0.7F)
                    .clientTrackingRange(8)
                    .notInPeaceful()
    );
    public static final DeferredHolder<EntityType<?>, EntityType<HuskMinionEntity>> HUSK_MINION = register(
            "husk_minion",
            EntityType.Builder
                    .<HuskMinionEntity>of(HuskMinionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SkeletonMinionEntity>> SKELETON_MINION = register(
            "skeleton_minion",
            EntityType.Builder
                    .<SkeletonMinionEntity>of(SkeletonMinionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<StrayMinionEntity>> STRAY_MINION = register(
            "stray_minion",
            EntityType.Builder
                    .<StrayMinionEntity>of(StrayMinionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SpiderMinionEntity>> SPIDER_MINION = register(
            "spider_minion",
            EntityType.Builder
                    .of(SpiderMinionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<BlazeMinionEntity>> BLAZE_MINION = register(
            "blaze_minion",
            EntityType.Builder
                    .of(BlazeMinionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<WitherSkeletonMinionEntity>> WITHER_SKELETON_MINION = register(
            "wither_skeleton_minion",
            EntityType.Builder
                    .<WitherSkeletonMinionEntity>of(WitherSkeletonMinionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SilverfishMinionEntity>> SILVERFISH_MINION = register(
            "silverfish_minion",
            EntityType.Builder
                    .of(SilverfishMinionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<VexMinionEntity>> VEX_MINION = register(
            "vex_minion",
            EntityType.Builder
                    .of(VexMinionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<IceWraithEntity>> ICE_WRAITH = register(
            "ice_wraith",
            EntityType.Builder
                    .of(IceWraithEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<LightningWraithEntity>> LIGHTNING_WRAITH = register(
            "lightning_wraith",
            EntityType.Builder
                    .of(LightningWraithEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SpiritWolfEntity>> SPIRIT_WOLF = register(
            "spirit_wolf",
            EntityType.Builder
                    .of(SpiritWolfEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SpiritHorseEntity>> SPIRIT_HORSE = register(
            "spirit_horse",
            EntityType.Builder
                    .of(SpiritHorseEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<PhoenixEntity>> PHOENIX = register(
            "phoenix",
            EntityType.Builder
                    .of(PhoenixEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<IceGiantEntity>> ICE_GIANT = register(
            "ice_giant",
            EntityType.Builder
                    .of(IceGiantEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SpectralGolemEntity>> SPECTRAL_GOLEM = register(
            "spectral_golem",
            EntityType.Builder
                    .of(SpectralGolemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<MagicSlimeEntity>> MAGIC_SLIME = register(
            "magic_slime",
            EntityType.Builder
                    .of(MagicSlimeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<DecoyEntity>> DECOY = register(
            "decoy",
            EntityType.Builder
                    .of(DecoyEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ShadowWraithEntity>> SHADOW_WRAITH = register(
            "shadow_wraith",
            EntityType.Builder
                    .of(ShadowWraithEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<StormElementalEntity>> STORM_ELEMENTAL = register(
            "storm_elemental",
            EntityType.Builder
                    .of(StormElementalEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<WizardEntity>> WIZARD = register(
            "wizard",
            EntityType.Builder
                    .of(WizardEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<EvilWizardEntity>> EVIL_WIZARD = register(
            "evil_wizard",
            EntityType.Builder
                    .of(EvilWizardEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<RemnantEntity>> REMNANT = register(
            "remnant",
            EntityType.Builder
                    .of(RemnantEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<MagicMissileEntity>> MAGIC_MISSILE = register(
            "magic_missile",
            EntityType.Builder
                    .<MagicMissileEntity>of(MagicMissileEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<IceShardEntity>> ICE_SHARD = register(
            "ice_shard",
            EntityType.Builder
                    .of(IceShardEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<LightningArrowEntity>> LIGHTNING_ARROW = register(
            "lightning_arrow",
            EntityType.Builder
                    .of(LightningArrowEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ForceArrowEntity>> FORCE_ARROW = register(
            "force_arrow",
            EntityType.Builder
                    .of(ForceArrowEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<DartEntity>> DART = register(
            "dart",
            EntityType.Builder
                    .of(DartEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<IceLanceEntity>> ICE_LANCE = register(
            "ice_lance",
            EntityType.Builder
                    .of(IceLanceEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<FlamecatcherArrowEntity>> FLAMECATCHER_ARROW = register(
            "flamecatcher_arrow",
            EntityType.Builder
                    .of(FlamecatcherArrowEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ConjuredArrowEntity>> CONJURED_ARROW = register(
            "conjured_arrow",
            EntityType.Builder
                    .of(ConjuredArrowEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<FireBombEntity>> FIRE_BOMB = register(
            "fire_bomb",
            EntityType.Builder
                    .<FireBombEntity>of(FireBombEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<PoisonBombEntity>> POISON_BOMB = register(
            "poison_bomb",
            EntityType.Builder
                    .<PoisonBombEntity>of(PoisonBombEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SparkBombEntity>> SPARK_BOMB = register(
            "spark_bomb",
            EntityType.Builder
                    .<SparkBombEntity>of(SparkBombEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SmokeBombEntity>> SMOKE_BOMB = register(
            "smoke_bomb",
            EntityType.Builder
                    .<SmokeBombEntity>of(SmokeBombEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<IceChargeEntity>> ICE_CHARGE = register(
            "ice_charge",
            EntityType.Builder
                    .of(IceChargeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ForceOrbEntity>> FORCE_ORB = register(
            "force_orb",
            EntityType.Builder
                    .of(ForceOrbEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<SparkEntity>> SPARK = register(
            "spark",
            EntityType.Builder
                    .of(SparkEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<DarknessOrbEntity>> DARKNESS_ORB = register(
            "darkness_orb",
            EntityType.Builder
                    .of(DarknessOrbEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<FireboltEntity>> FIREBOLT = register(
            "firebolt",
            EntityType.Builder
                    .of(FireboltEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ThunderboltEntity>> THUNDERBOLT = register(
            "thunderbolt",
            EntityType.Builder
                    .of(ThunderboltEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<LightningDiscEntity>> LIGHTNING_DISC = register(
            "lightning_disc",
            EntityType.Builder
                    .of(LightningDiscEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<EmberEntity>> EMBER = register(
            "ember",
            EntityType.Builder
                    .of(EmberEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<MagicFireballEntity>> MAGIC_FIREBALL = register(
            "magic_fireball",
            EntityType.Builder
                    .of(MagicFireballEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<LargeMagicFireballEntity>> LARGE_MAGIC_FIREBALL = register(
            "large_magic_fireball",
            EntityType.Builder
                    .of(LargeMagicFireballEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<IceballEntity>> ICEBALL = register(
            "iceball",
            EntityType.Builder
                    .of(IceballEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<MeteorEntity>> METEOR = register(
            "meteor",
            EntityType.Builder
                    .of(MeteorEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<LightningHammerEntity>> LIGHTNING_HAMMER = register(
            "lightning_hammer",
            EntityType.Builder
                    .of(LightningHammerEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<LevitatingBlockEntity>> LEVITATING_BLOCK = register(
            "levitating_block",
            EntityType.Builder
                    .of(LevitatingBlockEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<BlackHoleEntity>> BLACK_HOLE = register(
            "black_hole",
            EntityType.Builder
                    .<BlackHoleEntity>of(BlackHoleEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<BlizzardEntity>> BLIZZARD = register(
            "blizzard",
            EntityType.Builder
                    .of(BlizzardEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ForcefieldEntity>> FORCEFIELD = register(
            "forcefield",
            EntityType.Builder
                    .of(ForcefieldEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<FireSigilEntity>> FIRE_SIGIL = register(
            "fire_sigil",
            EntityType.Builder
                    .of(FireSigilEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<FrostSigilEntity>> FROST_SIGIL = register(
            "frost_sigil",
            EntityType.Builder
                    .of(FrostSigilEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<LightningSigilEntity>> LIGHTNING_SIGIL = register(
            "lightning_sigil",
            EntityType.Builder
                    .of(LightningSigilEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<CombustionRuneEntity>> COMBUSTION_RUNE = register(
            "combustion_rune",
            EntityType.Builder
                    .of(CombustionRuneEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<RingOfFireEntity>> RING_OF_FIRE = register(
            "ring_of_fire",
            EntityType.Builder
                    .of(RingOfFireEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<HealingAuraEntity>> HEALING_AURA = register(
            "healing_aura",
            EntityType.Builder
                    .of(HealingAuraEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<DecayEntity>> DECAY = register(
            "decay",
            EntityType.Builder
                    .<DecayEntity>of(DecayEntity::new, MobCategory.MISC)
                    .sized(2.0F, 0.2F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ZombieSpawnerEntity>> ZOMBIE_SPAWNER = register(
            "zombie_spawner",
            EntityType.Builder
                    .<ZombieSpawnerEntity>of(ZombieSpawnerEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<RadiantTotemEntity>> RADIANT_TOTEM = register(
            "radiant_totem",
            EntityType.Builder
                    .of(RadiantTotemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<WitheringTotemEntity>> WITHERING_TOTEM = register(
            "withering_totem",
            EntityType.Builder
                    .of(WitheringTotemEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ArrowRainEntity>> ARROW_RAIN = register(
            "arrow_rain",
            EntityType.Builder
                    .of(ArrowRainEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<EarthquakeEntity>> EARTHQUAKE = register(
            "earthquake",
            EntityType.Builder
                    .of(EarthquakeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<HailstormEntity>> HAILSTORM = register(
            "hailstorm",
            EntityType.Builder
                    .of(HailstormEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<StormcloudEntity>> STORMCLOUD = register(
            "stormcloud",
            EntityType.Builder
                    .of(StormcloudEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<ShieldEntity>> SHIELD = register(
            "shield",
            EntityType.Builder
                    .of(ShieldEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<BubbleEntity>> BUBBLE = register(
            "bubble",
            EntityType.Builder
                    .<BubbleEntity>of(BubbleEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .setShouldReceiveVelocityUpdates(false)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<TornadoEntity>> TORNADO = register(
            "tornado",
            EntityType.Builder
                    .of(TornadoEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<IceSpikeEntity>> ICE_SPIKE = register(
            "ice_spike",
            EntityType.Builder
                    .of(IceSpikeEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<BoulderEntity>> BOULDER = register(
            "boulder",
            EntityType.Builder
                    .of(BoulderEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );
    public static final DeferredHolder<EntityType<?>, EntityType<IceBarrierEntity>> ICE_BARRIER = register(
            "ice_barrier",
            EntityType.Builder
                    .of(IceBarrierEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );

    private static <T extends Entity> @NonNull DeferredHolder<EntityType<?>, EntityType<T>> register(
            String modEntityId,
            EntityType.Builder<T> builder
    ) {
        ResourceKey<EntityType<?>> resourceKey = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Wizardry.MODID, modEntityId));
        return ENTITIES.register(resourceKey.identifier().getPath(), () -> builder.build(resourceKey));
    }

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
