package top.begonia.wizardry.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.model.unbaked.block.EmissionUnbakedBlockModel;

public class EmissionModelLoader implements UnbakedModelLoader<EmissionUnbakedBlockModel> {
    public static final EmissionModelLoader INSTANCE = new EmissionModelLoader();
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "special_model_loader");

    private EmissionModelLoader() {
    }

    @Override
    public @NonNull EmissionUnbakedBlockModel read(@NonNull JsonObject jsonObject, @NonNull JsonDeserializationContext context) throws JsonParseException {
        jsonObject.remove("loader");
        UnbakedModel vanillaModel = context.deserialize(jsonObject, UnbakedModel.class);
        return new EmissionUnbakedBlockModel(vanillaModel);
    }
}
