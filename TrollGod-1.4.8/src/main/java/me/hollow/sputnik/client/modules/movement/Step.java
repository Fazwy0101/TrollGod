package me.hollow.sputnik.client.modules.movement;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;

@ModuleManifest(label = "Step", listen = false, category = Module.Category.MOVEMENT, color = 0xffffff00)
public class Step extends Module {

    private final Setting<Boolean> placeHolder = register(new Setting("PlaceHolder", true));

    @Override
    public void onEnable() {
        if (!placeHolder.getValue())
        mc.player.stepHeight = 2;
    }

    @Override
    public void onDisable() {
        if (!placeHolder.getValue())
        mc.player.stepHeight = 0.6f;
    }

}
