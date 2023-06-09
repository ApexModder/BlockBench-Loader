package xyz.apex.minecraft.bbloader.common.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import xyz.apex.minecraft.bbloader.common.api.JsonHelper;
import xyz.apex.minecraft.bbloader.common.api.model.BBDisplay;

import java.lang.reflect.Type;

public record BBDisplayImpl(
        Vector3fc rotation, /* optional, default [ 0, 0, 0 ] */
        Vector3fc translation, /* optional, default [ 0, 0, 0 ] */
        Vector3fc scale /* optional, default [ 1, 1, 1 ] */
) implements BBDisplay
{
    public static final BBDisplay EMPTY = new BBDisplayImpl(new Vector3f(0F), new Vector3f(0F), new Vector3f(1F));

    public static final class Deserializer implements JsonDeserializer<BBDisplay>
    {
        @Override
        public BBDisplay deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            return new BBDisplayImpl(
                    JsonHelper.parseVector3(root, "rotation", Vector3f::new),
                    JsonHelper.parseVector3(root, "translation", Vector3f::new),
                    JsonHelper.parseVector3(root, "scale", () -> new Vector3f(1F))
            );
        }
    }
}
