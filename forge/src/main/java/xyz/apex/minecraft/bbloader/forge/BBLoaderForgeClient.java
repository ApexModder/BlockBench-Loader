package xyz.apex.minecraft.bbloader.forge;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

final class BBLoaderForgeClient
{
    BBLoaderForgeClient()
    {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
    }
}
