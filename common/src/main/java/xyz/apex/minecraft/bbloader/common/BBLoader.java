package xyz.apex.minecraft.bbloader.common;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.apex.minecraft.bbloader.common.model.BlockBenchModel;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public final class BBLoader
{
    public static final String ID = "bbloader";
    public static final Logger LOGGER = LogManager.getLogger();
    @ApiStatus.Internal public static final BBLoader INSTANCE = new BBLoader();

    final FileToIdConverter converter = new FileToIdConverter("models/bbmodel", ".bbmodel");
    private final Map<ResourceLocation, BlockBenchModel> models = Maps.newConcurrentMap();
    @Nullable private ModLoader modLoader = null;

    private BBLoader() {}

    private BlockBenchModel loadModel(ResourceLocation modelName)
    {
        var modelPath = converter.idToFile(modelName);

        BBLoader.LOGGER.debug("Loading BBModel '{}' ({})...", modelPath, modelName);

        try(var reader = Minecraft.getInstance().getResourceManager().openAsReader(modelPath))
        {
            var model = BlockBenchModel.fromReader(reader);
            BBLoader.LOGGER.debug("Loaded BBModel '{}' ({}) successfully!", modelPath, modelName);
            return model;
        }
        catch(JsonParseException | IOException e)
        {
            BBLoader.LOGGER.error("Failed to load BBModel '{}' ({})!", modelPath, modelName, e);
            throw new RuntimeException(e);
        }
    }

    public void invalidate()
    {
        models.clear();
    }

    public void setModLoader(ModLoader modLoader)
    {
        Validate.isTrue(this.modLoader == null);
        this.modLoader = modLoader;
        BBSpriteSource.bootstrap();
    }

    public static BlockBenchModel getModel(ResourceLocation modelPath)
    {
        return INSTANCE.models.computeIfAbsent(modelPath, INSTANCE::loadModel);
    }

    @Nullable
    public static BlockBenchModel getModel(JsonObject root, boolean optional) throws JsonParseException
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

    public static ModLoader modLoader()
    {
        return Objects.requireNonNull(INSTANCE.modLoader);
    }
}
