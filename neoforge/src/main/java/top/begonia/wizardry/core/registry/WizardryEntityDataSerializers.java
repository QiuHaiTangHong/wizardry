package top.begonia.wizardry.core.registry;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.core.constants.VariableEntitySize;

import java.util.Optional;
import java.util.UUID;

public final class WizardryEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Wizardry.MODID);
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Optional<UUID>>> CASTER_UUID = DATA_SERIALIZERS.register("caster_uuid", () -> EntityDataSerializer.forValueType(ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC)));
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<VariableEntitySize>> VARIABLE_ENTITY_SIZE = DATA_SERIALIZERS.register("variable_entity_size", () -> EntityDataSerializer.forValueType(VariableEntitySize.STREAM_CODEC));

    public static void register(IEventBus modBus) {
        DATA_SERIALIZERS.register(modBus);
    }
}
