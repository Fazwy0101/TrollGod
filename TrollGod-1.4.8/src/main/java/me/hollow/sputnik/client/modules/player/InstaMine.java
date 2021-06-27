package me.hollow.sputnik.client.modules.player;

import me.hollow.sputnik.api.mixin.accessors.IPlayerControllerMP;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.client.events.ClickBlockEvent;
import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "InstaMine", category = Module.Category.PLAYER, color = 0xFAEEAF)
public class InstaMine extends Module {

    private final Setting<Integer> delay = register(new Setting<>("Delay", 20, 0, 500));

    private final Timer breakTimer = new Timer();

    private BlockPos renderBlock;
    private BlockPos lastBlock;
    private boolean packetCancel = false;
    private EnumFacing direction;

    @Override
    public void onUpdate() {
        if (renderBlock != null) {
            if (breakTimer.hasReached(delay.getValue())) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, renderBlock, direction));
                breakTimer.reset();
            }
        }
        ((IPlayerControllerMP) mc.playerController).setBlockHitDelay(0);
    }


    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging digPacket = (CPacketPlayerDigging) event.getPacket();
            if (digPacket.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK && packetCancel)
                event.setCancelled();
        }
    }

    @Subscribe
    public void onDamageBlock(ClickBlockEvent event) {
        if (event.getStage() != 1) {
            return;
        }
        if (canBreak(event.getPos())) {

            if (lastBlock == null || event.getPos() != lastBlock) {
                packetCancel = false;
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                        event.getPos(), event.getFacing()));
                packetCancel = true;
            } else {
                packetCancel = true;
            }
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                    event.getPos(), event.getFacing()));

            renderBlock = event.getPos();
            lastBlock = event.getPos();
            direction = event.getFacing();

            event.setCancelled();
        }
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();

        return block.getBlockHardness(blockState, mc.world, pos) != -1;
    }

    public BlockPos getTarget(){
        return renderBlock;
    }

    public void setTarget(BlockPos pos) {
        renderBlock = pos;
        packetCancel = false;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                pos, EnumFacing.DOWN));
        packetCancel = true;
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                pos, EnumFacing.DOWN));
        direction = EnumFacing.DOWN;
        lastBlock = pos;
    }

}
