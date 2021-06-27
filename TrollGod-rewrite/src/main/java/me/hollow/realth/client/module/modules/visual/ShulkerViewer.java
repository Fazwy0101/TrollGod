package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "ShulkerViewer", category = Module.Category.VISUAL)
public class ShulkerViewer extends Module {

    public static ShulkerViewer INSTANCE;

    {
        INSTANCE = this;
    }

}
