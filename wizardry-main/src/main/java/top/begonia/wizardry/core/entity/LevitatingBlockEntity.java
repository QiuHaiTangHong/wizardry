package top.begonia.wizardry.core.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class LevitatingBlockEntity extends FallingBlockEntity {
    @Nullable
    private WeakReference<LivingEntity> caster;
    @Nullable
    private UUID casterUUID;

    public float damageMultiplier = 1.0f;
    private int suspendTimer = 5;

    public LevitatingBlockEntity(EntityType<? extends LevitatingBlockEntity> type, Level level) {
        super(type, level);
    }

    public void suspend() {
        this.suspendTimer = 5;
    }

    @Nullable
    public LivingEntity getCaster() {
        if (this.caster != null) {
            return this.caster.get();
        }
        if (this.casterUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.casterUUID);
            if (entity instanceof LivingEntity living) {
                this.caster = new WeakReference<>(living);
                return living;
            }
        }
        return null;
    }

    public void setCaster(@Nullable LivingEntity caster) {
        this.caster = caster != null ? new WeakReference<>(caster) : null;
        if (caster != null) {
            this.casterUUID = caster.getUUID();
        }
    }

    @Override
    public void tick() {

    }

}
