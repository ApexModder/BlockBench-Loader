package xyz.apex.minecraft.bbloader.common.model;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import xyz.apex.minecraft.bbloader.common.api.JsonHelper;
import xyz.apex.minecraft.bbloader.common.api.model.BBElement;
import xyz.apex.minecraft.bbloader.common.api.model.BBFace;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public record BBElementImpl(
    String name, /* required */
    boolean rescale, /* optional, default: false */
    boolean locked, /* optional, default: false */
    Vector3fc from, /* required */
    Vector3fc to, /* required */
    @SerializedName("autouv") boolean autoUV, /* optional, default: false */
    int color, /* optional, default: -1 */
    boolean visibility, /* optional, default: true */
    boolean export, /* optional, default: true */
    float inflate, /* optional, default: 0 */
    Vector3fc rotation, /* optional, default [ 0, 0, 0 ] */
    Vector3fc origin, /* required */
    @SerializedName("uv_offset") Vector2fc uvOffset, /* optional, default: [ 0, 0 ] */
    Map<Direction, BBFace> faces, /* optional, default: { } | while this is optional it should rarely ever be empty */
    @Nullable String type, /* optional, default: null */
    @Nullable UUID uuid /* optional, default: null */
) implements BBElement
{
    @Nullable
    @Override
    public Direction.Axis rotationAxis()
    {
        if(rotation.x() != 0F) return Direction.Axis.X;
        else if(rotation.y() != 0F) return Direction.Axis.Y;
        else if(rotation.z() != 0F) return Direction.Axis.Z;
        else return null;
    }

    public static final class Deserializer implements JsonDeserializer<BBElement>
    {
        private static final Direction[] DIRECTIONS = Direction.values();

        @Override
        public BBElement deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            return new BBElementImpl(
                    GsonHelper.getAsString(root, "name"),
                    GsonHelper.getAsBoolean(root, "rescale", false),
                    GsonHelper.getAsBoolean(root, "locked", false),
                    JsonHelper.parseVector3(root, "from", null),
                    JsonHelper.parseVector3(root, "to", null),
                    GsonHelper.getAsBoolean(root, "autouv", false),
                    GsonHelper.getAsInt(root, "color", -1),
                    GsonHelper.getAsBoolean(root, "visibility", true),
                    GsonHelper.getAsBoolean(root, "export", true),
                    GsonHelper.getAsFloat(root, "inflate", 0F),
                    JsonHelper.parseVector3(root, "rotation", Vector3f::new),
                    JsonHelper.parseVector3(root, "origin", null),
                    JsonHelper.parseVector2(root, "uv_offset", Vector2f::new),
                    parseFaces(root, ctx),
                    GsonHelper.getAsString(root, "type", null),
                    JsonHelper.parseUUID(root, "uuid")
            );
        }

        private Map<Direction, BBFace> parseFaces(JsonObject root, JsonDeserializationContext ctx) throws JsonParseException
        {
            var map = ImmutableMap.<Direction, BBFace>builder();

            if(GsonHelper.isObjectNode(root, "faces"))
            {
                var facesJson = GsonHelper.getAsJsonObject(root, "faces");

                for(var face : DIRECTIONS)
                {
                    var bbFace = parseFace(facesJson, face, ctx);
                    if(bbFace != null) map.put(face, bbFace);
                }
            }

            return map.build();
        }

        @Nullable
        private BBFace parseFace(JsonObject root, Direction face, JsonDeserializationContext ctx) throws JsonParseException
        {
            var serializedName = face.getSerializedName();
            if(!GsonHelper.isObjectNode(root, serializedName)) return null;
            return ctx.deserialize(GsonHelper.getAsJsonObject(root, serializedName), BBFace.class);
        }
    }
}
