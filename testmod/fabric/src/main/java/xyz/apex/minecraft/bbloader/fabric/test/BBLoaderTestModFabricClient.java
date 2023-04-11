package xyz.apex.minecraft.bbloader.fabric.test;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import xyz.apex.minecraft.bbloader.test.BBLoaderTestMod;

public final class BBLoaderTestModFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BuiltInRegistries.BLOCK.getOptional(BBLoaderTestMod.PLUSH_BLOCK_NAME).ifPresent(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout()));
        // BuiltInRegistries.BLOCK.getOptional(BBLoaderTestMod.MUSHROOM_BLOCK_NAME).ifPresent(block -> BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout()));
    }
}
