package top.begonia.wizardry.api.entity.atom;

public interface ISummonEntity {
    void onSpawn();
    void onDespawn();
    default boolean hasParticleEffect() {
        return false;
    }
}
