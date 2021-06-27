package me.hollow.realth.client.module.modules.misc;

import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "AntiPush", category = Module.Category.MISC)
public class AntiPush extends Module {

    public static AntiPush INSTANCE;

    {
        INSTANCE = this;
    }

}
