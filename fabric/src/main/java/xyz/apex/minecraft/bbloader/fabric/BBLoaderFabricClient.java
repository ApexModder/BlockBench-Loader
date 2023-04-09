package xyz.apex.minecraft.bbloader.fabric;

import net.fabricmc.api.ClientModInitializer;
import xyz.apex.minecraft.bbloader.common.BBLoader;

public final class BBLoaderFabricClient implements BBLoader, ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        bootstrap();
    }
}
