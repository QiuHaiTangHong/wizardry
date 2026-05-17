package top.begonia.wizardry.common.data.spell.part;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import top.begonia.wizardry.common.data.spell.IWizardryProperty;

import java.util.Map;
import java.util.NoSuchElementException;

public record BasePropertiesData(
        Map<String, Float> baseProperties
) implements IWizardryProperty<BasePropertiesData> {
    public static final Codec<BasePropertiesData> CODEC = Codec.unboundedMap(Codec.STRING, Codec.FLOAT)
            .xmap(BasePropertiesData::new, BasePropertiesData::baseProperties);
    public static final StreamCodec<FriendlyByteBuf, BasePropertiesData> STREAM_CODEC =
            ByteBufCodecs.fromCodec(CODEC).cast();

    public static BasePropertiesData getDefault() {
        return new BasePropertiesData(Map.of());
    }

    private float get(String basePropertiesName) {
        if (!baseProperties.containsKey(basePropertiesName)) {
            throw new NoSuchElementException("法术属性缺失: 必须在 JSON 中定义 '" + basePropertiesName + "' 才能正常运行。");
        }
        return baseProperties.get(basePropertiesName);
    }

    public boolean has(String basePropertiesName) {
        return baseProperties.containsKey(basePropertiesName);
    }

    @Override
    public Codec<BasePropertiesData> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super FriendlyByteBuf, BasePropertiesData> getStreamCodec() {
        return STREAM_CODEC;
    }
}