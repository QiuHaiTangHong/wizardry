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

    public static void register(IEventBus modBus) {
        SPELLS.makeRegistry(builder -> builder
                .sync(true)
                .maxId(65535)
        );
        SPELLS.register(modBus);

    }
}