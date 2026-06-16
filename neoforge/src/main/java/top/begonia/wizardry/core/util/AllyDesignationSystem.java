package top.begonia.wizardry.core.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.data.player.WizardPlayerDataOperator;
import top.begonia.wizardry.core.registry.WizardryMobEffects;

@EventBusSubscriber(modid = Wizardry.MODID)
public final class AllyDesignationSystem {
    private AllyDesignationSystem() {
    }

    public enum FriendlyFire {

        ALL("All", false, false),
        ONLY_PLAYERS("Only players", false, true),
        ONLY_OWNED("Only summoned/tamed creatures", true, false),
        NONE("None", true, true);

        public static final String[] names;

        static {
            names = new String[values().length];
            for (FriendlyFire setting : values()) {
                names[setting.ordinal()] = setting.name;
            }
        }

        public final String name;
        public final boolean blockPlayers;
        public final boolean blockOwned;

        FriendlyFire(String name, boolean blockPlayers, boolean blockOwned) {
            this.name = name;
            this.blockPlayers = blockPlayers;
            this.blockOwned = blockOwned;
        }

        public static FriendlyFire fromName(String name) {

            for (FriendlyFire setting : values()) {
                if (setting.name.equalsIgnoreCase(name)) return setting;
            }

            Wizardry.LOGGER.info("Invalid string for the friendly fire setting. Using default (all) instead.");
            return ALL;
        }

    }

    public static boolean isValidTarget(Entity attacker, Entity target) {
        if (attacker instanceof OwnableEntity ownable) {
            if (!isValidTarget(ownable.getOwner(), target)) {
                return false;
            }
        }
        if (target == null) return false;
        if (attacker == null) return true;
        if (target == attacker) return false;
        if (target instanceof FakePlayer) {
            return false;
        }
//        if (Wizardry.settings.passiveMobsAreAllies) {
//            MobCategory category = target.getType().getCategory();
//            if (category == MobCategory.AMBIENT
//                    || category == MobCategory.CREATURE
//                    || category == MobCategory.WATER_CREATURE) {
//                return false;
//            }
//        }
        if (target instanceof OwnableEntity pet && pet.getOwner() == attacker) {
            return false;
        }
        if (target instanceof OwnableEntity targetPet && attacker instanceof Mob mobAttacker) {
            Entity petOwner = targetPet.getOwner();
//            if (mobAttacker.getRevengeTarget() != petOwner && mobAttacker.getTarget() != petOwner) {
//                return false;
//            }
        }
        final String mindControlKey = "wizardry:controller_uuid";
        if (target instanceof Mob mobTarget && mobTarget.hasEffect(WizardryMobEffects.MIND_CONTROL)) {
            CompoundTag entityNBT = mobTarget.getPersistentData(); // getEntityData() ➜ getPersistentData()
//            if (entityNBT.hasUUID(mindControlKey)) {
//                UUID controllerUuid = entityNBT.getUUID(mindControlKey);
//                if (mobTarget.level() instanceof ServerLevel serverLevel) {
//                    Entity controller = serverLevel.getEntity(controllerUuid);
//                    if (attacker == controller) {
//                        return false;
//                    }
//                }
//            }
        }
        if (attacker instanceof Player playerAttacker) {
            WizardPlayerDataOperator operator = WizardPlayerDataOperator.get(playerAttacker);
            switch (target) {
                case Player playerTarget -> {
                    return !operator.isPlayerAlly(playerTarget);
                }
                case OwnableEntity petTarget -> {
                    return !isOwnerAlly(playerAttacker, petTarget);
                }
                case Mob mobTarget when mobTarget.hasEffect(WizardryMobEffects.MIND_CONTROL) -> {
                    CompoundTag entityNBT = mobTarget.getPersistentData();
//                    if (entityNBT.hasUUID(mindControlKey)) {
//                        UUID controllerUuid = entityNBT.getUUID(mindControlKey);
//                        if (mobTarget.level() instanceof ServerLevel serverLevel) {
//                            Entity controller = serverLevel.getEntity(controllerUuid);
//                            if (controller instanceof Player controllerPlayer && operator.isPlayerAlly(controllerPlayer)) {
//                                return false;
//                            }
//                        }
//                    }
                }
                default -> {
                }
            }
        }

        return true;
    }

    public static boolean isAllied(LivingEntity allyOf, LivingEntity possibleAlly) {
        if (allyOf == null || possibleAlly == null) {
            return false;
        }
        if (allyOf instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner instanceof LivingEntity livingOwner) {
                if (livingOwner == possibleAlly || isAllied(livingOwner, possibleAlly)) {
                    return true;
                }
            }
        }
        if (allyOf instanceof Player player && possibleAlly instanceof Player possiblePlayer) {
            if (isPlayerAlly(player, possiblePlayer)) {
                return true;
            }
        }
        if (possibleAlly instanceof OwnableEntity pet) {
            if (pet.getOwner() == allyOf) {
                return true;
            }
            return allyOf instanceof Player player && isOwnerAlly(player, pet);
        }
//        if (possibleAlly instanceof Mob mob) {
//            if (mob.hasEffect(WizardryMobEffects.MIND_CONTROL)) {
//                CompoundTag entityNBT = mob.getPersistentData();
//                String nbtKey = "wizardry:controller_uuid";
//                if (entityNBT.hasUUID(nbtKey)) {
//                    UUID controllerUuid = entityNBT.getUUID(nbtKey);
//                    if (mob.level() instanceof ServerLevel serverLevel) {
//                        Entity controller = serverLevel.getEntity(controllerUuid);
//                        return controller == allyOf;
//                    }
//                }
//            }
//        }
        return false;
    }

    public static boolean isPlayerAlly(Player allyOf, Player possibleAlly) {
        WizardPlayerDataOperator operator = WizardPlayerDataOperator.get(allyOf);
        return operator.isPlayerAlly(possibleAlly);
    }

    public static boolean isOwnerAlly(Player allyOf, @NonNull OwnableEntity ownable) {
        WizardPlayerDataOperator operator = WizardPlayerDataOperator.get(allyOf);
        Entity owner = ownable.getOwner();
        if (ownable.getOwner() == null) {
            return false;
        }
        if (owner instanceof Player playerOwner) {
            return operator.isPlayerAlly(playerOwner);
        } else {
            return operator.isPlayerAlly(ownable.getOwner().getUUID());
        }
    }
}
