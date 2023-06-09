package xyz.apex.minecraft.bbloader.forge;

import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.apex.minecraft.bbloader.common.api.BBLoader;

final class BBLoaderForgeClient
{
    BBLoaderForgeClient()
    {
        BBLoader.INSTANCE.bootstrap();
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onRegisterClientResourceReload);
        bus.addListener(this::onRegisterGeometryLoader);
    }

    private void onRegisterGeometryLoader(ModelEvent.RegisterGeometryLoaders event)
    {
        event.register("geometry", new BBGeometryLoader());
    }

    private void onRegisterClientResourceReload(RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener(BBLoader.INSTANCE);
    }
}
