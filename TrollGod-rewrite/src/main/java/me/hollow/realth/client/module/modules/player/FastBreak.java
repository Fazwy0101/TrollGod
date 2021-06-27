package me.hollow.realth.client.module.modules.player;

import me.hollow.realth.api.mixin.mixins.AccessorPlayerControllerMP;
import me.hollow.realth.api.utils.RenderUtil;
import me.hollow.realth.api.utils.TickRate;
import me.hollow.realth.api.utils.Timer;
import me.hollow.realth.client.events.ClickBlockEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@ModuleManifest(label = "FastBreak", category = Module.Category.PLAYER)
public class FastBreak extends Module {

    public static FastBreak INSTANCE;

    private final Timer renderTimer = new Timer();
    private BlockPos currentPos = null;

    public FastBreak() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D() {
        if (currentPos != null && mc.world.getBlockState(currentPos).getBlock() == Blocks.AIR) {
            currentPos = null;
        }

        if (this.currentPos != null) {
            RenderUtil.drawBoxOutlined(currentPos, renderTimer.passed((int)(2000.0f * (20 / TickRate.TPS))) ? Color.GREEN : Color.RED, 40);
        }
    }

    @SubscribeEvent
    public void onClickBlock(ClickBlockEvent event) {
        if (event.isDamage()) {
            if (((AccessorPlayerControllerMP) mc.playerController).getCurBlockDamageMP() > 0.1f) {
                ((AccessorPlayerControllerMP) mc.playerController).setIsHittingBlock(true);
            }
        } else if (canBreak(event.getPos())) {
            ((AccessorPlayerControllerMP) mc.playerController).setIsHittingBlock(false);
            if (this.currentPos == null) {
                this.currentPos = event.getPos();
                this.renderTimer.reset();
            }
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getSide()));
            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getSide()));
            currentPos = event.getPos();
            event.setCanceled(true);
        }
    }

    public boolean canBreak(BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();
        return block.getBlockHardness(mc.world.getBlockState(pos), mc.world, pos) != -1;
    }

}
