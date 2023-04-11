package xyz.apex.minecraft.bbloader.common.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.GsonHelper;
import xyz.apex.minecraft.bbloader.common.api.model.BBResolution;

import java.lang.reflect.Type;

public record BBResolutionImpl(
        @SerializedName("width") int textureWidth, /* required */
        @SerializedName("height") int textureHeight /* required */
) implements BBResolution
{
    public static final class Deserializer implements JsonDeserializer<BBResolution>
    {
        @Override
        public BBResolution deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            return new BBResolutionImpl(
                    GsonHelper.getAsInt(root, "width"),
                    GsonHelper.getAsInt(root, "height")
            );
        }
    }
}
