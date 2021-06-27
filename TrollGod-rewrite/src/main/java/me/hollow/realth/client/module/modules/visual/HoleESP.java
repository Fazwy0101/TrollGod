package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.BlockUtil;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleManifest(label = "HoleESP", category = Module.Category.VISUAL)
public class HoleESP extends Module {

    private final Value<Float> range = new Value<>("Range", 6F, 1F, 10F);
    private final Value<Boolean> down = new Value<>("Down", true);

    private final Map<BlockPos, Boolean> holes = new HashMap<>();
    private static final BlockPos[] OFFSETS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, 0)
    };

    private int delayTicks;

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (delayTicks++ < 3) {
            return;
        }
        delayTicks = 0;
        holes.clear();
        final List<BlockPos> sphere = BlockUtil.getSphere(range.getValue());
        final int size = sphere.size();
        loop1:
        for (int i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            final Chunk chunk = mc.world.getChunk(pos);
            if (chunk.getBlockState(pos).getBlock() == Blocks.AIR && chunk.getBlockState(pos.offset(EnumFacing.UP, 1)).getBlock() == Blocks.AIR && chunk.getBlockState(pos.offset(EnumFacing.UP, 2)).getBlock() == Blocks.AIR) {
                boolean bedrock = true;
                for (int j = 0; j < 5; ++j) {
                    final Block offsetBlock = mc.world.getBlockState(pos.add(OFFSETS[j])).getBlock();
                    if (offsetBlock != Blocks.BEDROCK) {
                        bedrock = false;
                        if (offsetBlock != Blocks.OBSIDIAN) {
                            continue loop1;
                        }
                    }
                }
                holes.put(down.getValue() ? pos.offset(EnumFacing.DOWN, 1) : pos, bedrock);
            }
        }
    }

    @Override
    public void onRender3D() {
        for (Map.Entry<BlockPos, Boolean> hole : holes.entrySet()) {
            RenderUtil.drawBox(hole.getKey(), hole.getValue() ? Color.GREEN : Color.RED, 40);
        }
    }

}
