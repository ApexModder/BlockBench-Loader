package xyz.apex.minecraft.bbloader.common.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public record BBMeta(
        @SerializedName("format_version") String version, /* required */
        @SerializedName("creation_time") long creationTime, /* optional, default: -1L */
        @SerializedName("model_format") String modelFormat, /* optional, default: '' */
        @SerializedName("box_uv") boolean boxUV /* optional, default: false */
)
{
    public static final class Deserializer implements JsonDeserializer<BBMeta>
    {
        @Override
        public BBMeta deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            var formatVersion = GsonHelper.getAsString(root, "format_version");
            var creationTime = GsonHelper.getAsLong(root, "creation_time", -1L);
            var modelFormat = GsonHelper.getAsString(root, "model_format", "");
            var boxUV = GsonHelper.getAsBoolean(root, "box_uv", false);
            return new BBMeta(
                    formatVersion,
                    creationTime,
                    modelFormat,
                    boxUV
            );
        }
    }
}
