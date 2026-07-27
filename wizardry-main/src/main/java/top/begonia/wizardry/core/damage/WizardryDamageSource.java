package top.begonia.wizardry.core.damage;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class WizardryDamageSource extends DamageSource {
    private final boolean isRetaliatory;
    private final Entity minionEntity;

    /**
     * 私有构造方法, 用于创建魔法伤害源实例
     * <p> 该构造方法初始化伤害类型, 直接伤害来源实体, 间接伤害来源实体, 仆从实体以及是否为反击伤害的标志
     *
     * @param typeHolder    伤害类型的持有者
     * @param directEntity  直接造成伤害的实体, 可为 null
     * @param causingEntity 间接造成伤害的实体, 可为 null
     * @param minionEntity  仆从实体, 可为 null
     * @param isRetaliatory 是否为反击伤害
     */
    private WizardryDamageSource(
            Holder<DamageType> typeHolder,
            @Nullable Entity directEntity,
            @Nullable Entity causingEntity,
            @Nullable Entity minionEntity,
            boolean isRetaliatory
    ) {
        super(typeHolder, directEntity, causingEntity);
        this.isRetaliatory = isRetaliatory;
        this.minionEntity = minionEntity;
    }

    public static @NonNull DamageSource causeDirectMagicDamage(Holder<DamageType> type, Entity directEntity, boolean isRetaliatory) {
        return new WizardryDamageSource(type, directEntity, directEntity, null, isRetaliatory);
    }

    public static @NonNull DamageSource causeIndirectMagicDamage(Holder<DamageType> type, @NonNull Entity projectile, Entity projectileOwner, boolean isRetaliatory) {
        return new WizardryDamageSource(type, projectile, projectileOwner, null, isRetaliatory);
    }

    public static @NonNull DamageSource causeDirectMinionDamage(Holder<DamageType> type, Entity minion, Entity minionOwner, boolean isRetaliatory) {
        return new WizardryDamageSource(type, minion, minionOwner, minion, isRetaliatory);
    }

    public static @NonNull DamageSource causeIndirectMinionDamage(Holder<DamageType> type, Entity projectile, Entity minion, Entity minionOwner, boolean isRetaliatory) {
        return new WizardryDamageSource(type, projectile, minionOwner, minion, isRetaliatory);
    }

    public boolean isRetaliatory() {
        return this.isRetaliatory;
    }

    @Nullable
    public Entity getMinionEntity() {
        return this.minionEntity;
    }

    @Override
    public @NonNull Component getLocalizedDeathMessage(@NonNull LivingEntity victim) {
        if (this.minionEntity != null) {
            String key = "death.attack." + this.getMsgId();
            return Component.translatable(key, victim.getDisplayName(), this.minionEntity.getDisplayName());
        }
        return super.getLocalizedDeathMessage(victim);
    }
}
