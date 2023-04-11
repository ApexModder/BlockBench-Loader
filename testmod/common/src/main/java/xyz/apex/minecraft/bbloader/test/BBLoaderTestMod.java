package xyz.apex.minecraft.bbloader.test;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public interface BBLoaderTestMod
{
    String ID = "bbloader_test_mod";

    ResourceLocation PLUSH_BLOCK_NAME = new ResourceLocation(ID, "plush");
    ResourceLocation MUSHROOM_BLOCK_NAME = new ResourceLocation(ID, "mushroom");

    Supplier<BlockBehaviour.Properties> BLOCK_PROPERTIES = () -> BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion();
}
