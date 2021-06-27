package me.hollow.sputnik.client.modules.movement;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "Speed", listen = false, category = Module.Category.MOVEMENT, color = 0xAE85DE)
public class Speed extends Module {

    @Override
    public void onEnable() {
        setSuffix("Strafe");
    }
// I be off the durgs
}
