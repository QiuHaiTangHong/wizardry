package top.begonia.wizardry.client.data.parser;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.api.distmarker.Dist;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.data.definition.particle.ParticleCombinedHolder;
import top.begonia.wizardry.client.data.definition.particle.ParticleDescriptionData;
import top.begonia.wizardry.client.data.definition.particle.ParticleParserContextData;
import top.begonia.wizardry.client.data.definition.particle.ParticleResultData;
import top.begonia.wizardry.core.api.data.IDataParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticleParser implements IDataParser<ParticleDescriptionData, ParticleParserContextData, ParticleResultData> {
    public static final Identifier PARSER_NAME = Identifier.fromNamespaceAndPath(Wizardry.MODID, "particle");

    @Override
    public Dist getSupportedDist() {
        return Dist.CLIENT;
    }

    @Override
    public Identifier getIdentifier() {
        return PARSER_NAME;
    }

    @Override
    public ParticleDescriptionData parserItem(JsonElement json) {
        return ParticleDescriptionData.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(error -> new IllegalArgumentException("粒子 JSON 资产格式非法: " + error));
    }

    @Override
    public ParticleResultData transformItemToResult(Identifier id, ParticleDescriptionData data, ParticleParserContextData context, PreparableReloadListener.SharedState currentReload) {
        Optional<ParticleType<?>> type = BuiltInRegistries.PARTICLE_TYPE.getOptional(id);
        if (type.isPresent()) {
            ParticleCombinedHolder particleCombinedHolder = context.getParticleHolder(type.get());
            if (particleCombinedHolder != null) {
                var sprites = currentReload.get(AtlasManager.PENDING_STITCH)
                        .get(AtlasIds.PARTICLES).join();
                var missingSprite = sprites.missing();
                List<List<TextureAtlasSprite>> finishedMatrix = new ArrayList<>();
                for (var row : data.textures()) {
                    List<TextureAtlasSprite> finishedRow = new ArrayList<>();
                    for (Identifier spriteId : row) {
                        var sprite = sprites.getSprite(spriteId);
                        finishedRow.add(sprite == null ? missingSprite : sprite);
                    }
                    finishedMatrix.add(finishedRow);
                }
                particleCombinedHolder.getSpriteSet().rebind(finishedMatrix);
                return new ParticleResultData(particleCombinedHolder);
            }
        }
        return null;
    }
}
