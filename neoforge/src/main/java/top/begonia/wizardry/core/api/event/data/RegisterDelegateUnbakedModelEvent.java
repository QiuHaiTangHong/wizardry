package top.begonia.wizardry.core.api.event.data;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.client.model.DelegateUnbakedModel;

import java.util.Map;
import java.util.function.Function;

public class RegisterDelegateUnbakedModelEvent extends Event implements IModBusEvent {
    private final Map<Identifier, Function<UnbakedModel, DelegateUnbakedModel>> unbakedModels;

    public RegisterDelegateUnbakedModelEvent(Map<Identifier, Function<UnbakedModel, DelegateUnbakedModel>> unbakedModels) {
        this.unbakedModels = unbakedModels;
    }

    public void register(Identifier identifier, Function<UnbakedModel, DelegateUnbakedModel> factory) {
        this.unbakedModels.put(identifier, factory);
    }
}
