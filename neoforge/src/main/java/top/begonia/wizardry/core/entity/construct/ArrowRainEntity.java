package top.begonia.wizardry.core.entity.construct;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * ArrowRainEntity 类
 * <p>ArrowRainEntity 用于表示箭雨实体, 该类继承自 MagicConstructEntity 并实现其 tick 方法.ArrowRainEntity 主要处理箭雨的逻辑和行为, 包括创建箭, 设置属性并将其发射到目标位置的功能. 它负责模拟箭雨在世界中的运动方式以及它们如何与游戏环境进行交互.
 *
 * @author 秋海棠红
 * @version 1.0.0
 * @date 2026.07.09
 */
public class ArrowRainEntity extends MagicConstructEntity {
    public ArrowRainEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
//        if (!this.level().isClientSide()) {
//
//            double x = posX + (world.rand.nextDouble() - 0.5D) * (double) width;
//            double y = posY + world.rand.nextDouble() * (double) height;
//            double z = posZ + (world.rand.nextDouble() - 0.5D) * (double) width;
//
//            EntityConjuredArrow arrow = new EntityConjuredArrow(world, x, y, z);
//
//            arrow.motionX = MathHelper.cos((float) Math.toRadians(this.rotationYaw + 90));
//            arrow.motionY = -0.6;
//            arrow.motionZ = MathHelper.sin((float) Math.toRadians(this.rotationYaw + 90));
//
//            arrow.shootingEntity = this.getCaster();
//            arrow.setDamage(7.0d * damageMultiplier);
//
//            this.world.spawnEntity(arrow);
//        }
    }
}
