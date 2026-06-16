package top.begonia.wizardry.core.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class WizardryDamageSource extends DamageSource {
    private final boolean isRetaliatory;
    private final Entity minionEntity;

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

    public boolean isRetaliatory() {
        return this.isRetaliatory;
    }

    private static Holder<DamageType> getHolderOrThrow(Level level, ResourceKey<DamageType> key) {
        return level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
    }

    public static DamageSource causeDirectMagicDamage(Entity caster, ResourceKey<DamageType> resourceKey, boolean isRetaliatory) {
        return causeIndirectMagicDamage(caster, caster, resourceKey, isRetaliatory);
    }

    public static DamageSource causeIndirectMagicDamage(Entity magicProjectile, Entity caster, ResourceKey<DamageType> resourceKey, boolean isRetaliatory) {
        Holder<DamageType> holder = getHolderOrThrow(magicProjectile.level(), resourceKey);
        return new WizardryDamageSource(holder, magicProjectile, caster, null, isRetaliatory);
    }

    public static DamageSource causeMinionDamage(Entity minion, Entity caster, ResourceKey<DamageType> resourceKey, boolean isRetaliatory) {
        return causeIndirectMinionDamage(minion, minion, caster, resourceKey, isRetaliatory);
    }

    public static DamageSource causeIndirectMinionDamage(Entity projectile, Entity minion, Entity caster, ResourceKey<DamageType> resourceKey, boolean isRetaliatory) {
        Holder<DamageType> holder = getHolderOrThrow(caster.level(), resourceKey);
        return new WizardryDamageSource(holder, projectile, caster, minion, isRetaliatory);
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
