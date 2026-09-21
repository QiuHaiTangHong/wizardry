package top.begonia.wizardry.api.particle.extension.extract;

public interface IUvFlowOperation extends IBaseFlowOperation {
    default IUvFlowOperation setUv(float u0, float u1, float v0, float v1){
        return this.setU0(u0).setU1(u1).setV0(v0).setV1(v1);
    }

    float getU0();

    IUvFlowOperation setU0(float u0);

    float getU1();

    IUvFlowOperation setU1(float u1);

    float getV0();

    IUvFlowOperation setV0(float v0);

    float getV1();

    IUvFlowOperation setV1(float v1);
}
