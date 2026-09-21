package top.begonia.wizardry.client.data.definition.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record BookshelfModel(
        List<Element> quads
) {
    public static final Codec<BookshelfModel> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Element.CODEC.listOf().fieldOf("elements").forGetter(BookshelfModel::quads)
            ).apply(instance, BookshelfModel::new)
    );

    public record Element(
            Vector3fc from,
            Vector3fc to,
            Map<Direction, Face> faces
    ) {
        public static final Codec<Element> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ExtraCodecs.VECTOR3F.fieldOf("from").forGetter(Element::from),
                        ExtraCodecs.VECTOR3F.fieldOf("to").forGetter(Element::to),
                        Codec.unboundedMap(Direction.CODEC, Face.CODEC).fieldOf("faces").forGetter(Element::faces)
                ).apply(instance, Element::new)
        );
    }

    public record Face(
            List<Integer> uv,
            Optional<Integer> rotation
    ) {
        public static final Codec<Face> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.listOf().fieldOf("uv").forGetter(Face::uv),
                        Codec.INT.optionalFieldOf("rotation").forGetter(Face::rotation)
                ).apply(instance, Face::new)
        );
    }
}
