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
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.entity.projectile.arrow.MagicMissileEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.FireBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.PoisonBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.SmokeBombEntity;
import top.begonia.wizardry.core.entity.projectile.bomb.SparkBombEntity;

public final class WizardryEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Wizardry.MODID);

    private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(
            String modEntityId,
            EntityType.Builder<T> builder
    ) {
        ResourceKey<EntityType<?>> resourceKey = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Wizardry.MODID, modEntityId));
        return ENTITIES.register(resourceKey.identifier().getPath(), () -> builder.build(resourceKey));
    }

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

    public static final DeferredHolder<EntityType<?>, EntityType<SmokeBombEntity>> SMOKE_BOMB = register(
            "smoke_bomb",
            EntityType.Builder
                    .<SmokeBombEntity>of(SmokeBombEntity::new, MobCategory.MISC)
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

    public static final DeferredHolder<EntityType<?>, EntityType<MagicMissileEntity>> MAGIC_MISSILE = register(
            "magic_missile",
            EntityType.Builder
                    .of(MagicMissileEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
    );

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
