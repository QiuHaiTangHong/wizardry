package top.begonia.wizardry.client.model;

import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.function.BiFunction;

public record ArmorModelSetExtension<T>(T head, T chest, T legs, T feet) {
    public T get(EquipmentSlot slot) {
        Object result;
        switch (slot) {
            case HEAD -> result = this.head;
            case CHEST -> result = this.chest;
            case LEGS -> result = this.legs;
            case FEET -> result = this.feet;
            default -> throw new IllegalStateException("No model for slot: " + slot);
        }

        return (T) result;
    }

    public <U> ArmorModelSet<U> map(BiFunction<? super T, EquipmentSlot, ? extends U> mapper) {
        return new ArmorModelSet<>(
                mapper.apply(this.head, EquipmentSlot.HEAD),
                mapper.apply(this.chest, EquipmentSlot.CHEST),
                mapper.apply(this.legs, EquipmentSlot.LEGS),
                mapper.apply(this.feet, EquipmentSlot.FEET)
        );
    }
}
