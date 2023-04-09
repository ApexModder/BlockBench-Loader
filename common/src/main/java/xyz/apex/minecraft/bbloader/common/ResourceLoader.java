package xyz.apex.minecraft.bbloader.common;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import xyz.apex.minecraft.bbloader.common.model.BlockBenchModel;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public final class ResourceLoader
{
    public static final ResourceLoader INSTANCE = new ResourceLoader();

    private final FileToIdConverter converter = new FileToIdConverter("models/bbmodel", ".bbmodel");

    private final Map<ResourceLocation, Optional<BlockBenchModel>> models = Maps.newHashMap();

    private ResourceLoader() {}

    public Optional<BlockBenchModel> getModel(ResourceLocation modelPath)
    {
        return models.computeIfAbsent(modelPath, this::loadModel);
    }

    private Optional<BlockBenchModel> loadModel(ResourceLocation modelName)
    {
        var modelPath = converter.idToFile(modelName);

        BBLoader.LOGGER.debug("Loading BBModel '{}' ({})...", modelPath, modelName);

        try(var reader = Minecraft.getInstance().getResourceManager().openAsReader(modelPath))
        {
            var model = BlockBenchModel.fromReader(reader);
            BBLoader.LOGGER.debug("Loaded BBModel '{}' ({}) successfully!", modelPath, modelName);
            return Optional.of(model);
        }
        catch(JsonParseException | IOException e)
        {
            BBLoader.LOGGER.error("Failed to load BBModel '{}' ({})!", modelPath, modelName, e);
            return Optional.empty();
        }
    }

    public void invalidate()
    {
        models.clear();
    }
}
