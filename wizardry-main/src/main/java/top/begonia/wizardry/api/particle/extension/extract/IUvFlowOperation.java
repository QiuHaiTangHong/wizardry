package top.begonia.wizardry.api.particle.extension.extract;

public interface IUvFlowOperation extends IBaseFlowOperation {
    IUvFlowOperation setU0(float u0);

    IUvFlowOperation setU1(float u1);

    IUvFlowOperation setV0(float v0);

    IUvFlowOperation setV1(float v1);

    IUvFlowOperation setUv(float u0, float u1, float v0, float v1);

    float getU0();

    float getU1();

    float getV0();

    float getV1();
}
