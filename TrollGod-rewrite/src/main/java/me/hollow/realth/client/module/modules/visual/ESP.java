package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "ESP", category = Module.Category.VISUAL, listenable = false)
public class ESP extends Module {

    public final Value<Boolean> players = new Value<>("Players", true);

    public static ESP INSTANCE;

    {
        INSTANCE = this;
    }

}
