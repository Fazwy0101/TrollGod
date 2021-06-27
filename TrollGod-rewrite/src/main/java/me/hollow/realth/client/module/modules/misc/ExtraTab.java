package me.hollow.realth.client.module.modules.misc;

import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "ExtraTab", category = Module.Category.MISC)
public class ExtraTab extends Module {

    public static ExtraTab INSTANCE;

    {
        INSTANCE = this;
    }

}
