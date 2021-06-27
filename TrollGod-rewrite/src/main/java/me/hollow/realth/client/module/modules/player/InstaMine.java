package me.hollow.realth.client.module.modules.player;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.Timer;
import me.hollow.realth.client.events.ClickBlockEvent;
import me.hollow.realth.client.events.PacketEvent;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "CivBreak", category = Module.Category.PLAYER)
public class InstaMine extends Module {

    private final Value<Double> reach = new Value<>("Reach", 5D, 3D, 15D);
    private final Value<Integer> delay = new Value<>("Delay", 300, 0, 1000);

    private final Timer stopwatch = new Timer();

    private CPacketPlayerDigging dig;
    private EnumFacing side;
    private BlockPos pos;

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if ((event.getPacket() instanceof CPacketPlayerDigging) && ((dig == null) || (event.getPacket() != dig)) && pos != null) {
            CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                dig = packet;
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if ((dig != null) && (mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < reach.getValue()) && stopwatch.passed(delay.getValue()) && pos != null) {
            mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.getConnection().sendPacket(dig);
            stopwatch.reset();
        }
    }

    @SubscribeEvent
    public boolean clickBlock(ClickBlockEvent event) {
        if (!canBreak(event.getPos()))
            return false;
        pos = event.getPos();
        side = event.getSide();
        mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
        mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
        return false;
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        return blockState.getBlock().getBlockHardness(blockState, mc.world, pos) != -1;
    }

}
