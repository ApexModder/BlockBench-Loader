package xyz.apex.minecraft.bbloader.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import xyz.apex.minecraft.bbloader.common.api.BBLoader;

public final class BBLoaderFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BBLoader.INSTANCE.bootstrap();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId()
            {
                return new ResourceLocation(BBLoader.ID, "resources");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager)
            {
                BBLoader.INSTANCE.onResourceManagerReload(resourceManager);
            }
        });

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(BBLoaderResourceHandler::new);
    }
}
