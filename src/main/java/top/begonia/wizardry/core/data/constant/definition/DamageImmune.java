package top.begonia.wizardry.core.data.constant.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import top.begonia.wizardry.api.data.IResultData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record DamageImmune(
        Map<Identifier, List<Identifier>> entries
) implements IResultData {
    public static final Codec<DamageImmune> CODEC = RecordCodecBuilder.create(
            instance -> {
                return instance.group(
                        Codec.unboundedMap(
                                Identifier.CODEC,
                                Identifier.CODEC.listOf()
                        ).fieldOf("entries").forGetter(DamageImmune::entries)
                ).apply(instance, DamageImmune::new);
            }
    );

    public static final DamageImmune DEFAULT = new DamageImmune(new HashMap<>());

    @Override
    public Class<? extends IResultData> getDataClass() {
        return DamageImmune.class;
    }
}
