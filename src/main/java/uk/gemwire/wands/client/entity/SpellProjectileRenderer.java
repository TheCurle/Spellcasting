package uk.gemwire.wands.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import uk.gemwire.wands.Wands;
import uk.gemwire.wands.entity.SpellProjectileEntity;
import uk.gemwire.wands.types.Spell;

public class SpellProjectileRenderer extends EntityRenderer<SpellProjectileEntity> {

    public SpellProjectileRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public ResourceLocation getTextureLocation(SpellProjectileEntity p_114482_) {
        return p_114482_.getEntityData().get(SpellProjectileEntity.spellAccessor).toEntityTextureLocation();
    }

    @Override
    public void render(SpellProjectileEntity p_114485_, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_) {
        p_114488_.pushPose();
        p_114488_.scale(2.0F, 2.0F, 2.0F);
        p_114488_.mulPose(this.entityRenderDispatcher.cameraOrientation());
        p_114488_.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose posestack$pose = p_114488_.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = p_114489_.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(p_114485_)));
        vertex(vertexconsumer, matrix4f, matrix3f, p_114490_, 0.0F, 0, 0, 1);
        vertex(vertexconsumer, matrix4f, matrix3f, p_114490_, 1.0F, 0, 1, 1);
        vertex(vertexconsumer, matrix4f, matrix3f, p_114490_, 1.0F, 1, 1, 0);
        vertex(vertexconsumer, matrix4f, matrix3f, p_114490_, 0.0F, 1, 0, 0);
        p_114488_.popPose();
        super.render(p_114485_, p_114486_, p_114487_, p_114488_, p_114489_, p_114490_);
    }
    private static void vertex(VertexConsumer p_254095_, Matrix4f p_254477_, Matrix3f p_253948_, int p_253829_, float p_253995_, int p_254031_, int p_253641_, int p_254243_) {
        p_254095_.vertex(p_254477_, p_253995_ - 0.5F, (float)p_254031_ - 0.25F, 0.0F)
                .color(255, 255, 255, 255)
                .uv((float)p_253641_, (float)p_254243_)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(p_253829_)
                .normal(p_253948_, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}
