package xyz.apex.minecraft.bbloader.forge;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;
import xyz.apex.minecraft.bbloader.common.ModLoader;
import xyz.apex.minecraft.bbloader.common.VanillaHelper;
import xyz.apex.minecraft.bbloader.common.model.BlockBenchModel;

final class ForgeModLoader implements ModLoader
{
    @Override
    public ItemTransforms buildItemTransforms(BlockBenchModel bbModel)
    {
        var modded = ImmutableMap.<ItemDisplayContext, ItemTransform>builder();

        bbModel.displays().forEach((ctx, bbDisplay) -> {
            if(ctx.isModded()) modded.put(ctx, VanillaHelper.itemTransform(bbModel, bbDisplay));
        });

        return new ItemTransforms(
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.HEAD)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.GUI)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.GROUND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.FIXED)),
                modded.build()
        );
    }
}
