package xyz.apex.minecraft.bbloader.forge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import org.apache.commons.lang3.Validate;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import xyz.apex.minecraft.bbloader.common.BBLoader;
import xyz.apex.minecraft.bbloader.common.model.BBElement;
import xyz.apex.minecraft.bbloader.common.model.BBFace;
import xyz.apex.minecraft.bbloader.common.model.BlockBenchModel;

import java.util.function.Function;

public final class BBGeometryLoader implements IGeometryLoader<BBGeometryLoader.BBGeometry>
{
    @Override
    public BBGeometry read(JsonObject root, JsonDeserializationContext ctx) throws JsonParseException
    {
        var bbModel = BBLoader.getModel(root, false);
        Validate.notNull(bbModel); // optional==false | should never be null
        return new BBGeometry(bbModel);
    }

    public static final class BBGeometry extends SimpleUnbakedGeometry<BBGeometry>
    {
        private final BlockBenchModel bbModel;

        private BBGeometry(BlockBenchModel bbModel)
        {
            this.bbModel = bbModel;
        }

        @Override
        protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation)
        {
            var texture = spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(BBLoader.ID, "block/plush")));

            bbModel.elements().forEach(element -> {
                if(!element.visibility() || !element.export()) return;

                var shape = setupShape(element);

                element.faces().forEach((face, bbFace) -> {
                    modelBuilder.addUnculledFace(new BakedQuad(
                            makeVertices(face, shape, texture, element, bbFace, modelTransform.getRotation()),
                            bbFace.tintIndex(),
                            face,
                            texture,
                            true,
                            bbModel.ambientOcclusion()
                    ));
                });
            });
        }

        private float[] setupShape(BBElement element)
        {
            var from = element.from();
            var to = element.to();

            var inflate = element.inflate();

            if(inflate != 0F)
            {
                from = from.sub(inflate, inflate, inflate, new Vector3f());
                to = to.add(inflate, inflate, inflate, new Vector3f());
            }

            var shape = new float[6];
            shape[FaceInfo.Constants.MIN_X] = from.x() / 16F;
            shape[FaceInfo.Constants.MIN_Y] = from.y() / 16F;
            shape[FaceInfo.Constants.MIN_Z] = from.z() / 16F;
            shape[FaceInfo.Constants.MAX_X] = to.x() / 16F;
            shape[FaceInfo.Constants.MAX_Y] = to.y() / 16F;
            shape[FaceInfo.Constants.MAX_Z] = to.z() / 16F;
            return shape;
        }

        private int[] makeVertices(Direction facing, float[] shape, TextureAtlasSprite texture, BBElement element, BBFace face, Transformation transform)
        {
            var vertices = new int[32];

            for(var i = 0; i < 4; i++)
            {
                var info = FaceInfo.fromFacing(facing).getVertexInfo(i);
                var vertex = new Vector3f(shape[info.xFace], shape[info.yFace], shape[info.zFace]);
                applyElementRotation(vertex, element);
                applyModelRotation(vertex, transform);
                fillVertex(vertices, i, vertex, texture, face);
            }

            return vertices;
        }

        private void applyElementRotation(Vector3f vertex, BBElement element)
        {
            var axis = element.rotationAxis();
            if(axis == null) return;

            Vector3f vecAxis = null;
            Vector3f scale = null;
            float angle = 0F;

            switch(axis) {
                case X -> {
                    vecAxis = new Vector3f(1F, 0F, 0F);
                    scale = new Vector3f(0F, 1F, 1F);
                    angle = element.rotation().x();
                }

                case Y -> {
                    vecAxis = new Vector3f(0F, 1F, 0F);
                    scale = new Vector3f(1F, 0F, 1F);
                    angle = element.rotation().y();
                }

                case Z -> {
                    vecAxis = new Vector3f(0F, 0F, 1F);
                    scale = new Vector3f(1F, 1F, 0F);
                    angle = element.rotation().z();
                }
            }

            if(vecAxis == null) return;

            var transform = new Quaternionf().rotationAxis(angle * ((float) Math.PI / 180F), vecAxis);

            if(element.rescale())
            {
                if(Math.abs(angle) == 22.5F) scale.mul(1F / (float) Math.cos((double) ((float) Math.PI / 8F)) - 1F);
                else scale.mul(1F / (float) Math.cos((double) ((float) Math.PI / 4F)) - 1F);
                scale.add(1F, 1F, 1F);
            }
            else scale.set(1F, 1F, 1F);

            rotateVertexBy(vertex, element.origin().mul(.0625F, new Vector3f()), new Matrix4f().rotation(transform), scale);
        }

        private void applyModelRotation(Vector3f vertex, Transformation transform)
        {
            if(transform == Transformation.identity()) return;
            rotateVertexBy(vertex, new Vector3f(.5F), transform.getMatrix(), new Vector3f(1F));
        }

        private void rotateVertexBy(Vector3f pos, Vector3f origin, Matrix4f transform, Vector3f scale)
        {
            var vec = transform.transform(new Vector4f(pos.x() - origin.x(), pos.y() - origin.y(), pos.z() - origin.z(), 1F));
            vec.mul(new Vector4f(scale, 1F));
            pos.set(vec.x() + origin.x(), vec.y() + origin.y(), vec.z() + origin.z());
        }

        @SuppressWarnings("PointlessArithmeticExpression")
        private void fillVertex(int[] vertices, int vertexIndex, Vector3f vertex, TextureAtlasSprite texture, BBFace face)
        {
            var index = vertexIndex * 8;
            vertices[index + 0] = Float.floatToRawIntBits(vertex.x());
            vertices[index + 1] = Float.floatToRawIntBits(vertex.y());
            vertices[index + 2] = Float.floatToRawIntBits(vertex.z());
            vertices[index + 3] = -1;
            vertices[index + 4] = Float.floatToRawIntBits(getUV(texture, vertexIndex, face, true));
            vertices[index + 5] = Float.floatToRawIntBits(getUV(texture, vertexIndex, face, false));
        }

        private float getUV(TextureAtlasSprite texture, int vertexIndex, BBFace face, boolean uv)
        {
            var raw = getRawUV(vertexIndex, face, uv) * .999D + getRawUV(Mth.positiveModulo(vertexIndex + 2, 4), face, uv) * .001D;
            return uv ? texture.getU(raw) : texture.getV(raw);
        }

        private float getRawUV(int vertexIndex, BBFace face, boolean uv)
        {
            var index = Mth.positiveModulo(vertexIndex + face.rotation() / 90F, 4);
            var uvIndex = uv ? (index != 0 && index != 1 ? 2 : 0) : (index != 0 && index != 3 ? 3 : 1);
            var size = uv ? bbModel.resolution().textureWidth() : bbModel.resolution().textureHeight();
            return face.uv().get(uvIndex) * 16F / size;
        }
    }
}
