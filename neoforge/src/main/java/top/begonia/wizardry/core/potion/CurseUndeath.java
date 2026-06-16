package top.begonia.wizardry.core.potion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class CurseUndeath extends Curse {
    public CurseUndeath(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, @NonNull LivingEntity entity, int amplification) {
        float timeRatio = serverLevel.environmentAttributes().getValue(
                EnvironmentAttributes.SUN_ANGLE,
                entity.position()
        );
        boolean isDaytime = (timeRatio < 0.25f || timeRatio > 0.75f);
        boolean hasSkyLight = serverLevel.dimensionType().hasSkyLight();
        if (hasSkyLight && isDaytime) {
            @SuppressWarnings("deprecation")
            float lightLevel = entity.getLightLevelDependentMagicValue();
            if (lightLevel > 0.5F
                    && entity.getRandom().nextFloat() * 30.0F < (lightLevel - 0.4F) * 2.0F
                    && serverLevel.canSeeSky(entity.blockPosition().above((int) entity.getEyeHeight()))) {
                boolean shouldSetFire = true;
                ItemStack headStack = entity.getItemBySlot(EquipmentSlot.HEAD);
                if (!headStack.isEmpty()) {
                    if (headStack.isDamageableItem()) {
                        headStack.hurtAndBreak(entity.getRandom().nextInt(2), serverLevel, null, (item) -> entity.onEquippedItemBroken(item, EquipmentSlot.HEAD));
                    }
                    shouldSetFire = false;
                }
                if (shouldSetFire) {
                    entity.igniteForSeconds(8);
                }
            }
        }
        return true;
    }
}
