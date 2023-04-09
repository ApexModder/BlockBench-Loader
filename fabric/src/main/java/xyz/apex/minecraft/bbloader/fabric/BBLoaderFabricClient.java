package xyz.apex.minecraft.bbloader.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import xyz.apex.minecraft.bbloader.common.BBLoader;
import xyz.apex.minecraft.bbloader.common.ResourceLoader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class BBLoaderFabricClient implements BBLoader, ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId()
            {
                return new ResourceLocation(ID, "resources");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
            {
                return ResourceLoader.INSTANCE.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }
        });

        BuiltInRegistries.BLOCK.getOptional(new ResourceLocation(ID, "test_block")).ifPresent(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout()));
    }
}
