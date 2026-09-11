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
import top.begonia.wizardry.api.particle.options.QuadParticleOptions;
import top.begonia.wizardry.client.WizardryClient;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardryParticles;

public class SmokeBombEntity extends BombEntity {

    public SmokeBombEntity(EntityType<? extends BombEntity> type, Level level) {
        super(type, level);
    }

    public SmokeBombEntity(EntityType<? extends BombEntity> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
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
        WizardryClient.particleManager.createParticleOpt(
                level,
                new QuadParticleOptions(WizardryParticles.FLASH.get()),
                hitPos.x, hitPos.y, hitPos.z
        ).ifPresent(p -> p.scaleValue(5 * blastMultiplier)
                .color(0.0f, 0.0f, 0.0f)
                .spawn()
        );

        this.level().addParticle(ParticleTypes.EXPLOSION, hitPos.x(), hitPos.y(), hitPos.z(), 0, 0, 0);

        for (int i = 0; i < 60 * blastMultiplier; i++) {
            WizardryClient.particleManager.createParticleOpt(
                    level,
                    this.random,
                    new QuadParticleOptions(WizardryParticles.FLASH.get()),
                    hitPos.x(), hitPos.y(), hitPos.z(),
                    2 * blastMultiplier,
                    false
            ).ifPresent(p -> {
                        float brightness = this.random.nextFloat() * 0.1f + 0.1f;
                        p.scaleValue(5 * blastMultiplier)
                                .color(brightness, brightness, brightness)
                                .time(80 + this.random.nextInt(12))
                                .shaded(true)
                                .spawn();
                    }
            );

            WizardryClient.particleManager.createParticleOpt(
                    level,
                    this.random,
                    new QuadParticleOptions(WizardryParticles.DARK_MAGIC.get()),
                    hitPos.x(), hitPos.y(), hitPos.z(),
                    2 * blastMultiplier,
                    false
            ).ifPresent(p -> {
                        float brightness = this.random.nextFloat() * 0.3f;
                        p.color(brightness, brightness, brightness)
                                .spawn();
                    }
            );
        }
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return WizardryItems.SMOKE_BOMB.get();
    }
}
