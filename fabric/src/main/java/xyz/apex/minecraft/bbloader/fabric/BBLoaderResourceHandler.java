package xyz.apex.minecraft.bbloader.fabric;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import xyz.apex.minecraft.bbloader.common.VanillaHelper;
import xyz.apex.minecraft.bbloader.common.api.BBLoader;

import java.io.IOException;

final class BBLoaderResourceHandler implements ModelResourceProvider
{
    private final ResourceManager manager;

    BBLoaderResourceHandler(ResourceManager manager)
    {
        this.manager = manager;
    }

    @Nullable
    @Override
    public UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) throws ModelProviderException
    {
        var json = readJson(resourceId);
        if(json == null) return null; // builtins fail to be read cause they are builtin and have no json
        var bbModel = BBLoader.INSTANCE.getModel(json, true);
        if(bbModel == null) return null;
        return VanillaHelper.toVanilla(bbModel);
    }

    @Nullable
    private JsonObject readJson(ResourceLocation resourceId) throws ModelProviderException
    {
        try(var reader = manager.openAsReader(resourceId.withPrefix("models/").withSuffix(".json")))
        {
            return GsonHelper.parse(reader);
        }
        catch(JsonParseException | IOException e)
        {
            return null;
            // throw new ModelProviderException("Failed to read resourceId: %s".formatted(resourceId), e);
        }
    }
}
