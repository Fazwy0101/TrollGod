package me.hollow.realth.client.module.modules.misc;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "Swing", category = Module.Category.MISC, listenable = false)
public class Swing extends Module {

    public final Value<Mode> mode = new Value<>("Mode", Mode.OFFHAND);

    public static Swing INSTANCE;

    {
        INSTANCE = this;
    }

    public enum Mode {
        OFFHAND,
        SHUFFLE,
        PACKET
    }

}
