package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

import java.util.ArrayList;
import java.util.List;

@ModuleManifest(label = "NoFall", category = Module.Category.MISC, color = 0x99e6ff)
public class NoFall extends Module {

    private List<CPacketPlayer> packets = new ArrayList<>();

    @Override
    public void onEnable() {
        setSuffix("NCP");
    }

    private int teleportId;

    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.fallDistance > 2 && isBlockUnder()) {
            if ((mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 0.3, mc.player.posZ)).getBlock() instanceof BlockAir)) {
                if (this.teleportId <= 0) {
                    final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX, mc.player.posY <= 10 ? 255 : 1, mc.player.posZ, mc.player.onGround);
                    this.packets.add(bounds);
                    mc.player.connection.sendPacket(bounds);
                    return;
                }
                double posY = -0.00000001;
                if (mc.player.fallDistance > 1.5) {
                    mc.player.setVelocity(0, 0, 0);
                    for (int i = 0; i <= 3; i++) {
                        mc.player.setVelocity(0, posY - 0.0625 * i, 0);
                        move(0, posY - 0.0625 * i, 0);
                    }
                }
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent.Send event) {
        if (mc.player.fallDistance > 2 && isBlockUnder()) {
            if ((mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 0.3, mc.player.posZ)).getBlock() instanceof BlockAir)) {
                if (event.getPacket() instanceof SPacketPlayerPosLook) {
                    final SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
                    if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
                        if (this.teleportId <= 0) {
                            this.teleportId = packet.getTeleportId();
                        } else {
                            event.setCancelled();
                        }
                    }
                }
                if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
                    event.setCancelled(true);
                }
                if (event.getPacket() instanceof CPacketPlayer) {
                    final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                    if (packets.contains(packet)) {
                        packets.remove(packet);
                        return;
                    }
                    event.setCancelled();
                }
            }
        }
    }

    private boolean isBlockUnder() {
        for (int i = (int) (mc.player.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(mc.player.posX, i, mc.player.posZ);
            if (mc.world.getBlockState(pos).getBlock() instanceof BlockAir)
                continue;
            return true;
        }
        return false;
    }

    private void move(double x, double y, double z) {
        final CPacketPlayer pos = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(pos);
        mc.player.connection.sendPacket(pos);

        final CPacketPlayer bounds = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY <= 10 ? 255 : 1, mc.player.posZ + z, mc.player.onGround);
        this.packets.add(bounds);
        mc.player.connection.sendPacket(bounds);

        this.teleportId++;
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId - 1));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId));
        mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportId + 1));
    }

}