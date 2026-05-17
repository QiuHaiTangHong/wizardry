package top.begonia.wizardry.client.data.definition.model;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3fc;
import top.begonia.wizardry.client.data.definition.IData;

import java.util.List;

public record OnlyModelQuads(
        List<QuadGeometry> quads
) implements IData {

    @Override
    public Class<? extends IData> getDataClass() {
        return OnlyModelQuads.class;
    }

    public record QuadGeometry(
            Vector3fc position0, Vector3fc position1, Vector3fc position2, Vector3fc position3,
            long packedUV0, long packedUV1, long packedUV2, long packedUV3,
            Direction direction, BakedNormals bakedNormals
    ) {
    }
}
