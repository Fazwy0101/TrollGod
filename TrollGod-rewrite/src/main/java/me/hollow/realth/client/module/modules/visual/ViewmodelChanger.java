package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;

@ModuleManifest(label = "ModelChanger", category = Module.Category.VISUAL, listenable = false)
public class ViewmodelChanger extends Module {

    public final Value<Float> offsetX = new Value<>("Offset X", 0f, -1f, 1f);
    public final Value<Float> offsetY = new Value<>("Offset Y", 0f, -1f, 1f);
    public final Value<Float> offsetZ = new Value<>("Offset Z", 0f, -1f, 1f);

    public final Value<Float> offhandY = new Value<>("Offhand Y", 0f, -1f, 1f);
    public final Value<Float> scaleX = new Value<>("ScaleX", 0f, -10f, 10f);
    public final Value<Float> scaleY = new Value<>("ScaleY", 0f, -10f, 10f);
    public final Value<Float> scaleZ = new Value<>("ScaleZ", 0f, -10f, 10f);

    public static ViewmodelChanger INSTANCE;

    {
        INSTANCE = this;
    }

}
