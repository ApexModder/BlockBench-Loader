package xyz.apex.minecraft.bbloader.common.api.model;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2fc;
import org.joml.Vector3fc;

import java.util.Map;
import java.util.UUID;

public interface BBElement
{
    String name();

    boolean rescale();

    boolean locked();

    Vector3fc from();

    Vector3fc to();

    boolean autoUV();

    int color();

    boolean visibility();

    boolean export();

    float inflate();

    Vector3fc rotation();

    Vector3fc origin();

    Vector2fc uvOffset();

    Map<Direction, BBFace> faces();

    @Nullable String type();

    @Nullable UUID uuid();

    // region: Helpers
    @Nullable Direction.Axis rotationAxis();
    // endregion
}
