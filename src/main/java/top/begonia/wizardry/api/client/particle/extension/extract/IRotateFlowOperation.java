package top.begonia.wizardry.api.client.particle.extension.extract;

import org.joml.Quaternionf;

public interface IRotateFlowOperation extends IBaseFlowOperation {
    Quaternionf getRotate();

    void setRotate(Quaternionf rotate);
}
