package top.begonia.wizardry.core.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import top.begonia.wizardry.Wizardry;

public final class WizardryDamageType {
    public static final ResourceKey<DamageType> MAGIC = createKey("magic");
    public static final ResourceKey<DamageType> FIRE = createKey("fire");
    public static final ResourceKey<DamageType> FROST = createKey("frost");
    public static final ResourceKey<DamageType> SHOCK = createKey("shock");
    public static final ResourceKey<DamageType> WITHER = createKey("wither");
    public static final ResourceKey<DamageType> POISON = createKey("poison");
    public static final ResourceKey<DamageType> FORCE = createKey("force");
    public static final ResourceKey<DamageType> BLAST = createKey("blast");
    public static final ResourceKey<DamageType> RADIANT = createKey("radiant");

    private static ResourceKey<DamageType> createKey(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Wizardry.MODID, path));
    }
}
