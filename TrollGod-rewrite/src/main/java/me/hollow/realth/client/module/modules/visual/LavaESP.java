package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.BlockUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ModuleManifest(label = "LavaESP", category = Module.Category.VISUAL)
public class LavaESP extends Module {

    private final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    private final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    private final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    private final Value<Integer> alpha = new Value<>("Alpha", 255, 0, 255);
    private final Value<Integer> shrink = new Value<>("Shrink", 10, 5, 10);
    private final Value<Integer> pockets = new Value<>("Pockets", 8, 1, 30);

    private final List<BlockPos> airPockets = new ArrayList<>();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        airPockets.clear();
        final List<BlockPos> sphere = BlockUtil.getSphere(7);
        final int size = sphere.size();
        int lavaCount = 0;
        for (int i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            if (mc.world.getBlockState(pos).getMaterial() == Material.LAVA) {
                lavaCount++;
            }

            if (lavaCount > 12) {
                if (BlockUtil.canPlaceCrystal(pos, true)) {
                    airPockets.add(pos);
                }
            }
        }
        airPockets.sort(Comparator.comparingDouble(pckt -> mc.player.getDistanceSq(pckt)));
    }

    @Override
    public void onRender3D() {
        final int size = airPockets.size();
        for (int i = 0; i < size; ++i) {
            final BlockPos airPocket = airPockets.get(i);
            if (i > pockets.getValue())
                continue;
            final AxisAlignedBB bb = new AxisAlignedBB(airPocket.getX() - mc.getRenderManager().viewerPosX, airPocket.getY() - mc.getRenderManager().viewerPosY, airPocket.getZ() - mc.getRenderManager().viewerPosZ, airPocket.getX() + 1 - mc.getRenderManager().viewerPosX, airPocket.getY() + 1 - mc.getRenderManager().viewerPosY, airPocket.getZ() + 1 - mc.getRenderManager().viewerPosZ);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            RenderGlobal.renderFilledBox(bb.shrink(shrink.getValue() == 0 ? 0 : shrink.getValue() / 10f), red.getValue() / 255.0f, green.getValue() / 255.0f, blue.getValue() / 255.0f, alpha.getValue() / 255.0f);
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
}
