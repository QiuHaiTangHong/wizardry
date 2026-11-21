package top.begonia.wizardry.api.core.entity.atom;

public interface ISummonEntity {
    void onSpawn();

    void onDespawn();

    default boolean hasParticleEffect() {
        return false;
    }
}
