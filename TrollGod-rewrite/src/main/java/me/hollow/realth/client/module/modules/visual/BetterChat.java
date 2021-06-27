package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "BetterChat", category = Module.Category.VISUAL)
public class BetterChat extends Module {

    public final Value<Boolean> noRect = new Value<>("No Rect", true);
    public final Value<Boolean> font = new Value<>("C Font", true);

    public static BetterChat INSTANCE;

    {
        INSTANCE = this;
    }

}
