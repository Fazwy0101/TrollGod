package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "Chams", category = Module.Category.VISUAL, listenable = false)
public class Chams extends Module {

    //c++ header file Type Beats
    public final Value<Boolean> wireframe      = new Value<>("Wireframe", false);
    public final Value<Boolean> self           = new Value<>("Self", false);
    public final Value<Boolean> global         = new Value<>("Global", false);
    public final Value<Integer> visibleRed     = new Value<>("Red", 0, 0, 255);
    public final Value<Integer> visibleGreen   = new Value<>("Green", 255, 0, 255);
    public final Value<Integer> visibleBlue    = new Value<>("Blue", 0, 0, 255);
    public final Value<Integer> invisibleRed   = new Value<>("Invis Red", 255, 0, 255);
    public final Value<Integer> invisibleGreen = new Value<>("Invis Green", 0, 0, 255);
    public final Value<Integer> invisibleBlue  = new Value<>("Invis Blue", 0, 0, 255);
    public final Value<Integer> alpha          = new Value<>("Alpha", 60, 0, 255);


    public static Chams INSTANCE;

    {
        INSTANCE = this;
    }

}
