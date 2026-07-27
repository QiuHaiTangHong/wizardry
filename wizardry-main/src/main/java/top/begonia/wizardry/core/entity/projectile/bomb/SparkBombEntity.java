package top.begonia.wizardry.core.entity.projectile.bomb;

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

public class SparkBombEntity extends BombEntity {

    public SparkBombEntity(EntityType<? extends BombEntity> type, Level level) {
        super(type, level);
    }

    public SparkBombEntity(EntityType<? extends BombEntity> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
    }

    public SparkBombEntity(EntityType<? extends BombEntity> type, double x, double y, double z, Level level, ItemStack itemStack) {
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
        ParticleBuilder.spawnShockParticles(level, hitPos.x(), hitPos.y() + this.getBbHeight() / 2, hitPos.z());
    }

    @Override
    protected @NonNull Item getDefaultItem() {
        return WizardryItems.SPARK_BOMB.get();
    }

}
