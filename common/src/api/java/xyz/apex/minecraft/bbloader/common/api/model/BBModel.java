package xyz.apex.minecraft.bbloader.common.api.model;

import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface BBModel
{
    @Nullable BBMeta meta();

    String name();

    @Nullable String parent();

    boolean ambientOcclusion();

    boolean frontGuiLight();

    BBResolution resolution();

    List<BBElement> elements();

    List<BBTexture> textures();

    Map<ItemDisplayContext, BBDisplay> displays();

    // region: Helpers
    BBDisplay display(ItemDisplayContext ctx);
    // endregion
}
