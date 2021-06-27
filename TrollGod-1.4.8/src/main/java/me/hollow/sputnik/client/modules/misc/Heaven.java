package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "Heaven", category = Module.Category.MISC, color = 0xffff00)
public class Heaven extends Module {

    @Override
    public void onUpdate() {
        if (mc.player.isDead || mc.player.getHealth() <= 0) {
            mc.player.posY += 0.001;
        }
    }

}
