package xyz.apex.minecraft.bbloader.common.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public record BlockBenchModel(
        @Nullable BBMeta meta, /* optional, default: null */

        String name, /* required */
        @Nullable String parent, /* optional, default: null */
        @SerializedName("ambientoclusion") boolean ambientOcclusion, /* optional, default: false */
        @SerializedName("front_gui_light") boolean frontGuiLight, /* optional, default: false */

        BBResolution resolution, /* required */
        List<BBElement> elements /* optional, default: [ ] | while this is optional, it should rarely ever be empty */

        // TODO: Missing elements from bbmodel spec
        //  visible_box
        //  variable_placeholders
        //  variable_placeholder_buttons
        //  outliner
        //  textures
        //  display
)
{
    public static final Gson GSON = Util.make(new GsonBuilder(), builder -> builder
            .registerTypeAdapter(BlockBenchModel.class, new Deserializer())
            .registerTypeAdapter(BBMeta.class, new BBMeta.Deserializer())
            .registerTypeAdapter(BBResolution.class, new BBResolution.Deserializer())
            .registerTypeAdapter(BBElement.class, new BBElement.Deserializer())
            .registerTypeAdapter(BBFace.class, new BBFace.Deserializer())
    ).create();

    public static BlockBenchModel fromReader(Reader reader)
    {
        return GSON.fromJson(reader, BlockBenchModel.class);
    }

    public static BlockBenchModel fromJson(JsonElement json)
    {
        return GSON.fromJson(json, BlockBenchModel.class);
    }

    public static final class Deserializer implements JsonDeserializer<BlockBenchModel>
    {
        @Override
        public BlockBenchModel deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            return new BlockBenchModel(
                    parseMeta(root, ctx),

                    GsonHelper.getAsString(root, "name"),
                    GsonHelper.getAsString(root, "parent", null),
                    GsonHelper.getAsBoolean(root, "ambientocclusion", false),
                    GsonHelper.getAsBoolean(root, "front_gui_light", false),

                    ctx.deserialize(GsonHelper.getAsJsonObject(root, "resolution"), BBResolution.class),
                    parseElements(root, ctx)
            );
        }

        @Nullable
        private BBMeta parseMeta(JsonObject root, JsonDeserializationContext ctx) throws JsonParseException
        {
            if(!GsonHelper.isObjectNode(root, "meta")) return null;
            return ctx.deserialize(GsonHelper.getAsJsonObject(root, "meta"), BBMeta.class);
        }

        private List<BBElement> parseElements(JsonObject root, JsonDeserializationContext ctx) throws JsonParseException
        {
            if(!GsonHelper.isArrayNode(root, "elements")) return Collections.emptyList();
            var elements = ImmutableList.<BBElement>builder();

            for(var jsonElement : GsonHelper.getAsJsonArray(root, "elements"))
            {
                elements.add((BBElement) ctx.deserialize(jsonElement, BBElement.class));
            }

            return elements.build();
        }
    }
}
