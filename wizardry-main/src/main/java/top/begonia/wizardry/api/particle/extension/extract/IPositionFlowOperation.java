package top.begonia.wizardry.api.particle.extension.extract;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;

public interface IPositionFlowOperation extends IBaseFlowOperation{
    IPositionFlowOperation setX(double x);

    IPositionFlowOperation setY(double y);

    IPositionFlowOperation setZ(double z);

    IPositionFlowOperation setPosition(double x, double y, double z);

    default IPositionFlowOperation setPosition(@NonNull Vector3d position){
        return this.setPosition(position.x, position.y, position.z);
    }

    double getX();

    double getY();

    double getZ();

    Vector3d getOldPos();

    Vector3d getPosition();

    Vec3 getVec3Position();
}
