package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "EnchantColor", category = Module.Category.VISUAL, listenable = false)
public class EnchantColor extends Module {

    public static EnchantColor INSTANCE;

    {
        INSTANCE = this;
    }

}
