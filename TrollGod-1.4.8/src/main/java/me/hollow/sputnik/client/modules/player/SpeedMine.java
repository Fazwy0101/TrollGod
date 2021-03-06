package me.hollow.sputnik.client.modules.player;

import me.hollow.sputnik.Sputnik;
import me.hollow.sputnik.api.mixin.accessors.IPlayerControllerMP;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.api.util.render.RenderUtil;
import me.hollow.sputnik.client.events.ClickBlockEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

import java.awt.*;

@ModuleManifest(label = "SpeedMine", category = Module.Category.PLAYER, color = 0xA5AEAD)
public class SpeedMine extends Module {

    public final Setting<Boolean> reset = register(new Setting("Reset", true));

    private static SpeedMine INSTANCE;

    private final Timer renderTimer = new Timer();
    private BlockPos currentPos = null;

    public SpeedMine() {
        INSTANCE = this;
    }

    public static SpeedMine getInstance() {
        return INSTANCE;
    }

    @Override
    public void onRender3D() {
        if (currentPos != null && mc.world.getBlockState(currentPos).getBlock() == Blocks.AIR) {
            currentPos = null;
        }

        if (this.currentPos != null) {
            Color color = new Color(this.renderTimer.hasReached((int)(2000.0f * Sputnik.INSTANCE.getTpsManager().getTpsFactor())) ? 0 : 255, this.renderTimer.hasReached((int)(2000.0f * Sputnik.INSTANCE.getTpsManager().getTpsFactor())) ? 255 : 0, 0, 255);
            RenderUtil.drawProperBoxESP(currentPos, color, 1, true, true, 40, 1);
        }
    }

    @Subscribe
    public void onClickBlock(ClickBlockEvent event) {
        if (event.getStage() == 0) {
            if (((IPlayerControllerMP) mc.playerController).getCurBlockDamageMP() > 0.1f) {
                ((IPlayerControllerMP) mc.playerController).setIsHittingBlock(true);
            }
        } else if (canBreak(event.getPos())) {
            ((IPlayerControllerMP) mc.playerController).setIsHittingBlock(false);
            if (this.currentPos == null) {
                this.currentPos = event.getPos();
                this.renderTimer.reset();
            }
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            currentPos = event.getPos();
            event.setCancelled();
        }
    }

    public boolean canBreak(BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();
        return block.getBlockHardness(mc.world.getBlockState(pos), mc.world, pos) != -1;
    }

}
