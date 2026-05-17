package top.begonia.wizardry.client.data.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.quad.BakedNormals;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.model.BookshelfModel;
import top.begonia.wizardry.client.data.definition.model.OnlyModelQuads;

import java.util.ArrayList;
import java.util.List;

public class BookshelfModelParser implements IDataParser<OnlyModelQuads> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "bookshelf_model_parser");

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public OnlyModelQuads parser(JsonElement json) {
        BookshelfModel rawModel = BookshelfModel.CODEC.parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> Wizardry.LOGGER.error("书架局部 JSON 配置解析失败: {}", error))
                .orElse(null);
        if (rawModel == null || rawModel.quads() == null) {
            return null;
        }
        List<OnlyModelQuads.QuadGeometry> compiledGeometries = new ArrayList<>();
        for (BookshelfModel.Element element : rawModel.quads()) {
            Vector3f vFrom = new Vector3f(element.from().x() / 16.0f, element.from().y() / 16.0f, element.from().z() / 16.0f);
            Vector3f vTo = new Vector3f(element.to().x() / 16.0f, element.to().y() / 16.0f, element.to().z() / 16.0f);
            element.faces().forEach((dir, face) -> {
                List<Integer> uv = face.uv();
                float u0 = 0.0f, v0 = 0.0f, u1 = 1.0f, v1 = 1.0f;
                if (uv != null && uv.size() >= 4) {
                    u0 = uv.get(0) / 16.0f;
                    v0 = uv.get(1) / 16.0f;
                    u1 = uv.get(2) / 16.0f;
                    v1 = uv.get(3) / 16.0f;
                }
                Vector3fc[] pos = new Vector3fc[4];
                switch (dir) {
                    case NORTH -> {
                        pos[0] = new Vector3f(vTo.x(), vTo.y(), vFrom.z());
                        pos[1] = new Vector3f(vTo.x(), vFrom.y(), vFrom.z());
                        pos[2] = new Vector3f(vFrom.x(), vFrom.y(), vFrom.z());
                        pos[3] = new Vector3f(vFrom.x(), vTo.y(), vFrom.z());
                    }
                    case SOUTH -> {
                        pos[0] = new Vector3f(vFrom.x(), vTo.y(), vTo.z());
                        pos[1] = new Vector3f(vFrom.x(), vFrom.y(), vTo.z());
                        pos[2] = new Vector3f(vTo.x(), vFrom.y(), vTo.z());
                        pos[3] = new Vector3f(vTo.x(), vTo.y(), vTo.z());
                    }
                    case WEST -> {
                        pos[0] = new Vector3f(vFrom.x(), vTo.y(), vFrom.z());
                        pos[1] = new Vector3f(vFrom.x(), vFrom.y(), vFrom.z());
                        pos[2] = new Vector3f(vFrom.x(), vFrom.y(), vTo.z());
                        pos[3] = new Vector3f(vFrom.x(), vTo.y(), vTo.z());
                    }
                    case EAST -> {
                        pos[0] = new Vector3f(vTo.x(), vTo.y(), vTo.z());
                        pos[1] = new Vector3f(vTo.x(), vFrom.y(), vTo.z());
                        pos[2] = new Vector3f(vTo.x(), vFrom.y(), vFrom.z());
                        pos[3] = new Vector3f(vTo.x(), vTo.y(), vFrom.z());
                    }
                    case UP -> {
                        pos[0] = new Vector3f(vFrom.x(), vTo.y(), vTo.z());
                        pos[1] = new Vector3f(vTo.x(), vTo.y(), vTo.z());
                        pos[2] = new Vector3f(vTo.x(), vTo.y(), vFrom.z());
                        pos[3] = new Vector3f(vFrom.x(), vTo.y(), vFrom.z());
                    }
                    case DOWN -> {
                        pos[0] = new Vector3f(vFrom.x(), vFrom.y(), vFrom.z());
                        pos[1] = new Vector3f(vTo.x(), vFrom.y(), vFrom.z());
                        pos[2] = new Vector3f(vTo.x(), vFrom.y(), vTo.z());
                        pos[3] = new Vector3f(vFrom.x(), vFrom.y(), vTo.z());
                    }
                }
                float[][] uvs = {{u1, v0}, {u1, v1}, {u0, v1}, {u0, v0}};
                int rotDegree = face.rotation().orElse(0);
                int shift = (rotDegree / 90) % 4;
                if (shift > 0) {
                    float[][] rotatedUvs = new float[4][2];
                    for (int i = 0; i < 4; i++) {
                        rotatedUvs[(i + shift) % 4] = uvs[i];
                    }
                    uvs = rotatedUvs;
                }
                long[] packed = new long[4];
                for (int i = 0; i < 4; i++) {
                    long uBits = Integer.toUnsignedLong(Float.floatToRawIntBits(uvs[i][0]));
                    long vBits = Integer.toUnsignedLong(Float.floatToRawIntBits(uvs[i][1]));
                    packed[i] = (uBits << 32) | (vBits & 0xFFFFFFFFL);
                }
                compiledGeometries.add(new OnlyModelQuads.QuadGeometry(
                        pos[0], pos[1], pos[2], pos[3],
                        packed[0], packed[1], packed[2], packed[3],
                        dir,
                        BakedNormals.UNSPECIFIED
                ));
            });
        }
        return new OnlyModelQuads(compiledGeometries);
    }
}
