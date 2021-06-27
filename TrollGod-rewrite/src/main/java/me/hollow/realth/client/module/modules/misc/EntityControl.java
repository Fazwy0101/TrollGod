package me.hollow.realth.client.module.modules.misc;

import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "EntityControl", category = Module.Category.MISC)
public class EntityControl extends Module {

    public static EntityControl INSTANCE;

    {
        INSTANCE = this;
    }

}
