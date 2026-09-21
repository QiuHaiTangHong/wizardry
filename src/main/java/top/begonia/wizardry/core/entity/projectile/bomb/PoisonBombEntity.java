package top.begonia.wizardry.core.entity.projectile.bomb;

import net.minecraft.client.multiplayer.ClientLevel;
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
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryParticles;

public class PoisonBombEntity extends BombEntity {

    public PoisonBombEntity(EntityType<? extends BombEntity> type, Level level) {
        super(type, level);
    }

    public PoisonBombEntity(EntityType<? extends BombEntity> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
    }

    public PoisonBombEntity(EntityType<? extends BombEntity> type, double x, double y, double z, Level level, ItemStack itemStack) {
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
    protected void createParticles(ClientLevel level) {
        Vec3 hitPos = this.position();
        WizardryClient.particleManager.getParticle(
                level,
                WizardryParticles.FLASH.get(),
                hitPos.x, hitPos.y, hitPos.z
        ).ifPresent(p -> p.scaleValue(5 * blastMultiplier)
                .color(0.2f + this.random.nextFloat() * 0.3f, 0.6f, 0.0f)
                .spawn()
        );

        for (int i = 0; i < 60 * blastMultiplier; i++) {
            WizardryClient.particleManager.getParticle(
                    level,
                    this.random,
                    WizardryParticles.SPARKLE.get(),
                    hitPos.x(), hitPos.y(), hitPos.z(),
                    2 * blastMultiplier,
                    false
            ).ifPresent(p -> p.time(35)
                    .scaleValue(2.0f)
                    .color(0.2f + this.random.nextFloat() * 0.3f, 0.6f, 0.0f)
                    .spawn()
            );

            WizardryClient.particleManager.getParticle(
                    level,
                    this.random,
                    WizardryParticles.DARK_MAGIC.get(),
                    hitPos.x(), hitPos.y(), hitPos.z(),
                    2 * blastMultiplier,
                    false
            ).ifPresent(p -> p.color(0.2f + this.random.nextFloat() * 0.2f, 0.8f, 0.0f)
                    .spawn()
            );
        }
        this.level().addParticle(ParticleTypes.EXPLOSION, hitPos.x(), hitPos.y(), hitPos.z(), 0, 0, 0);
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return WizardryItems.POISON_BOMB.get();
    }
}
