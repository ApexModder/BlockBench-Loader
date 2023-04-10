package xyz.apex.minecraft.bbloader.common;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.server.packs.resources.ResourceManager;

final class BBSpriteSource implements SpriteSource
{
    public static final Codec<BBSpriteSource> CODEC = Codec.unit(BBSpriteSource::new);
    public static final SpriteSourceType SPRITE_SOURCE_TYPE = SpriteSources.register("%s:sprites".formatted(BBLoader.ID), CODEC);

    private BBSpriteSource() {}

    @Override
    public void run(ResourceManager resourceManager, Output output)
    {
        BBLoader.LOGGER.info("Loading BBModel Sprites!");
        BBLoader.INSTANCE.converter.listMatchingResources(resourceManager).forEach((modelPath, resource) -> {
            try
            {
                var modelName = BBLoader.INSTANCE.converter.fileToId(modelPath);
                var bbModel = BBLoader.getModel(modelName);
                bbModel.textures().forEach(bbTexture -> {
                    var source = bbTexture.source();
                    if(source == null) return;

                    var textureKey = bbTexture.textureKey(true);
                    BBLoader.LOGGER.debug("Registering BBTexture SpriteSource: '{}#{}'", modelName, textureKey);

                    output.add(textureKey, () -> new SpriteContents(
                            textureKey,
                            new FrameSize(
                                    bbModel.resolution().textureWidth(),
                                    bbModel.resolution().textureHeight()
                            ),
                            source,
                            AnimationMetadataSection.EMPTY
                    ));
                });
            }
            catch(Throwable t)
            {
                BBLoader.LOGGER.catching(t);
            }
        });
    }

    @Override
    public SpriteSourceType type()
    {
        return SPRITE_SOURCE_TYPE;
    }

    static void bootstrap() {}
}
