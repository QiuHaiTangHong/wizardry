package top.begonia.wizardry.api.entity.atom;

public interface ILifeTicksEntity {
    int getLifetime();
    void setLifetime(int ticks);
    void updateDelegate();
}
