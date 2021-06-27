package me.hollow.realth.api.mixin.mixins;

import me.hollow.realth.Realth;
import me.hollow.realth.api.utils.ColorUtil;
import me.hollow.realth.client.events.Render2DEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.modules.visual.CustomWeather;
import me.hollow.realth.client.module.modules.visual.Nametags;
import me.hollow.realth.client.module.modules.visual.RenderTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Random;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Final
    @Shadow
    Minecraft mc;

    //Throwback TO Miku Clients

    @Shadow
    private Random random;

    @Shadow
    public abstract void disableLightmap();

    @Shadow
    public abstract void enableLightmap();

    @Shadow
    private int rendererUpdateCount;

    @Shadow
    protected float[] rainXCoords;

    @Shadow
    protected float[] rainYCoords;

    @Shadow
    protected static ResourceLocation SNOW_TEXTURES;

    @Inject(method = "renderHand", at = @At("HEAD"))
    public void renderHand(float partialTicks, int pass, CallbackInfo ci) {
        if (mc.player == null || mc.world == null)
            return;
        final int size = Realth.INSTANCE.getModuleManager().getSize();
        for (int i = 0; i < size; ++i) {
            final Module mod = Realth.INSTANCE.getModuleManager().getModules().get(i);
            if (mod.isEnabled()) {
                mod.onRender3D();
            }
        }
    }

    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float y, float ratio, float zNear, float zFar) {
        Project.gluPerspective(y, RenderTweaks.INSTANCE.isEnabled() ? RenderTweaks.INSTANCE.ratio.getValue() : ratio, zNear, zFar);
    }


    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"))
    public void updateCameraAndRender$renderGameOverlay(float partialTicks, long nanoTime, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new Render2DEvent());
    }

    @Inject(method = "drawNameplate", at = @At("HEAD"), cancellable = true)
    private static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, CallbackInfo ci) {
        if (Nametags.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }

    /**
     * @author Miku Clietnts
     */
    @Overwrite
    public void renderRainSnow(float partialTicks) {
        //if (Miku.INSTANCE.getModuleManager().findByClass(Snow.class).isEnabled()) {
        if (CustomWeather.INSTANCE.isEnabled() && CustomWeather.INSTANCE.snow.getValue()) {
            net.minecraftforge.client.IRenderHandler renderer = this.mc.world.provider.getWeatherRenderer();
            if (renderer != null) {
                renderer.render(partialTicks, this.mc.world, mc);
                return;
            }

            this.enableLightmap();
            Entity entity = this.mc.getRenderViewEntity();
            World world = this.mc.world;
            int i = MathHelper.floor(entity.posX);
            int j = MathHelper.floor(entity.posY);
            int k = MathHelper.floor(entity.posZ);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.disableCull();
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.alphaFunc(516, 0.1F);
            double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
            double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
            double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
            int l = MathHelper.floor(d1);
            int i1 = 5;

            if (this.mc.gameSettings.fancyGraphics) {
                i1 = 10;
            }

            int j1 = -1;
            float f1 = (float) this.rendererUpdateCount + partialTicks;
            bufferbuilder.setTranslation(-d0, -d1, -d2);
            GlStateManager.color(1, 0, 1, 1);
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            Color color = CustomWeather.INSTANCE.rainbow.getValue() ? new Color(ColorUtil.getRainbow(5000, 0.7F)) : new Color(CustomWeather.INSTANCE.red.getValue(), CustomWeather.INSTANCE.green.getValue(), CustomWeather.INSTANCE.blue.getValue());
            float red = color.getRed() / 255f;
            float green = color.getGreen() / 255f;
            float blue = color.getBlue() / 255f;
            for (int k1 = k - i1; k1 <= k + i1; ++k1) {
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    int i2 = ((k1 - k + 16) << 5) + l1 - i + 16;
                    double d3 = (double) this.rainXCoords[i2] * 0.5;
                    double d4 = (double) this.rainYCoords[i2] * 0.5;
                    blockpos$mutableblockpos.setPos(l1, 0, k1);
                    int j2 = world.getPrecipitationHeight(blockpos$mutableblockpos).getY();
                    int k2 = j - i1;
                    int l2 = j + i1;
                    if (k2 < j2) {
                        k2 = j2;
                    }
                    if (l2 < j2) {
                        l2 = j2;
                    }
                    int i3 = j2;
                    if (j2 < l) {
                        i3 = l;
                    }
                    if (k2 == l2) continue;
                    this.random.setSeed(l1 * l1 * 3121L + l1 * 45238971L ^ k1 * k1 * 418711L + k1 * 13761L);
                    blockpos$mutableblockpos.setPos(l1, k2, k1);
                    if (j1 != 1) {
                        j1 = 1;
                        mc.getTextureManager().bindTexture(SNOW_TEXTURES);
                        bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                    }
                    double d8 = ((float) (this.rendererUpdateCount & 0x1FF) + partialTicks) / 512.0f;
                    double d9 = this.random.nextDouble() + (double) f1 * 0.01 * (double) ((float) this.random.nextGaussian());
                    double d10 = this.random.nextDouble() + (double) (f1 * (float) this.random.nextGaussian()) * 0.001;
                    double d11 = (double) ((float) l1 + 0.5f) - entity.posX;
                    double d12 = (double) ((float) k1 + 0.5f) - entity.posZ;
                    float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / (float) i1;
                    float f5 = ((1.0f - f6 * f6) * 0.3f + 0.5f);
                    blockpos$mutableblockpos.setPos(l1, i3, k1);
                    int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 0xF000F0) / 4;
                    int j4 = i4 >> 16 & 0xFFFF;
                    int k4 = i4 & 0xFFFF;
                    bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) l2, (double) k1 - d4 + 0.5D).tex(0.0D, (double) k2 * 0.25D + d8 + d10).color(red, green, blue, f5).lightmap(j4, k4).endVertex();
                    bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) l2, (double) k1 + d4 + 0.5D).tex(1.0D, (double) k2 * 0.25D + d8 + d10).color(red, green, blue, f5).lightmap(j4, k4).endVertex();
                    bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) k2, (double) k1 + d4 + 0.5D).tex(1.0D, (double) l2 * 0.25D + d8 + d10).color(red, green, blue, f5).lightmap(j4, k4).endVertex();
                    bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) k2, (double) k1 - d4 + 0.5D).tex(0.0D, (double) l2 * 0.25D + d8 + d10).color(red, green, blue, f5).lightmap(j4, k4).endVertex();
                }
            }
            if (j1 >= 0) {
                tessellator.draw();
            }
            GlStateManager.shadeModel(GL11.GL_FLAT);
            bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            this.disableLightmap();
        }
    }

}
