package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import me.hollow.realth.client.other.ColorHandler;
import net.minecraft.util.math.RayTraceResult;

@ModuleManifest(label = "BlockHighlight", category = Module.Category.VISUAL)
public class BlockHighlight extends Module {

    @Override
    public void onRender3D() {
        if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            RenderUtil.drawOutline(mc.objectMouseOver.getBlockPos(), ColorHandler.getColor());
        }
    }

}
