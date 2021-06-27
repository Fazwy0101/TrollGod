package me.hollow.sputnik.client.modules.visual;

import com.google.common.collect.Maps;
import me.hollow.sputnik.api.mixin.mixins.render.AccessorEntityRenderer;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

@ModuleManifest(label = "OffscreenESP", category = Module.Category.VISUAL)
public class OffscreenESP extends Module {

    private final Setting<Float> size = register(new Setting<>("Size", 10f, 5f, 25f));
    private final Setting<Float> widthDiv = register(new Setting<>("Width Div", 2.2f, 1.8f, 3f));
    private final Setting<Float> heightDiv = register(new Setting<>("Height Div", 1.3f, 1f, 2f));
    private final Setting<Integer> radius = register(new Setting<>("Radius", 45, 10, 200));

    private final EntityListener entityListener = new EntityListener();

    public static OffscreenESP INSTANCE;

    public OffscreenESP() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D() {
        entityListener.render3d();
    }

    public void onRender2D() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int size = mc.world.loadedEntityList.size();
        for (int i = 0; i < size; i++) {
            final Entity o = mc.world.loadedEntityList.get(i);
            if (o instanceof EntityLivingBase && isValid((EntityLivingBase) o)) {
                final EntityLivingBase entity = (EntityLivingBase) o;
                final Vec3d pos = entityListener.getEntityLowerBounds().get(entity);

                if (pos != null) {
                    if (isOnScreen(pos)) {
                        float x = (float) pos.x / scaledResolution.getScaleFactor();
                        float y = (float) pos.y / scaledResolution.getScaleFactor();
                    } else {

                        int x = (Display.getWidth() / 2) / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale);
                        int y = (Display.getHeight() / 2) / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale);
                        float yaw = getRotations(entity) - mc.player.rotationYaw;
                        // Rotate around crosshair
                        GL11.glTranslatef(x, y, 0);
                        GL11.glRotatef(yaw, 0, 0, 1);
                        GL11.glTranslatef(-x, -y, 0);

                        drawTracerPointer(x, y - radius.getValue(), this.size.getValue(), widthDiv.getValue(), heightDiv.getValue(), getColor(entity, 255).getRGB());

                        // Fix rotate around crosshair
                        GL11.glTranslatef(x, y, 0);
                        GL11.glRotatef(-yaw, 0, 0, 1);
                        GL11.glTranslatef(-x, -y, 0);
                    }
                }
            }
        }
    }

    public static void drawTracerPointer(float x, float y, float size, float widthDiv, float heightDiv, int color) {
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        ((AccessorEntityRenderer) Minecraft.getMinecraft().entityRenderer).invokeOrientCamera(Minecraft.getMinecraft().getRenderPartialTicks());
        GL11.glPushMatrix();
        RenderUtil.glColor(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x - size / widthDiv, y + size);
        GL11.glVertex2d(x, y + size / heightDiv);
        GL11.glVertex2d(x + size / widthDiv, y + size);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glColor4f(0, 0, 0, 0.8f);
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if(!blend)
            GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }

    private boolean isOnScreen(Vec3d pos) {
        if (pos.x > -1 && pos.z < 1)
            return pos.x / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) >= 0 && pos.x / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) <= Display.getWidth() && pos.y / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) >= 0 && pos.y / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) <= Display.getHeight();

        return false;
    }

    private boolean isValid(EntityLivingBase entity) {
        return entity != mc.player && isValidType(entity) && entity.getEntityId() != -1488 && entity.isEntityAlive();
    }

    private boolean isValidType(EntityLivingBase entity) {
        return entity instanceof EntityPlayer;
    }

    private float getRotations(EntityLivingBase ent) {
        final double x = ent.posX - mc.player.posX;
        final double z = ent.posZ - mc.player.posZ;
        final float yaw = (float) (-(Math.atan2(x, z) * 57.29577951308232));
        return yaw;
    }

    private Color getColor(EntityLivingBase player, int alpha) {
        final float distance = mc.player.getDistance(player);
        if (distance <= 32.0f) {
            return new Color(1.0f - ((distance / 32.0f) / 2), distance / 32.0f, 0.0f);
        } else {
            return new Color(0.0f, 0.9f, 0.0f);
        }
    }

    public static Vec3d to2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);

        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new Vec3d(screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2));
        }
        return null;
    }

    public class EntityListener {
        private Map<Entity, Vec3d> entityUpperBounds = Maps.newHashMap();
        private Map<Entity, Vec3d> entityLowerBounds = Maps.newHashMap();

        private void render3d() {
            if (!entityUpperBounds.isEmpty()) {
                entityUpperBounds.clear();
            }
            if (!entityLowerBounds.isEmpty()) {
                entityLowerBounds.clear();
            }
            for (Entity e : mc.world.loadedEntityList) {
                Vec3d bound = getEntityRenderPosition(e);
                bound.add(new Vec3d(0, e.height + 0.2, 0));
                Vec3d upperBounds = to2D(bound.x, bound.y, bound.z), lowerBounds = to2D(bound.x, bound.y - 2, bound.z);
                if (upperBounds != null && lowerBounds != null) {
                    entityUpperBounds.put(e, upperBounds);
                    entityLowerBounds.put(e, lowerBounds);
                }
            }
        }

        private Vec3d getEntityRenderPosition(Entity entity) {
            double partial = mc.getRenderPartialTicks();

            double x = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partial) - mc.getRenderManager().viewerPosX;
            double y = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partial) - mc.getRenderManager().viewerPosY;
            double z = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partial) - mc.getRenderManager().viewerPosZ;

            return new Vec3d(x, y, z);
        }

        public Map<Entity, Vec3d> getEntityUpperBounds() {
            return entityUpperBounds;
        }

        public Map<Entity, Vec3d> getEntityLowerBounds() {
            return entityLowerBounds;
        }
    }

}
