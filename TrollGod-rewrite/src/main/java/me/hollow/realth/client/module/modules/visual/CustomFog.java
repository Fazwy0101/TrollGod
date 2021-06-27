package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "CustomFog", category = Module.Category.VISUAL)
public class CustomFog extends Module {

    private final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    private final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    private final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        event.setRed(red.getValue() / 255f);
        event.setGreen(green.getValue() / 255f);
        event.setBlue(blue.getValue() / 255f);
    }

}
