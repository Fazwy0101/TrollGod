package me.hollow.realth.client.module.modules.player;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.events.PacketEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "XCarry", category = Module.Category.PLAYER)
public class XCarry extends Module {
//Borby
    private final Value<Boolean> forceCancel = new Value<>("ForceCancel",true);

    @SubscribeEvent
    public void onUpdate(PacketEvent event) {
        if (event.getPacket() instanceof CPacketCloseWindow){
            if (forceCancel.getValue() ) {
                event.setCanceled(true);
            }
            else {
                for (int i = 1; i <= 4; i++) {
                    if (mc.player.inventory.getStackInSlot(i).getItem() != Items.AIR)
                        event.setCanceled(true);
                }
            }
        }
    }
}

