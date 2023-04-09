package xyz.apex.minecraft.bbloader.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import xyz.apex.minecraft.bbloader.common.BBLoader;

@Mod(BBLoader.ID)
public final class BBLoaderForge implements BBLoader
{
    public BBLoaderForge()
    {
        bootstrap();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> BBLoaderForgeClient::new);
    }
}
