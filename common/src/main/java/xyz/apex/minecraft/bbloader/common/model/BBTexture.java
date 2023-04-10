package xyz.apex.minecraft.bbloader.common.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import xyz.apex.minecraft.bbloader.common.BBLoader;
import xyz.apex.minecraft.bbloader.common.JsonHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
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
        @SerializedName("source") @Nullable String base64 /* optional, default: null */
)
{
    public ResourceLocation textureKey(boolean builtIn)
    {
        var name = StringUtils.removeEndIgnoreCase(this.name, ".png"); // remove file ext, its not required in texture path
        var folder = StringUtils.removeEnd(this.folder, "/"); // if ending with / remove it, formatting will add one
        if(builtIn) folder = StringUtils.appendIfMissingIgnoreCase(folder, "/builtin"); // append builtin folder if its missing
        var path = "%s/%s".formatted(folder, name);
        // registers texture leading to following path
        // <namespace>:block/[<folder>/]<path>
        return new ResourceLocation(namespace, path);
    }

    @Nullable
    public NativeImage source()
    {
        if(base64 == null) return null;

        try
        {
            // 'data:image/png;base64,' causes image to be "corrupted"
            var index = base64.indexOf(',');
            if(index == -1) return null;
            var bytes = Base64.getMimeDecoder().decode(base64.substring(index));
            return NativeImage.read(bytes); // NativeImage (Mojang) rather than BufferedImage, should not use java awt were possible
        }
        catch(IllegalArgumentException | IOException e)
        {
            BBLoader.LOGGER.warn("Error occurred while parsing Base64 encoded source image data!", e);
            return null;
        }
    }

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
                    GsonHelper.getAsString(root, "source", null)
            );
        }
    }
}
