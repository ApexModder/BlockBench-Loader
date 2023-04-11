package xyz.apex.minecraft.bbloader.forge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.apache.commons.lang3.Validate;
import xyz.apex.minecraft.bbloader.common.VanillaHelper;
import xyz.apex.minecraft.bbloader.common.api.BBLoader;
import xyz.apex.minecraft.bbloader.common.api.model.BBModel;

import java.util.function.Function;

public final class BBGeometryLoader implements IGeometryLoader<BBGeometryLoader.BBGeometry>
{
    @Override
    public BBGeometry read(JsonObject root, JsonDeserializationContext ctx) throws JsonParseException
    {
        var bbModel = BBLoader.INSTANCE.getModel(root, false);
        Validate.notNull(bbModel); // optional==false | should never be null
        return new BBGeometry(bbModel);
    }

    public static final class BBGeometry implements IUnbakedGeometry<BBGeometry>
    {
        private final BBModel bbModel;

        private BBGeometry(BBModel bbModel)
        {
            this.bbModel = bbModel;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
        {
            // NOTE: This breaks forges render type json system,
            //  have to register render types for your blocks using BBLoader the old fashioned way
            //  ClientSetup event listener and call ItemBlockRenderTypes.setRenderLayer
            var vanilla = VanillaHelper.toVanilla(bbModel);
            return vanilla.bake(baker, vanilla, spriteGetter, modelState, modelLocation, true);
        }
    }
}
