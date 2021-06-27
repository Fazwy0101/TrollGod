package me.hollow.realth.client.module.modules.combat;

import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "AutoBed", category = Module.Category.COMBAT)
public class AutoBed extends Module {

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {

    }
    
}
