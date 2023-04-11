package xyz.apex.minecraft.bbloader.common.api.model;

import org.joml.Vector4fc;

public interface BBFace
{
    Vector4fc uv();

    int rotation();

    int texture();

    int tintIndex();
}
