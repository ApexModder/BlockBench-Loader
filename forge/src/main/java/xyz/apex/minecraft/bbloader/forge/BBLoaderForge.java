package xyz.apex.minecraft.bbloader.forge;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.apex.minecraft.bbloader.common.BBLoader;
import xyz.apex.minecraft.bbloader.common.DebugData;

@Mod(BBLoader.ID)
public final class BBLoaderForge
{
    public static final RegistryObject<Block> PLUSH_BLOCK = RegistryObject.createOptional(DebugData.PLUSH_BLOCK_NAME, ForgeRegistries.Keys.BLOCKS, BBLoader.ID);
    public static final RegistryObject<Block> MUSHROOM_BLOCK = RegistryObject.createOptional(DebugData.MUSHROOM_BLOCK_NAME, ForgeRegistries.Keys.BLOCKS, BBLoader.ID);

    public static final RegistryObject<Item> PLUSH_ITEM = RegistryObject.createOptional(DebugData.PLUSH_BLOCK_NAME, ForgeRegistries.Keys.ITEMS, BBLoader.ID);
    public static final RegistryObject<Item> MUSHROOM_ITEM = RegistryObject.createOptional(DebugData.MUSHROOM_BLOCK_NAME, ForgeRegistries.Keys.ITEMS, BBLoader.ID);

    public BBLoaderForge()
    {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        var blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, BBLoader.ID);
        var items = DeferredRegister.create(ForgeRegistries.ITEMS, BBLoader.ID);
        blocks.register(bus);
        items.register(bus);
        bus.addListener(this::onBuildCreativeModeTabContents);

        DebugData.register(
                (blockName, blockFactory) -> blocks.register(blockName.getPath(), blockFactory)::get,
                (itemName, blockSupplier) -> items.register(itemName.getPath(), () -> new BlockItem(blockSupplier.get(), new Item.Properties()))
        );

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> BBLoaderForgeClient::new);
    }

    private void onBuildCreativeModeTabContents(CreativeModeTabEvent.BuildContents event)
    {
        if(event.getTab() != CreativeModeTabs.TOOLS_AND_UTILITIES) return;
        PLUSH_ITEM.ifPresent(event::accept);
        MUSHROOM_ITEM.ifPresent(event::accept);
    }
}
