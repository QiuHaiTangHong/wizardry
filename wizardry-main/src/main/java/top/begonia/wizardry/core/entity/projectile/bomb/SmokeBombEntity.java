package top.begonia.wizardry.core.entity.projectile.bomb;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.client.util.ParticleBuilder;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryParticles;

public class SmokeBombEntity extends BombEntity {

    public SmokeBombEntity(EntityType<? extends BombEntity> type, Level level) {
        super(type, level);
    }

    public SmokeBombEntity(EntityType<? extends BombEntity> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
    }

    public SmokeBombEntity(EntityType<? extends BombEntity> type, double x, double y, double z, Level level, ItemStack itemStack) {
        super(type, x, y, z, level, itemStack);
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    protected void onHitEntity(@NonNull EntityHitResult hitResult) {

    }

    @Override
    protected void onHitBlock(@NonNull BlockHitResult hitResult) {

    }

    @Override
    protected void createParticles(Level level) {
        Vec3 hitPos = this.position();
        ParticleBuilder.create(WizardryParticles.FLASH.get()).pos(hitPos).scale(5 * blastMultiplier).clr(0, 0, 0).spawn(this.level());

        this.level().addParticle(ParticleTypes.EXPLOSION, hitPos.x(), hitPos.y(), hitPos.z(), 0, 0, 0);

        for (int i = 0; i < 60 * blastMultiplier; i++) {

            float brightness = this.random.nextFloat() * 0.1f + 0.1f;
            ParticleBuilder.create(WizardryParticles.CLOUD.get(), this.random, hitPos.x(), hitPos.y(), hitPos.z(), 2 * blastMultiplier, false)
                    .clr(brightness, brightness, brightness).time(80 + this.random.nextInt(12)).shaded(true).spawn(this.level());

            brightness = this.random.nextFloat() * 0.3f;
            ParticleBuilder.create(WizardryParticles.DARK_MAGIC.get(), this.random, hitPos.x(), hitPos.y(), hitPos.z(), 2 * blastMultiplier, false)
                    .clr(brightness, brightness, brightness).spawn(this.level());
        }
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return WizardryItems.SMOKE_BOMB.get();
    }
}
