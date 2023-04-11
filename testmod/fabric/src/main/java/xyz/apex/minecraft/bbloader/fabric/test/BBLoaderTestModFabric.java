package xyz.apex.minecraft.bbloader.fabric.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import xyz.apex.minecraft.bbloader.test.BBLoaderTestMod;
import xyz.apex.minecraft.bbloader.test.TestBlock;

public final class BBLoaderTestModFabric implements BBLoaderTestMod, ModInitializer
{
    @Override
    public void onInitialize()
    {
        registerBlock(PLUSH_BLOCK_NAME);
        registerBlock(MUSHROOM_BLOCK_NAME);
    }

    private void registerBlock(ResourceLocation blockName)
    {
        var block = Registry.register(BuiltInRegistries.BLOCK, blockName, new TestBlock(BLOCK_PROPERTIES.get()));
        var item = Registry.register(BuiltInRegistries.ITEM, blockName, new BlockItem(block, new Item.Properties()));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.accept(item));
    }
}
