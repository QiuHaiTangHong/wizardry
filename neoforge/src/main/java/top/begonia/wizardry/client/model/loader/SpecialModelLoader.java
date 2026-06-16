package top.begonia.wizardry.client.model.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.render.block.unbaked.GlowUnbakedBlockModel;

public class SpecialModelLoader implements UnbakedModelLoader<GlowUnbakedBlockModel> {
    public static final SpecialModelLoader INSTANCE = new SpecialModelLoader();
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Wizardry.MODID, "special_model_loader");

    private SpecialModelLoader() {
    }

    @Override
    public @NonNull GlowUnbakedBlockModel read(@NonNull JsonObject jsonObject, @NonNull JsonDeserializationContext context) throws JsonParseException {
        jsonObject.remove("loader");
        UnbakedModel vanillaModel = context.deserialize(jsonObject, UnbakedModel.class);
        return new GlowUnbakedBlockModel(vanillaModel);
    }
}
