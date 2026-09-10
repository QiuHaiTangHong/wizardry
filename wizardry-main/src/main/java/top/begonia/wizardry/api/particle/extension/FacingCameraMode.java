package top.begonia.wizardry.api.particle.extension;

import net.minecraft.client.Camera;
import org.joml.Quaternionf;

public interface FacingCameraMode {
    FacingCameraMode LOOK_AT_XYZ = (target, camera, _) -> target.set(camera.rotation());
    FacingCameraMode LOOK_AT_Y = (target, camera, _) -> target.set(0.0F, camera.rotation().y, 0.0F, camera.rotation().w);

    void setRotation(Quaternionf rotate, Camera camera, float partialTickTime);
}
