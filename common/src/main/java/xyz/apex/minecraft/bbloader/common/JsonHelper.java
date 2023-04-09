package xyz.apex.minecraft.bbloader.common;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.UUID;

public interface JsonHelper
{
    static Vector4f parseVector4(JsonObject root, String key, boolean optional) throws JsonParseException
    {
        if(optional && !GsonHelper.isArrayNode(root, key)) return new Vector4f();
        var array = GsonHelper.getAsJsonArray(root, key);
        if(array.size() != 4) throw new JsonParseException("Expected array with size of 4, found: %d".formatted(array.size()));
        return new Vector4f(
                GsonHelper.convertToFloat(array.get(0), "%s[0]".formatted(key)),
                GsonHelper.convertToFloat(array.get(1), "%s[1]".formatted(key)),
                GsonHelper.convertToFloat(array.get(2), "%s[2]".formatted(key)),
                GsonHelper.convertToFloat(array.get(3), "%s[3]".formatted(key))
        );
    }

    static Vector3f parseVector3(JsonObject root, String key, boolean optional) throws JsonParseException
    {
        if(optional && !GsonHelper.isArrayNode(root, key)) return new Vector3f();
        var array = GsonHelper.getAsJsonArray(root, key);
        if(array.size() != 3) throw new JsonParseException("Expected array with size of 3, found: %d".formatted(array.size()));
        return new Vector3f(
                GsonHelper.convertToFloat(array.get(0), "%s[0]".formatted(key)),
                GsonHelper.convertToFloat(array.get(1), "%s[1]".formatted(key)),
                GsonHelper.convertToFloat(array.get(2), "%s[2]".formatted(key))
        );
    }

    static Vector2f parseVector2(JsonObject root, String key, boolean optional) throws JsonParseException
    {
        if(optional && !GsonHelper.isArrayNode(root, key)) return new Vector2f();
        var array = GsonHelper.getAsJsonArray(root, key);
        if(array.size() != 2) throw new JsonParseException("Expected array with size of 2, found: %d".formatted(array.size()));
        return new Vector2f(
                GsonHelper.convertToFloat(array.get(0), "%s[0]".formatted(key)),
                GsonHelper.convertToFloat(array.get(1), "%s[1]".formatted(key))
        );
    }

    @Nullable
    static UUID parseUUID(JsonObject root, String key) throws JsonParseException
    {
        if(!GsonHelper.isStringValue(root, key)) return null;
        return UUID.fromString(GsonHelper.getAsString(root, key));
    }
}
