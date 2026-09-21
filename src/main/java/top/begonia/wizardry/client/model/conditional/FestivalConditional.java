package top.begonia.wizardry.client.model.conditional;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.Month;

public class FestivalConditional implements ConditionalItemModelProperty {
    public static final MapCodec<FestivalConditional> CODEC = MapCodec.unit(new FestivalConditional());

    @Override
    public @NonNull MapCodec<? extends ConditionalItemModelProperty> type() {
        return CODEC;
    }

    @Override
    public boolean get(
            @NonNull ItemStack itemStack,
            @Nullable ClientLevel clientLevel,
            @Nullable LivingEntity livingEntity,
            int i,
            @NonNull ItemDisplayContext itemDisplayContext
    ) {
        return LocalDate.now().getMonth() == Month.DECEMBER && LocalDate.now().getDayOfMonth() >= 24 && LocalDate.now().getDayOfMonth() <= 26;
    }
}
