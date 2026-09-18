package top.begonia.wizardry.api.particle.extension.extract;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;

public interface IPositionFlowOperation extends IBaseFlowOperation {
    default IPositionFlowOperation setPosition(double x, double y, double z) {
        return this.setX(x).setY(y).setZ(z);
    }

    default IPositionFlowOperation setOldPosition(double x, double y, double z) {
        return this.setOldX(x).setOldY(y).setOldZ(z);
    }

    double getX();

    IPositionFlowOperation setX(double x);

    double getY();

    IPositionFlowOperation setY(double y);

    double getZ();

    IPositionFlowOperation setZ(double z);

    double getOldX();

    IPositionFlowOperation setOldX(double x);

    double getOldY();

    IPositionFlowOperation setOldY(double y);

    double getOldZ();

    IPositionFlowOperation setOldZ(double z);

    default Vector3d getOldPos() {
        return new Vector3d(this.getOldX(), this.getOldY(), this.getOldZ());
    }

    default Vec3 getVec3OldPos() {
        return new Vec3(this.getOldX(), this.getOldY(), this.getOldZ());
    }

    default IPositionFlowOperation setOldPosition(@NonNull Vector3d position) {
        return this.setOldPosition(position.x, position.y, position.z);
    }

    default IPositionFlowOperation setOldPosition(@NonNull Vec3 position) {
        return this.setOldPosition(position.x, position.y, position.z);
    }

    default Vector3d getPosition() {
        return new Vector3d(this.getX(), this.getY(), this.getZ());
    }

    default IPositionFlowOperation setPosition(@NonNull Vector3d position) {
        return this.setPosition(position.x, position.y, position.z);
    }

    default IPositionFlowOperation setPosition(@NonNull Vec3 position) {
        return this.setPosition(position.x, position.y, position.z);
    }

    default Vec3 getVec3Position() {
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }
}
