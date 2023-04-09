package xyz.apex.minecraft.bbloader.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import xyz.apex.minecraft.bbloader.common.BBLoader;
import xyz.apex.minecraft.bbloader.common.DebugData;

public final class BBLoaderFabric implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        DebugData.register().ifPresent(pair -> {
            var block = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(BBLoader.ID, "test_block"), pair.getFirst().get());
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(BBLoader.ID, "test_block"), pair.getSecond().apply(block));
            ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> entries.accept(block));
            BBLoader.LOGGER.debug("Registered Fabric Debug Data!");
        });
    }
}
