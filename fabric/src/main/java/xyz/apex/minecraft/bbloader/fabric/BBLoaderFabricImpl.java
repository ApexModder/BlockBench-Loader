package xyz.apex.minecraft.bbloader.fabric;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemDisplayContext;
import xyz.apex.minecraft.bbloader.common.BBLoaderImpl;
import xyz.apex.minecraft.bbloader.common.VanillaHelper;
import xyz.apex.minecraft.bbloader.common.api.model.BBModel;

public final class BBLoaderFabricImpl extends BBLoaderImpl
{
    @Override
    public ItemTransforms itemTransforms(BBModel bbModel)
    {
        return new ItemTransforms(
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.HEAD)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.GUI)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.GROUND)),
                VanillaHelper.itemTransform(bbModel, bbModel.display(ItemDisplayContext.FIXED))
        );
    }
}
