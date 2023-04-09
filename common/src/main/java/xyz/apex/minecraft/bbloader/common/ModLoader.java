package xyz.apex.minecraft.bbloader.common;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import xyz.apex.minecraft.bbloader.common.model.BlockBenchModel;

public interface ModLoader
{
    ItemTransforms buildItemTransforms(BlockBenchModel bbModel);
}
