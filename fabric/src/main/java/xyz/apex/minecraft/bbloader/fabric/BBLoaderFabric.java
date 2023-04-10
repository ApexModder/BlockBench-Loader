package xyz.apex.minecraft.bbloader.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import xyz.apex.minecraft.bbloader.common.DebugData;

public final class BBLoaderFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        DebugData.register((blockName, blockFactory) -> {
            var block = Registry.register(BuiltInRegistries.BLOCK, blockName, blockFactory.get());
            return () -> block;
        }, (itemName, blockSupplier) -> {
            var item = Registry.register(BuiltInRegistries.ITEM, itemName, new BlockItem(blockSupplier.get(), new Item.Properties()));
            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.accept(item));
        });
    }
}
