package xyz.apex.minecraft.bbloader.common;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.bbloader.common.model.BlockBenchModel;

import java.io.IOException;
import java.util.Map;

@ApiStatus.Internal
public final class ResourceLoader implements ResourceManagerReloadListener
{
    public static final ResourceLoader INSTANCE = new ResourceLoader();

    private final FileToIdConverter converter = new FileToIdConverter("models/bbmodel", ".bbmodel");

    private Map<ResourceLocation, BlockBenchModel> models = ImmutableMap.of();

    private ResourceLoader() {}

    public BlockBenchModel getModel(ResourceLocation modelPath)
    {
        return models.get(modelPath);
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager)
    {
        BBLoader.LOGGER.info("Loading BlockBench models...");
        var models = ImmutableMap.<ResourceLocation, BlockBenchModel>builder();
        var resources = converter.listMatchingResources(resourceManager);

        for(var entry : resources.entrySet())
        {
            var resourcePath = entry.getKey();
            BBLoader.LOGGER.debug("Loading BBModel '{}'...", resourcePath);

            try(var reader = resourceManager.openAsReader(resourcePath))
            {
                var model = BlockBenchModel.fromReader(reader);
                var resourceId = converter.fileToId(resourcePath);
                models.put(resourceId, model);
                BBLoader.LOGGER.debug("Loaded BBModel '{}' ({}) successfully!", resourcePath, resourceId);
            }
            catch(JsonParseException | IOException e)
            {
                BBLoader.LOGGER.error("Failed to load BBModel '{}'!", resourcePath, e);
            }
        }

        this.models = models.build();
        BBLoader.LOGGER.info("Finished Loading BBModels, Loaded {} BBModel(s)!", this.models.size());
    }
}
