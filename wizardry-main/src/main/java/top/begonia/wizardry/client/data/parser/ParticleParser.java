package top.begonia.wizardry.client.data.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.data.IDataParser;
import top.begonia.wizardry.api.data.IParserContext;
import top.begonia.wizardry.api.particle.extension.MutableDoubleSpriteSet;
import top.begonia.wizardry.client.data.definition.particle.ParticleDescriptionData;

import java.util.ArrayList;
import java.util.List;

public class ParticleParser implements IDataParser<ParticleDescriptionData, IParserContext, MutableDoubleSpriteSet> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "particle");
    private static final Identifier PARTICLE_PATH = Identifier.withDefaultNamespace("particles");

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public ParticleDescriptionData parserItem(JsonElement json) {
        return ParticleDescriptionData.CODEC
                .parse(JsonOps.INSTANCE, json)
                .getOrThrow(error -> new IllegalArgumentException("粒子 JSON 资产格式非法: " + error));
    }

    @Override
    public MutableDoubleSpriteSet transformItemToResult(
            Identifier id,
            @NonNull ParticleDescriptionData data,
            IParserContext context,
            PreparableReloadListener.SharedState currentReload,
            @NonNull ResourceManager resourceManager
    ) {
        AtlasManager atlasManager = Minecraft.getInstance().getAtlasManager();
        TextureAtlas textureAtlas = atlasManager.getAtlasOrThrow(PARTICLE_PATH);
        List<List<TextureAtlasSprite>> result = new ArrayList<>();
        for (List<Identifier> texture : data.textures()) {
            List<TextureAtlasSprite> parts = new ArrayList<>();
            for (Identifier identifier : texture) {
                TextureAtlasSprite atlasSprite = textureAtlas.getSprite(identifier);
                parts.add(atlasSprite);
            }
            result.add(parts);
        }
        return new MutableDoubleSpriteSet(result);
    }
}
