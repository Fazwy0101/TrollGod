package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.client.events.PacketEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.network.play.client.CPacketEntityAction;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "AntiHunger", category = Module.Category.MISC, color = 0xFFA500)
public class AntiHunger extends Module {

    @Subscribe
    public void onPacket(PacketEvent event) {
        final CPacketEntityAction packet = (CPacketEntityAction) event.getPacket();
        if (packet.getAction() == CPacketEntityAction.Action.START_SPRINTING || packet.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
            event.setCancelled(true);
        }
    }

}