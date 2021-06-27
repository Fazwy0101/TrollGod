package me.hollow.realth.client.module.modules.misc;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "FPSLimit", category = Module.Category.MISC, listenable = false)
public class FPSLimit extends Module {

    public static FPSLimit INSTANCE;

    {
        INSTANCE = this;
    }

    public final Value<Integer> limit = new Value<>("Limit", 60, 1, 360);

}
