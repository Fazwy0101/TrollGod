package me.hollow.realth.client.module.modules.misc;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import me.hollow.realth.client.other.ColorHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;


//TODO rewrite this its so fucking bad oooh my god
@ModuleManifest(label = "PearlViewer", category = Module.Category.MISC)
public class PearlViewer extends Module {

    private final Value<Double> timeValue = new Value<>("Time", 7D, 0D, 30D);

    private final HashMap<UUID, List<Vec3d>> poses = new HashMap<>();
    private final HashMap<UUID, Double> time = new HashMap<>();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        for (final Map.Entry<UUID, Double> e : new HashMap<>(this.time).entrySet()) {
            if (e.getValue() <= 0.0) {
                this.poses.remove(e.getKey());
                this.time.remove(e.getKey());
            } else {
                this.time.replace(e.getKey(), e.getValue() - 0.05);
            }
        }
        for (int i = 0, loadedEntityListSize = mc.world.loadedEntityList.size(); i < loadedEntityListSize; i++) {
            final Entity e2 = mc.world.loadedEntityList.get(i);
            if (e2 instanceof EntityEnderPearl) {
                if (!this.poses.containsKey(e2.getUniqueID())) {
                    for (int j = 0, playerEntitiesSize = mc.world.playerEntities.size(); j < playerEntitiesSize; j++) {
                        final Entity e3 = mc.world.playerEntities.get(j);
                        if (e3.getDistanceSq(e2) < 4 && !e3.getName().equals(this.mc.player.getName())) {
                            final String name = e3.getName();
                            ChatUtil.printMessage(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase() + " threw a pearl facing " + e2.getHorizontalFacing());
                            break;
                        }
                    }
                    this.poses.put(e2.getUniqueID(), new ArrayList<>(Collections.singletonList(e2.getPositionVector())));
                    this.time.put(e2.getUniqueID(), timeValue.getValue());
                } else {
                    this.time.replace(e2.getUniqueID(), timeValue.getValue());
                    final List<Vec3d> v = this.poses.get(e2.getUniqueID());
                    v.add(e2.getPositionVector());
                }
            }
        }
    }

    @Override
    public void onRender3D() {
        if (isNull())
            return;
        final double x = RenderUtil.getRenderPosX();
        final double y = RenderUtil.getRenderPosY();
        final double z = RenderUtil.getRenderPosZ();
        glSetup();
        GL11.glLineWidth(1f);
        GL11.glBegin(1);
        for (final Map.Entry<UUID, List<Vec3d>> e : this.poses.entrySet()) {
            final List<Vec3d> vec3ds = e.getValue();
            if (vec3ds.size() <= 2) {
                continue;
            }
            GL11.glColor3d(ColorHandler.getColor().getRed() / 255f, ColorHandler.getColor().getGreen() / 255f, ColorHandler.getColor().getBlue() / 255f);
            final int size = vec3ds.size();
            for (int i = 1; i < size; ++i) {
                GL11.glVertex3d(vec3ds.get(i).x - x, vec3ds.get(i).y - y, vec3ds.get(i).z - z);
                GL11.glVertex3d(vec3ds.get(i - 1).x - x, vec3ds.get(i - 1).y - y, vec3ds.get(i - 1).z - z);
            }
        }
        GL11.glEnd();
        glCleanup();
    }

    public static void glSetup() {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0F);
    }

    public static void glCleanup() {
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

}
