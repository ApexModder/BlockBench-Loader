package xyz.apex.minecraft.bbloader.forge.test;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.apex.minecraft.bbloader.test.BBLoaderTestMod;
import xyz.apex.minecraft.bbloader.test.TestBlock;

@Mod(BBLoaderTestMod.ID)
public final class BBLoaderTestModForge implements BBLoaderTestMod
{
    private final RegistryObject<TestBlock> plushBlock;
    private final RegistryObject<TestBlock> mushroomBlock;
    private final RegistryObject<BlockItem> plushBlockItem;
    private final RegistryObject<BlockItem> mushroomBlockItem;

    public BBLoaderTestModForge()
    {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        var blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
        var items = DeferredRegister.create(ForgeRegistries.ITEMS, ID);

        plushBlock = blocks.register(PLUSH_BLOCK_NAME.getPath(), () -> new TestBlock(BLOCK_PROPERTIES.get()));
        mushroomBlock = blocks.register(MUSHROOM_BLOCK_NAME.getPath(), () -> new TestBlock(BLOCK_PROPERTIES.get()));

        plushBlockItem = items.register(PLUSH_BLOCK_NAME.getPath(), () -> new BlockItem(plushBlock.get(), new Item.Properties()));
        mushroomBlockItem = items.register(MUSHROOM_BLOCK_NAME.getPath(), () -> new BlockItem(mushroomBlock.get(), new Item.Properties()));

        blocks.register(bus);
        items.register(bus);
        bus.addListener(this::onBuildCreativeModeTabContents);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Client::new);
    }

    private void onBuildCreativeModeTabContents(CreativeModeTabEvent.BuildContents event)
    {
        if(event.getTab() != CreativeModeTabs.TOOLS_AND_UTILITIES) return;
        plushBlockItem.ifPresent(event::accept);
        mushroomBlockItem.ifPresent(event::accept);
    }

    private final class Client
    {
        private Client()
        {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        }

        private void onClientSetup(FMLClientSetupEvent event)
        {
            plushBlock.ifPresent(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout()));
            // mushroomBlock.ifPresent(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout()));
        }
    }
}
