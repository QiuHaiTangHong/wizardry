package top.begonia.wizardry.core.entity.construct.scaled.impl;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import top.begonia.wizardry.core.entity.construct.scaled.ScaledConstructEntity;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.spell.AbstractSpell;

public class HailstormEntity extends ScaledConstructEntity {
    public HailstormEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.setSizeMultiplier(WizardrySpells.HAILSTORM.get().getBaseProperty(AbstractSpell.EFFECT_RADIUS));
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    @Override
    public void tick() {

        super.tick();

        if (!this.level().isClientSide()) {

            double x = this.getX() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth();
            double y = this.getY() + this.random.nextDouble() * (double) this.getBbHeight();
            double z = this.getZ() + (this.random.nextDouble() - 0.5D) * (double) this.getBbWidth();

//            EntityIceShard iceshard = new EntityIceShard(world);
//            iceshard.setPosition(x, y, z);
//
//            iceshard.motionX = MathHelper.cos((float)Math.toRadians(this.rotationYaw + 90));
//            iceshard.motionY = -0.6;
//            iceshard.motionZ = MathHelper.sin((float)Math.toRadians(this.rotationYaw + 90));
//
//            iceshard.setCaster(this.getCaster());
//            iceshard.damageMultiplier = this.damageMultiplier;
//
//            this.world.spawnEntity(iceshard);
        }
    }
}
