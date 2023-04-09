package xyz.apex.minecraft.bbloader.common.model;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import xyz.apex.minecraft.bbloader.common.JsonHelper;

import java.awt.image.BufferedImage;
import java.lang.reflect.Type;
import java.util.UUID;

public record BBTexture(
        @Nullable String path, /* optional, default: null */
        String name, /* required */
        String folder, /* required */
        String namespace, /* required */
        String id, /* required */
        boolean particle, /* optional, default: false */
        @SerializedName("render_mode") @Nullable String renderMode, /* optional, default: null */
        boolean visible, /* optional, default: true */
        @Nullable String mode, /* optional, default: null */
        boolean saved, /* optional, default: false */
        @Nullable UUID uuid, /* optional, default: null */
        @SerializedName("relative_path") @Nullable String relativePath, /* optional, default: null */
        @Nullable BufferedImage source /* optional, default: null */
)
{
    public static final class Deserializer implements JsonDeserializer<BBTexture>
    {
        @Override
        public BBTexture deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException
        {
            var root = json.getAsJsonObject();
            return new BBTexture(
                    GsonHelper.getAsString(root, "path", null),
                    GsonHelper.getAsString(root, "name"),
                    GsonHelper.getAsString(root, "folder"),
                    GsonHelper.getAsString(root, "namespace"),
                    GsonHelper.getAsString(root, "id"),
                    GsonHelper.getAsBoolean(root, "particle", false),
                    GsonHelper.getAsString(root, "render_mode", null),
                    GsonHelper.getAsBoolean(root, "visible", true),
                    GsonHelper.getAsString(root, "mode", null),
                    GsonHelper.getAsBoolean(root, "saved", false),
                    JsonHelper.parseUUID(root, "uuid"),
                    GsonHelper.getAsString(root, "relativePath", null),
                    parseSource(root)
            );
        }

        @Nullable
        private BufferedImage parseSource(JsonObject root) throws JsonParseException
        {
            // TODO: Look into loading the image data and seeing if we can have minecraft use it for the texture slot, rather than a texture file
            return null;
            /*if(!GsonHelper.isStringValue(root, "source")) return null;
            var source = GsonHelper.getAsString(root, "source");

            try
            {
                var bytes = Base64.getDecoder().decode(source);
                return ImageIO.read(new ByteArrayInputStream(bytes));
            }
            catch(IllegalArgumentException | IOException e)
            {
                BBLoader.LOGGER.warn("Error occurred while parsing Base64 encoded source image data!", e);
                return null;
            }*/
        }
    }
}
