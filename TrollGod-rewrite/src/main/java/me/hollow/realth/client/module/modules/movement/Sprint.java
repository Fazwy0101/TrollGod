package me.hollow.realth.client.module.modules.movement;

import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "Sprint", category = Module.Category.MOVEMENT)
public class Sprint extends Module {

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!mc.player.isSprinting() && mc.player.moveForward > 0) {
            mc.player.setSprinting(true);
        }
    }

}
