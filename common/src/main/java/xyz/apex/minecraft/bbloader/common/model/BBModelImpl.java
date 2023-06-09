package xyz.apex.minecraft.bbloader.common.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import xyz.apex.minecraft.bbloader.common.api.model.*;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record BBModelImpl(
        @Nullable BBMeta meta, /* optional, default: null */

        String name, /* required */
        @Nullable String parent, /* optional, default: null */
        @SerializedName("ambientoclusion") boolean ambientOcclusion, /* optional, default: false */
        @SerializedName("front_gui_light") boolean frontGuiLight, /* optional, default: false */

        BBResolution resolution, /* required */
        List<BBElement> elements, /* optional, default: [ ] | while this is optional, it should rarely ever be empty */
        List<BBTexture> textures, /* optional, default: [ ] | while this is optional, it should rarely ever be empty */

        @SerializedName("display") Map<ItemDisplayContext, BBDisplay> displays /* optional, default: { } */

        // TODO: Missing elements from bbmodel spec
        //  visible_box
        //  variable_placeholders
        //  variable_placeholder_buttons
        //  outliner
) implements BBModel
{
    public static final Gson GSON = Util.make(new GsonBuilder(), builder -> builder
            .registerTypeAdapter(BBModel.class, new Deserializer())
            .registerTypeAdapter(BBMeta.class, new BBMetaImpl.Deserializer())
            .registerTypeAdapter(BBResolution.class, new BBResolutionImpl.Deserializer())
            .registerTypeAdapter(BBElement.class, new BBElementImpl.Deserializer())
            .registerTypeAdapter(BBFace.class, new BBFaceImpl.Deserializer())
            .registerTypeAdapter(BBDisplay.class, new BBDisplayImpl.Deserializer())
            .registerTypeAdapter(BBTexture.class, new BBTextureImpl.Deserializer())
    ).create();

    public static BBModel fromReader(Reader reader)
    {
        return GSON.fromJson(reader, BBModel.class);
    }

    public static BBModel fromJson(JsonElement json)
    {
        return GSON.fromJson(json, BBModel.class);
    }

    @Override
    public BBDisplay display(ItemDisplayContext ctx)
    {
        return displays.getOrDefault(ctx, BBDisplayImpl.EMPTY);
    }

    public static final class Deserializer implements JsonDeserializer<BBModel>
    {
        @Override
        public BBModel deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            return new BBModelImpl(
                    parseMeta(root, ctx),

                    GsonHelper.getAsString(root, "name"),
                    GsonHelper.getAsString(root, "parent", null),
                    GsonHelper.getAsBoolean(root, "ambientocclusion", false),
                    GsonHelper.getAsBoolean(root, "front_gui_light", false),

                    ctx.deserialize(GsonHelper.getAsJsonObject(root, "resolution"), BBResolution.class),
                    parseElements(root, ctx),
                    parseTextures(root, ctx),
                    parseDisplays(root, ctx)
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

        private Map<ItemDisplayContext, BBDisplay> parseDisplays(JsonObject root, JsonDeserializationContext ctx) throws JsonParseException
        {
            var map = ImmutableMap.<ItemDisplayContext, BBDisplay>builder();

            if(GsonHelper.isObjectNode(root, "display"))
            {
                var displaysJson = GsonHelper.getAsJsonObject(root, "display");

                // dont cache .values() like we do for Direction.values()
                // since forge allows modders to extend this enum
                // and add custom display contexts
                // this should ensure we pick up those custom types
                // and try to deserialize them for our models
                for(var itemDisplayCtx : ItemDisplayContext.values())
                {
                    var serializedName = itemDisplayCtx.getSerializedName();
                    if(!GsonHelper.isObjectNode(displaysJson, serializedName)) continue;
                    map.put(itemDisplayCtx, ctx.deserialize(GsonHelper.getAsJsonObject(displaysJson, serializedName), BBDisplay.class));
                }
            }

            return map.build();
        }

        private List<BBTexture> parseTextures(JsonObject root, JsonDeserializationContext ctx) throws JsonParseException
        {
            var textures = ImmutableList.<BBTexture>builder();

            if(GsonHelper.isArrayNode(root, "textures"))
            {
                for(var element : GsonHelper.getAsJsonArray(root, "textures"))
                {
                    textures.add((BBTexture) ctx.deserialize(element, BBTexture.class));
                }
            }

            return textures.build();
        }
    }
}
