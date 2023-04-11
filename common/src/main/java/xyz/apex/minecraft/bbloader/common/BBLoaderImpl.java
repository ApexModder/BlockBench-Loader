package xyz.apex.minecraft.bbloader.common;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.apex.minecraft.bbloader.common.api.BBLoader;
import xyz.apex.minecraft.bbloader.common.api.model.BBModel;
import xyz.apex.minecraft.bbloader.common.model.BBModelImpl;

import java.io.IOException;
import java.util.Map;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public abstract class BBLoaderImpl implements BBLoader
{
    private final Map<ResourceLocation, BBModel> models = Maps.newConcurrentMap();

    @Override
    public BBModel getModel(ResourceLocation modelPath)
    {
        return models.computeIfAbsent(modelPath, this::loadModel);
    }

    @Nullable
    @Override
    public BBModel getModel(JsonObject root, boolean optional) throws JsonParseException
    {
        // if(!GsonHelper.isStringValue(root, "loader")) return null;
        // var loader = GsonHelper.getAsString(root, "loader");
        // if(!loader.equals("%s:geometry".formatted(BBLoader.ID))) return null;

        if(!GsonHelper.isStringValue(root, "bbmodel"))
        {
            if(!optional) throw new JsonParseException("Missing requires BBModel property!");
            return null;
        }

        var rawBBModelName = GsonHelper.getAsString(root, "bbmodel");
        var bbModelName = ResourceLocation.tryParse(rawBBModelName);
        if(bbModelName == null)
        {
            if(!optional) throw new JsonParseException("Invalid BBModel property! [Invalid resource location] '%s'".formatted(rawBBModelName));
            return null;
        }

        return getModel(bbModelName);
    }

    @Override
    public void bootstrap()
    {
        BBSpriteSource.bootstrap();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager)
    {
        models.clear();
    }

    private BBModel loadModel(ResourceLocation modelName)
    {
        var modelPath = FILE_TO_ID_CONVERTER.idToFile(modelName);

        LOGGER.debug("Loading BBModel '{}' ({})...", modelPath, modelName);

        try(var reader = Minecraft.getInstance().getResourceManager().openAsReader(modelPath))
        {
            var model = BBModelImpl.fromReader(reader);
            LOGGER.debug("Loaded BBModel '{}' ({}) successfully!", modelPath, modelName);
            return model;
        }
        catch(JsonParseException | IOException e)
        {
            LOGGER.error("Failed to load BBModel '{}' ({})!", modelPath, modelName, e);
            throw new RuntimeException(e);
        }
    }
}
