package top.begonia.wizardry.api.particle.extension.extract;

import net.minecraft.client.Camera;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import top.begonia.wizardry.api.particle.extension.Layer;
import top.begonia.wizardry.api.particle.renderer.CompositeQuadParticleRenderState;

import java.util.HashMap;
import java.util.Map;

public class ExtractFlow implements IRotateFlowOperation,
        IPositionFlowOperation,
        IColorFlowOperation,
        IUvFlowOperation,
        ISizeFlowOperation {
    private CompositeQuadParticleRenderState state;
    private Layer layer;
    private Camera camera;
    private Entity linkEntity;
    private Quaternionf rotate = new Quaternionf();
    private int age = 0, lifetime = 0;
    private float partialTick = 0;
    private final Vector3d pos = new Vector3d();
    private final Vector3d oldPos = new Vector3d();
    private float alpha = 1.0F, red = 1.0F, green = 1.0F, blue = 1.0F;
    private float startRed = 1.0F, startGreen = 1.0F, startBlue = 1.0F;
    private float endRed = 1.0F, endGreen = 1.0F, endBlue = 1.0F;
    private float scale;
    private float u0 = 0.0F;
    private float u1 = 1.0F;
    private float v0 = 0.0F;
    private float v1 = 1.0F;
    /**
     * 光照坐标
     */
    private int lightCoords;
    private final Map<Class<?>, Map<String, Object>> additionalData = new HashMap<>();

    public void beginExtraction(
            CompositeQuadParticleRenderState state,
            Layer layer,
            Camera camera,
            Entity linkEntity,
            double x,
            double y,
            double z,
            double oldX,
            double oldY,
            double oldZ,
            float scale,
            float partialTick,
            int age,
            int lifetime,
            int lightCoords
    ) {
        this.state = state;
        this.layer = layer;
        this.camera = camera;
        this.linkEntity = linkEntity;
        this.oldPos.x = oldX;
        this.oldPos.y = oldY;
        this.oldPos.z = oldZ;
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        this.scale = scale;
        this.age = age;
        this.lifetime = lifetime;
        this.partialTick = partialTick;
        this.lightCoords = lightCoords;
        this.additionalData.clear();
    }

    public ExtractFlow() {
    }

    public void setStartColor(float red, float green, float blue) {
        this.startRed = red;
        this.startGreen = green;
        this.startBlue = blue;
    }

    public void setEndColor(float red, float green, float blue) {
        this.endRed = red;
        this.endGreen = green;
        this.endBlue = blue;
    }

    @Override
    public Layer getLayer() {
        return this.layer;
    }

    @Override
    public Camera getCamera() {
        return this.camera;
    }

    @Override
    public CompositeQuadParticleRenderState getState() {
        return this.state;
    }

    @Override
    public Entity getLinkEntity() {
        return this.linkEntity;
    }

    @Override
    public float getPartialTick() {
        return this.partialTick;
    }

    @Override
    public float getAge() {
        return this.age;
    }

    @Override
    public float getLifetime() {
        return this.lifetime;
    }

    @Override
    public <T> void putAdditionalData(String name, T data, Class<T> type) {
        Map<String, Object> classMap = this.additionalData.computeIfAbsent(type, _ -> new HashMap<>());
        classMap.put(name, data);
    }

    @Override
    public <T> T getAdditionalData(String name, Class<T> type) {
        Map<String, Object> classMap = this.additionalData.get(type);
        if (classMap == null) {
            return null;
        }
        return type.cast(classMap.get(name));
    }

    @Override
    public Quaternionf getRotate() {
        return this.rotate;
    }

    @Override
    public void setRotate(Quaternionf rotate) {
        this.rotate = rotate;
    }

    @Override
    public IUvFlowOperation setU0(float u0) {
        this.u0 = u0;
        return this;
    }

    @Override
    public IUvFlowOperation setU1(float u1) {
        this.u1 = u1;
        return this;
    }

    @Override
    public IUvFlowOperation setV0(float v0) {
        this.v0 = v0;
        return this;
    }

    @Override
    public IUvFlowOperation setV1(float v1) {
        this.v1 = v1;
        return this;
    }

    @Override
    public IUvFlowOperation setUv(float u0, float u1, float v0, float v1) {
        return this.setU0(u0).setU1(u1).setV0(v0).setV1(v1);
    }

    @Override
    public float getU0() {
        return this.u0;
    }

    @Override
    public float getU1() {
        return this.u1;
    }

    @Override
    public float getV0() {
        return this.v0;
    }

    @Override
    public float getV1() {
        return this.v1;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public Vector3f getColor() {
        return new Vector3f(this.red, this.green, this.blue);
    }

    @Override
    public int getHEXColor() {
        return ARGB.colorFromFloat(this.alpha, this.red, this.green, this.blue);
    }

    @Override
    public Vector3f getStartColor() {
        return new Vector3f(this.startRed, this.startGreen, this.startBlue);
    }

    @Override
    public Vector3f getEndColor() {
        return new Vector3f(this.endRed, this.endGreen, this.endBlue);
    }

    @Override
    public float getAlpha() {
        return this.alpha;
    }

    @Override
    public int getLightCoords() {
        return this.lightCoords;
    }

    @Override
    public IColorFlowOperation setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        return this;
    }

    @Override
    public IColorFlowOperation setAlpha(float alpha) {
        this.alpha = alpha;
        return this;
    }

    @Override
    public IColorFlowOperation setLightCoords(int lightCoords) {
        this.lightCoords = lightCoords;
        return this;
    }

    @Override
    public IPositionFlowOperation setX(double x) {
        this.pos.x = x;
        return this;
    }

    @Override
    public IPositionFlowOperation setY(double y) {
        this.pos.y = y;
        return this;
    }

    @Override
    public IPositionFlowOperation setZ(double z) {
        this.pos.z = z;
        return this;
    }

    @Override
    public IPositionFlowOperation setPosition(double x, double y, double z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
        return this;
    }

    @Override
    public double getX() {
        return this.pos.x;
    }

    @Override
    public double getY() {
        return this.pos.y;
    }

    @Override
    public double getZ() {
        return this.pos.z;
    }

    @Override
    public Vector3d getPosition() {
        return new Vector3d(this.pos);
    }

    @Override
    public Vector3d getOldPos() {
        return new Vector3d(this.oldPos);
    }

    @Override
    public Vec3 getVec3Position() {
        return new Vec3(this.pos.x, this.pos.y, this.pos.z);
    }
}
