package xyz.apex.minecraft.bbloader.common.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public record BBResolution(
        @SerializedName("width") int textureWidth, /* required */
        @SerializedName("height") int textureHeight /* required */
)
{
    public static final class Deserializer implements JsonDeserializer<BBResolution>
    {
        @Override
        public BBResolution deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            return new BBResolution(
                    GsonHelper.getAsInt(root, "width"),
                    GsonHelper.getAsInt(root, "height")
            );
        }
    }
}
