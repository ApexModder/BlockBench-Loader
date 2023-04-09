package xyz.apex.minecraft.bbloader.forge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.apex.minecraft.bbloader.common.BBLoader;

final class BBLoaderForgeClient
{
    BBLoaderForgeClient()
    {
        BBLoader.INSTANCE.setModLoader(new ForgeModLoader());
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegisterClientResourceReload);
        bus.addListener(this::onRegisterGeometryLoader);
        bus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        BBLoaderForge.TEST_BLOCK.ifPresent(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout()));
    }

    private void onRegisterGeometryLoader(ModelEvent.RegisterGeometryLoaders event)
    {
        event.register("geometry", new BBGeometryLoader());
    }

    private void onRegisterClientResourceReload(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) pResourceManager -> BBLoader.INSTANCE.invalidate());
    }
}
