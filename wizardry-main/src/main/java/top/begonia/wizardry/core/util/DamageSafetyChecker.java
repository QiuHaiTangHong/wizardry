package top.begonia.wizardry.core.util;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.stream.Collectors;

public final class DamageSafetyChecker {
    private static final int EXCESSIVE_CALL_THRESHOLD = 15;
    private static final int EXCESSIVE_CALL_LIMIT = 25;
    private static final int attacksThisTick = 0;
    private static Set<ResourceKey<DamageType>> VANILLA_DAMAGES;

    public static void updateVanillaDamages(@NonNull RegistryAccess registryAccess) {
        Registry<DamageType> damageTypeRegistry = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE);
        VANILLA_DAMAGES = damageTypeRegistry.keySet().stream()
                .filter(identifier -> identifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE))
                .map(identifier -> ResourceKey.create(Registries.DAMAGE_TYPE, identifier))
                .collect(Collectors.toUnmodifiableSet());
    }
}
