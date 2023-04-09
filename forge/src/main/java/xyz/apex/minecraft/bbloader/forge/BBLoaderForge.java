package xyz.apex.minecraft.bbloader.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xyz.apex.minecraft.bbloader.common.BBLoader;

@Mod(BBLoader.ID)
public final class BBLoaderForge implements BBLoader
{
    public static final RegistryObject<Item> TEST_BLOCK = RegistryObject.createOptional(new ResourceLocation(ID, "test_block"), ForgeRegistries.Keys.ITEMS, ID);

    public BBLoaderForge()
    {
        BBLoader.bootstrap().ifPresent(pair -> {
            var bus = FMLJavaModLoadingContext.get().getModEventBus();
            var blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, ID);
            var items = DeferredRegister.create(ForgeRegistries.ITEMS, ID);
            var block = blocks.register("test_block", pair.getFirst());
            items.register("test_block", () -> pair.getSecond().apply(block.get()));
            blocks.register(bus);
            items.register(bus);
            bus.addListener(this::onBuildCreativeModeTabContents);
            LOGGER.warn("Register Forge Debug Data!");
        });

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> BBLoaderForgeClient::new);
    }

    private void onBuildCreativeModeTabContents(CreativeModeTabEvent.BuildContents event)
    {
        if(event.getTab() != CreativeModeTabs.TOOLS_AND_UTILITIES) return;
        TEST_BLOCK.ifPresent(event::accept);
    }
}
