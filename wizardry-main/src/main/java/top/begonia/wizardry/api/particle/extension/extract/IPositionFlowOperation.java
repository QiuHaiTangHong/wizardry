package top.begonia.wizardry.api.particle.extension.extract;

import org.joml.Vector3f;

public interface IPositionFlowOperation extends IBaseFlowOperation{
    IPositionFlowOperation setX(float x);

    IPositionFlowOperation setY(float y);

    IPositionFlowOperation setZ(float z);

    IPositionFlowOperation setPosition(float x, float y, float z);

    float getX();

    float getY();

    float getZ();

    Vector3f getPosition();
}
