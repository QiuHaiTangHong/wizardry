package top.begonia.wizardry.client.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.NonNull;

public class MovingSoundEntity<T extends Entity> extends AbstractTickableSoundInstance {
    protected final T source;

    public MovingSoundEntity(
            @NonNull T entity,
            SoundEvent sound,
            SoundSource source,
            float volume,
            float pitch,
            boolean repeat) {

        super(sound, source, SoundInstance.createUnseededRandom());

        this.source = entity;
        this.volume = volume;
        this.pitch = pitch;
        this.looping = repeat;
        this.delay = 0;

        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public void tick() {
        if (this.source.isRemoved()) {
            this.stop();
        } else {
            this.x = this.source.getX();
            this.y = this.source.getY();
            this.z = this.source.getZ();
        }
    }
}
