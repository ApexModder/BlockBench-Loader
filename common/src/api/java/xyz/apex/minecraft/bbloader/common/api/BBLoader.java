package xyz.apex.minecraft.bbloader.common.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.apex.minecraft.bbloader.common.api.model.BBModel;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface BBLoader extends ResourceManagerReloadListener
{
    String ID = "bbloader";
    Logger LOGGER = LogManager.getLogger();
    FileToIdConverter FILE_TO_ID_CONVERTER = new FileToIdConverter("models/bbmodel", ".bbmodel");
    BBLoader INSTANCE = load();

    BBModel getModel(ResourceLocation modelPath);

    @Nullable BBModel getModel(JsonObject root, boolean optional) throws JsonParseException;

    // used to build convert into vanillas item transforms
    // this is done slightingly differently depending on platform
    // which is why its part of the BBLoader interface, to be implemented per platform
    @ApiStatus.Internal ItemTransforms itemTransforms(BBModel bbModel);

    // Used initialize various things when BBLoader loads
    // should not be called manually
    @ApiStatus.Internal void bootstrap();

    private static BBLoader load()
    {
        var providers = ServiceLoader.load(BBLoader.class).stream().toList();
        if(providers.isEmpty()) throw new IllegalStateException("Missing BBLoader ServiceProvider implementation!");
        else if(providers.size() > 1)
        {
            var names = providers.stream().map(ServiceLoader.Provider::type).map(Class::getName).collect(Collectors.joining(",", "[", "]"));
            throw new IllegalStateException("Expected exactly 1 BBLoader implementation, found: %s".formatted(names));
        }
        else return providers.get(0).get();
    }
}
