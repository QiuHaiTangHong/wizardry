package top.begonia.wizardry.client.model.armour;

import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.function.BiFunction;

public record ArmorModelSetExtension<T>(T head, T chest, T legs, T feet) {
    public T get(@NonNull EquipmentSlot slot) {
        T result;
        switch (slot) {
            case HEAD -> result = this.head;
            case CHEST -> result = this.chest;
            case LEGS -> result = this.legs;
            case FEET -> result = this.feet;
            default -> throw new IllegalStateException("No model for slot: " + slot);
        }

        return result;
    }

    @Contract("_ -> new")
    public <U> @NonNull ArmorModelSet<U> map(@NonNull BiFunction<? super T, EquipmentSlot, ? extends U> mapper) {
        return new ArmorModelSet<>(
                mapper.apply(this.head, EquipmentSlot.HEAD),
                mapper.apply(this.chest, EquipmentSlot.CHEST),
                mapper.apply(this.legs, EquipmentSlot.LEGS),
                mapper.apply(this.feet, EquipmentSlot.FEET)
        );
    }
}
