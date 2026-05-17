package top.begonia.wizardry.common.data.spell;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface IWizardryProperty<T> {
    Codec<T> getCodec();

    StreamCodec<? super FriendlyByteBuf, T> getStreamCodec();
}
