package xyz.apex.minecraft.bbloader.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import xyz.apex.minecraft.bbloader.common.model.BBDisplay;
import xyz.apex.minecraft.bbloader.common.model.BBElement;
import xyz.apex.minecraft.bbloader.common.model.BBFace;
import xyz.apex.minecraft.bbloader.common.model.BlockBenchModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public interface VanillaHelper
{
    static BlockModel toVanilla(BlockBenchModel bbModel)
    {
        return new BlockModel(
                null, // TODO: Parents?
                elements(bbModel),
                textures(bbModel),
                bbModel.ambientOcclusion(),
                bbModel.frontGuiLight() ? BlockModel.GuiLight.FRONT : BlockModel.GuiLight.SIDE,
                BBLoader.modLoader().buildItemTransforms(bbModel),
                List.of() // TODO: ItemPredicates
        );
    }

    static Map<String, Either<Material, String>> textures(BlockBenchModel bbModel)
    {
        var particle = new AtomicBoolean(false);
        var textures = ImmutableMap.<String, Either<Material, String>>builder();
        // ensure missingno exists, this is a builtin default texture everything defaults to in case of an error
        textures.put("missingno", Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation())));

        bbModel.textures().forEach(bbTexture -> {
            var textureId = bbTexture.id();

            if(bbTexture.particle())
            {
                if(particle.get()) throw new RuntimeException("Duplicate particle texture definition!");
                particle.set(true);
                // route particle texture to point towards this texture
                textures.put(BlockModel.PARTICLE_TEXTURE_REFERENCE, Either.right("%s".formatted(textureId)));
            }

            textures.put(textureId, Either.left(new Material(TextureAtlas.LOCATION_BLOCKS, bbTexture.textureKey(false))));
        });

        // ensure particle always exists
        if(!particle.get())
        {
            if(bbModel.textures().isEmpty()) textures.put(BlockModel.PARTICLE_TEXTURE_REFERENCE, Either.right("missingno")); // default to #missingno
            else
            {
                // default to first texture, if no particle defined
                var texture = bbModel.textures().get(0);
                textures.put(BlockModel.PARTICLE_TEXTURE_REFERENCE, Either.right(texture.id()));
            }
        }

        return textures.build();
    }

    static List<BlockElement> elements(BlockBenchModel bbModel)
    {
        var elements = ImmutableList.<BlockElement>builder();

        bbModel.elements().forEach(bbElement -> {
            var element = element(bbModel, bbElement);
            if(element != null) elements.add(element);
        });

        return elements.build();
    }

    @Nullable
    static BlockElement element(BlockBenchModel bbModel, BBElement bbElement)
    {
        if(!bbElement.export() || !bbElement.visibility()) return null;

        var from = bbElement.from();
        var to = bbElement.to();
        var inflate = bbElement.inflate();

        if(inflate != 0F)
        {
            from = from.sub(inflate, inflate, inflate, new Vector3f());
            to = to.add(inflate, inflate, inflate, new Vector3f());
        }

        return new BlockElement(
                from,
                to,
                elementFaces(bbModel, bbElement),
                elementRotation(bbModel, bbElement),
                true // TODO: can this shade flag be loaded from bbmodel json?
        );
    }

    static Map<Direction, BlockElementFace> elementFaces(BlockBenchModel bbModel, BBElement bbElement)
    {
        var map = ImmutableMap.<Direction, BlockElementFace>builder();

        bbElement.faces().forEach((face, bbFace) -> {
            var elementFace = elementFace(bbModel, bbElement, bbFace);
            map.put(face, elementFace);
        });

        return map.build();
    }

    static BlockElementFace elementFace(BlockBenchModel bbModel, BBElement bbElement, BBFace bbFace)
    {
        return new BlockElementFace(
                null, // TODO: cull face support
                bbFace.tintIndex(),
                textureId(bbModel, bbElement, bbFace),
                elementFaceUV(bbModel, bbElement, bbFace)
        );
    }

    static String textureId(BlockBenchModel bbModel, BBElement bbElement, BBFace bbFace)
    {
        var faceTexture = bbFace.texture();
        var textures = bbModel.textures();
        if(textures.isEmpty() || faceTexture < 0 || faceTexture > textures.size() - 1) return "missingno";
        return textures.get(faceTexture).id();
    }

    static BlockFaceUV elementFaceUV(BlockBenchModel bbModel, BBElement bbElement, BBFace bbFace)
    {
        return new BlockFaceUV(
                uvs(bbModel, bbElement, bbFace),
                bbFace.rotation()
        );
    }

    @Nullable
    static BlockElementRotation elementRotation(BlockBenchModel bbModel, BBElement bbElement)
    {
        var axis = bbElement.rotationAxis();
        if(axis == null) return null;

        return new BlockElementRotation(
                new Vector3f(bbElement.origin().mul(.0625F, new Vector3f())),
                axis,
                switch(axis) {
                    case X -> bbElement.rotation().x();
                    case Y -> bbElement.rotation().y();
                    case Z -> bbElement.rotation().z();
                },
                bbElement.rescale()
        );
    }

    static ItemTransform itemTransform(BlockBenchModel bbModel, BBDisplay bbDisplay)
    {
        var rotation = new Vector3f(bbDisplay.rotation());

        var translation = new Vector3f(bbDisplay.translation());
        translation.mul(.0625F);
        translation.set(
                Mth.clamp(translation.x(), -5F, 5F),
                Mth.clamp(translation.y(), -5F, 5F),
                Mth.clamp(translation.z(), -5F, 5F)
        );

        var scale = new Vector3f(bbDisplay.scale());
        scale.set(
                Mth.clamp(scale.x(), -4F, 4F),
                Mth.clamp(scale.y(), -4F, 4F),
                Mth.clamp(scale.z(), -4F, 4F)
        );

        return new ItemTransform(rotation, translation, scale);
    }

    static float[] uvs(BlockBenchModel bbModel, BBElement bbElement, BBFace bbFace)
    {
        var uv = bbFace.uv();
        return new float[] {
                uv.x() * 16F / bbModel.resolution().textureWidth(),
                uv.y() * 16F / bbModel.resolution().textureHeight(),
                uv.z() * 16F / bbModel.resolution().textureWidth(),
                uv.w() * 16F / bbModel.resolution().textureHeight()
        };
    }
}
