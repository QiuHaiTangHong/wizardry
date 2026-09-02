package top.begonia.wizardry.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.model.DelegateUnbakedModel;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.event.data.RegisterDelegateUnbakedModelEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WizardryModelLoader implements UnbakedModelLoader<DelegateUnbakedModel> {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "wizardry_model_loader");
    private final Map<Identifier, Function<UnbakedModel, DelegateUnbakedModel>> UNBAKED_MODELS = new HashMap<>();

    public WizardryModelLoader() {
        ModLoader.postEvent(new RegisterDelegateUnbakedModelEvent(UNBAKED_MODELS));
    }

    @Override
    public @NonNull DelegateUnbakedModel read(@NonNull JsonObject jsonObject, @NonNull JsonDeserializationContext context) throws JsonParseException {
        jsonObject.remove("loader");
        Function<UnbakedModel, DelegateUnbakedModel> factory = UNBAKED_MODELS.get(Identifier.tryParse(jsonObject.get("type").getAsString()));
        jsonObject.remove("type");
        UnbakedModel vanillaModel = context.deserialize(jsonObject, UnbakedModel.class);
        return factory.apply(vanillaModel);
    }
}
