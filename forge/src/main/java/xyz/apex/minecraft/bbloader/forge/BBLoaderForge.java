package xyz.apex.minecraft.bbloader.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import xyz.apex.minecraft.bbloader.common.api.BBLoader;

@Mod(BBLoader.ID)
public final class BBLoaderForge
{
    public BBLoaderForge()
    {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> BBLoaderForgeClient::new);
    }
}
