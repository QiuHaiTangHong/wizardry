package top.begonia.wizardry.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.EnabledEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.damage.WizardryDamageTypes;
import top.begonia.wizardry.core.data.constant.WizardryServerDataManager;
import top.begonia.wizardry.core.data.constant.definition.DamageImmune;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class EntityUtils {
    private EntityUtils() {
    }

    @Contract(pure = true)
    public static int getDefaultAimingError(@NonNull Difficulty difficulty) {
        return switch (difficulty) {
            case NORMAL -> 6;
            case HARD -> 2;
            default -> 10;
        };
    }

    public static boolean isEntityImmune(
            @NonNull Holder<DamageType> typeHolder,
            Entity entity
    ) {
        if (typeHolder == WizardryDamageTypes.FIRE && entity.fireImmune()) {
            return true;
        }
        DamageImmune damageImmune = WizardryServerDataManager.getInstance()
                .getData(Identifier.fromNamespaceAndPath(Wizardry.MODID, "damage_immune/wizardry_damage_immune"), DamageImmune.class)
                .orElse(DamageImmune.DEFAULT);
        Map<Identifier, List<Identifier>> entities = damageImmune.entries();
        Identifier type = null;
        Identifier entityIdentifier = EntityUtils.getIdentifier(entity);
        if (typeHolder.unwrapKey().isPresent()) {
            type = typeHolder.unwrapKey().get().identifier();
        }
        return type != null && entities.get(type).contains(entityIdentifier);
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof LivingEntity && !(entity instanceof ArmorStand);
    }

    public static @NonNull List<LivingEntity> getLivingWithinCylinder(
            double radius,
            double x, double y, double z,
            double height,
            Level level
    ) {
        return getEntitiesWithinCylinder(radius, x, y, z, height, level, LivingEntity.class);
    }

    public static @NonNull List<LivingEntity> getLivingWithinCylinder(
            double radius,
            @NonNull Vec3 position,
            double height,
            Level level
    ) {
        return getEntitiesWithinCylinder(radius, position.x, position.y, position.z, height, level, LivingEntity.class);
    }

    @Contract("_, _, _, _, _, _, _ -> !null")
    public static <T extends Entity> @NonNull List<T> getEntitiesWithinCylinder(
            double radius,
            double x, double y, double z,
            double height,
            @NonNull Level level,
            Class<T> entityType
    ) {
        AABB aabb = new AABB(
                x - radius, y, z - radius,
                x + radius, y + height, z + radius
        );

        double radiusSqr = radius * radius;

        return level.getEntitiesOfClass(
                entityType,
                aabb,
                entity -> {
                    double dx = entity.getX() - x;
                    double dz = entity.getZ() - z;
                    return dx * dx + dz * dz <= radiusSqr;
                }
        );
    }

    public static boolean attackEntityWithoutKnockback(ServerLevel serverLevel, @NonNull Entity entity, DamageSource source, float amount) {
        Vec3 prevDeltaMove = entity.getDeltaMovement();
        boolean succeeded = entity.hurtServer(serverLevel, source, amount);
        entity.setDeltaMovement(prevDeltaMove);
        return succeeded;
    }

    public static @NonNull Identifier getIdentifier(@NonNull Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
    }

    public static @NonNull List<LivingEntity> getLivingWithinRadius(double radius, double x, double y, double z, Level level) {
        return getEntitiesWithinRadius(radius, x, y, z, level, LivingEntity.class);
    }

    public static <T extends Entity> @NonNull List<T> getEntitiesWithinRadius(double radius, double x, double y, double z, @NonNull Level level, Class<T> entityType) {
        AABB aabb = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        List<T> entityList = new ArrayList<>(level.getEntitiesOfClass(entityType, aabb));
        double radiusSqr = radius * radius;
        entityList.removeIf(entity -> entity.distanceToSqr(x, y, z) > radiusSqr);

        return entityList;
    }

    public static boolean canDamageBlocks(@Nullable Entity entity, ServerLevel serverLevel) {
        if (entity == null) {
            return ServerConfig.dispenserBlockDamage;
        } else if (entity instanceof Player player) {
            return player.mayBuild() && ServerConfig.playerBlockDamage;
        }
        return EventHooks.canEntityGrief(serverLevel, entity);
    }

    public static boolean isBlockUnbreakable(@NonNull Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.isAir() && state.getDestroySpeed(level, pos) < 0;
    }

    public static void undoGravity(@NonNull Entity entity) {
        if (!entity.isNoGravity()) {
            double gravity = 0.04;
            switch (entity) {
                case ThrowableProjectile _ -> gravity = 0.03;
                case AbstractArrow _ -> gravity = 0.05;
                case LivingEntity _ -> gravity = 0.08;
                default -> {
                }
            }
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x, motion.y + gravity, motion.z);
        }
    }

    public static TierEnum populateSpells(
            final LivingEntity wizard,
            List<AbstractSpell> spells,
            ElementEnum element,
            boolean master,
            int count,
            RandomSource random
    ) {
        TierEnum maxTier = TierEnum.NOVICE;
        List<AbstractSpell> npcSpells = WizardrySpells.getSpells(s -> s.canBeCastBy(wizard, false));
        for (int i = 0; i < count; i++) {
            TierEnum tier;
            ElementEnum finalElement = element == ElementEnum.MAGIC ? ElementEnum.values()[random.nextInt(ElementEnum.values().length)] : element;

            int randomiser = random.nextInt(20);

            if (randomiser < 10) {
                tier = TierEnum.NOVICE;
            } else if (randomiser < 16) {
                tier = TierEnum.APPRENTICE;
            } else if (randomiser < 19 || !master) {
                tier = TierEnum.ADVANCED;
            } else {
                tier = TierEnum.MASTER;
            }

            if (tier.ordinal() > maxTier.ordinal()) {
                maxTier = tier;
            }
            List<AbstractSpell> list = WizardrySpells.getSpells(new TierElementFilter(tier, finalElement, EnabledEnum.NPCS));
            list.retainAll(npcSpells);
            list.removeAll(spells);

            if (list.isEmpty()) {
                list = npcSpells;
                list.removeAll(spells);
            }

            if (!list.isEmpty()) {
                spells.add(list.get(random.nextInt(list.size())));
            }
        }
        return maxTier;
    }
}
