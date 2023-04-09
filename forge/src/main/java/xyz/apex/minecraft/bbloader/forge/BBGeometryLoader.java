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
import xyz.apex.minecraft.bbloader.common.BBLoader;
import xyz.apex.minecraft.bbloader.common.VanillaHelper;
import xyz.apex.minecraft.bbloader.common.model.BlockBenchModel;

import java.util.function.Function;

public final class BBGeometryLoader implements IGeometryLoader<BBGeometryLoader.BBGeometry>
{
    @Override
    public BBGeometry read(JsonObject root, JsonDeserializationContext ctx) throws JsonParseException
    {
        var bbModel = BBLoader.getModel(root, false);
        Validate.notNull(bbModel); // optional==false | should never be null
        return new BBGeometry(bbModel);
    }

    public static final class BBGeometry implements IUnbakedGeometry<BBGeometry>
    {
        private final BlockBenchModel bbModel;

        private BBGeometry(BlockBenchModel bbModel)
        {
            this.bbModel = bbModel;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
        {
            var vanilla = VanillaHelper.toVanilla(bbModel);
            return vanilla.bake(baker, vanilla, spriteGetter, modelState, modelLocation, true);
        }
    }
}
