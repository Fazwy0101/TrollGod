package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.Realth;
import me.hollow.realth.client.module.modules.visual.Chams;
import me.hollow.realth.client.other.ColorHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.awt.*;

@Mixin(RenderLivingBase.class)
public class MixinRenderLivingBase <T extends EntityLivingBase> extends Render<T> {

    @Shadow
    protected ModelBase mainModel;

    private final Minecraft mc = Minecraft.getMinecraft();

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true)
    public void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        if (Chams.INSTANCE.isEnabled() && !Chams.INSTANCE.wireframe.getValue() && entitylivingbaseIn instanceof EntityPlayer) {
            if (entitylivingbaseIn == mc.player && !Chams.INSTANCE.self.getValue()) {
                return;
            }
            ci.cancel();
            Color visibleColor = new Color(Chams.INSTANCE.visibleRed.getValue(), Chams.INSTANCE.visibleGreen.getValue(), Chams.INSTANCE.visibleBlue.getValue());
            Color invisibleColor = new Color(Chams.INSTANCE.invisibleRed.getValue(), Chams.INSTANCE.invisibleGreen.getValue(), Chams.INSTANCE.invisibleBlue.getValue());
            if (Chams.INSTANCE.global.getValue()) {
                visibleColor = ColorHandler.getColor();
                invisibleColor = ColorHandler.getColor();
            }
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.5f);
            GL11.glEnable(2960);
            GL11.glPolygonMode(1028, 6913);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glEnable(10754);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
            GL11.glColor4d(((float) invisibleColor.getRed() / 255), ((float) invisibleColor.getGreen() / 255), ((float) invisibleColor.getBlue() / 255), ((float) Chams.INSTANCE.alpha.getValue() / 255));
            this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glColor4d(((float) visibleColor.getRed() / 255), ((float) visibleColor.getGreen() / 255), ((float) visibleColor.getBlue() / 255), ((float) Chams.INSTANCE.alpha.getValue() / 255));
            this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
        }
    }

    @Inject(method = "renderLayers", at = @At("RETURN"))
    public void onRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo ci) {
        if (Chams.INSTANCE.isEnabled() && Chams.INSTANCE.wireframe.getValue() && entitylivingbaseIn instanceof EntityPlayer && entitylivingbaseIn != Minecraft.getMinecraft().player) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (Realth.INSTANCE.getFriendManager().isFriend(entitylivingbaseIn.getName())) {
                GL11.glColor3f(0.33333334f, 0.78431374f, 0.78431374f);
            } else {
                Color color = ColorHandler.getColor();
                GL11.glColor3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
            }
            GL11.glLineWidth(0.0001f);
            this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleIn);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }

}
