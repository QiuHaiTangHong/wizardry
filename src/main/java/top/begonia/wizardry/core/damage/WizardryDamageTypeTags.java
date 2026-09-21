package top.begonia.wizardry.core.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

public final class WizardryDamageTypeTags {
    public static final TagKey<DamageType> MAGIC = create("magic");
    public static final TagKey<DamageType> FIRE = create("fire");
    public static final TagKey<DamageType> FROST = create("frost");
    public static final TagKey<DamageType> SHOCK = create("shock");
    public static final TagKey<DamageType> WITHER = create("wither");
    public static final TagKey<DamageType> POISON = create("poison");
    public static final TagKey<DamageType> FORCE = create("force");
    public static final TagKey<DamageType> BLAST = create("blast");
    public static final TagKey<DamageType> RADIANT = create("radiant");

    private static @NonNull TagKey<DamageType> create(String name) {
        return TagKey.create(
                Registries.DAMAGE_TYPE,
                Identifier.fromNamespaceAndPath(Wizardry.MODID, name)
        );
    }

    private WizardryDamageTypeTags() {}
}
