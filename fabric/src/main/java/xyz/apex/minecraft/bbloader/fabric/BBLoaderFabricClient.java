package xyz.apex.minecraft.bbloader.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import xyz.apex.minecraft.bbloader.common.BBLoader;

public final class BBLoaderFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BBLoader.INSTANCE.setModLoader(new FabricModLoader());

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId()
            {
                return new ResourceLocation(BBLoader.ID, "resources");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager)
            {
                BBLoader.INSTANCE.invalidate();
            }
        });

        ModelLoadingRegistry.INSTANCE.registerResourceProvider(BBLoaderResourceHandler::new);

        BuiltInRegistries.BLOCK.getOptional(new ResourceLocation(BBLoader.ID, "test_block")).ifPresent(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout()));
    }
}
