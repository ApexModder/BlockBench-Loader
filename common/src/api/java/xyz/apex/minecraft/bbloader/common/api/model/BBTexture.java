package xyz.apex.minecraft.bbloader.common.api.model;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface BBTexture
{
    @Nullable String path();

    String name();

    String folder();

    String namespace();

    String id();

    boolean particle();

    @Nullable String renderMode();

    boolean visible();

    String mode();

    boolean saved();

    @Nullable UUID uuid();

    @Nullable String relativePath();

    @Nullable String base64();

    // region: Helpers
    ResourceLocation textureKey(boolean builtIn);

    @Nullable NativeImage source();
    // endregion
}
