package top.begonia.wizardry.core.entity.construct;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

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
