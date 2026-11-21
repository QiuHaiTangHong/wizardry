package top.begonia.wizardry.api.core.entity.atom;

public interface ILifeTicksEntity {
    int getLifetime();

    void setLifetime(int ticks);

    void updateDelegate();
}
