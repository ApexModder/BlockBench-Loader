package xyz.apex.minecraft.bbloader.common.model;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.GsonHelper;
import org.joml.Vector4fc;
import xyz.apex.minecraft.bbloader.common.api.JsonHelper;
import xyz.apex.minecraft.bbloader.common.api.model.BBFace;

import java.lang.reflect.Type;

public record BBFaceImpl(
        Vector4fc uv, /* required */
        int rotation, /* optional, default: 0 */
        int texture, /* optional, default: -1 */
        @SerializedName("tintindex") int tintIndex /* optional, default: -1 */
) implements BBFace
{
    public static final class Deserializer implements JsonDeserializer<BBFace>
    {
        @Override
        public BBFace deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            return new BBFaceImpl(
                    JsonHelper.parseVector4(root, "uv", null),
                    parseRotation(root),
                    GsonHelper.getAsInt(root, "texture", -1),
                    GsonHelper.getAsInt(root, "tintindex", -1)
            );
        }

        private int parseRotation(JsonObject root) throws JsonParseException
        {
            if(!GsonHelper.isNumberValue(root, "rotation")) return 0;
            var rotation = GsonHelper.getAsInt(root, "rotation");
            if(rotation != 0 && rotation != 90 && rotation != 180 && rotation != 270) throw new JsonParseException("Invalid face rotation: %d, only 0/90/180/270 allowed".formatted(rotation));
            return rotation;
        }
    }
}
