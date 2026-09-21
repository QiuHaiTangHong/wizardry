package top.begonia.wizardry.core.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;

import java.util.function.Function;

/**
 * 巫师模组伤害类型定义类
 * <p>集中管理和提供巫师模组 (Wizardry) 中所有自定义伤害类型的 {@link Holder} 引用.
 * 每个静态字段代表一种特定的伤害类型, 通过延迟求值的方式从游戏注册表中获取对应的 {@link DamageType} 对象.
 * <p>该类为工具类, 不可实例化, 所有伤害类型均通过静态字段直接访问.
 * <p>支持的伤害类型包括:
 * <ul>
 *   <li>{@link #MAGIC} - 魔法伤害</li>
 *   <li>{@link #FIRE} - 火焰伤害</li>
 *   <li>{@link #FROST} - 冰霜伤害</li>
 *   <li>{@link #SHOCK} - 雷电伤害</li>
 *   <li>{@link #WITHER} - 凋零伤害</li>
 *   <li>{@link #POISON} - 中毒伤害</li>
 *   <li>{@link #FORCE} - 力量伤害</li>
 *   <li>{@link #BLAST} - 爆炸伤害</li>
 *   <li>{@link #RADIANT} - 光耀伤害</li>
 * </ul>
 * <p>所有伤害类型均通过 {@link ResourceKey} 从注册表中查找, 确保与模组注册的伤害类型严格对应.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @since 1.0.0
 */
public final class WizardryDamageTypes {
    public static final Function<RegistryAccess, Holder<DamageType>> MAGIC = registryAccess -> getDamageType("magic", registryAccess);
    public static final Function<RegistryAccess, Holder<DamageType>> FIRE = registryAccess -> getDamageType("fire", registryAccess);
    public static final Function<RegistryAccess, Holder<DamageType>> FROST = registryAccess -> getDamageType("frost", registryAccess);
    public static final Function<RegistryAccess, Holder<DamageType>> SHOCK = registryAccess -> getDamageType("shock", registryAccess);
    public static final Function<RegistryAccess, Holder<DamageType>> WITHER = registryAccess -> getDamageType("wither", registryAccess);
    public static final Function<RegistryAccess, Holder<DamageType>> POISON = registryAccess -> getDamageType("poison", registryAccess);
    public static final Function<RegistryAccess, Holder<DamageType>> FORCE = registryAccess -> getDamageType("force", registryAccess);
    public static final Function<RegistryAccess, Holder<DamageType>> BLAST = registryAccess -> getDamageType("blast", registryAccess);
    public static final Function<RegistryAccess, Holder<DamageType>> RADIANT = registryAccess -> getDamageType("radiant", registryAccess);

    private WizardryDamageTypes() {
    }

    private static @NonNull Holder<DamageType> getDamageType(String name, @NonNull RegistryAccess registryAccess) {
        return registryAccess.getOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Wizardry.MODID, name)));
    }
}
