package me.hollow.realth.client.module.modules.visual;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.events.Render2DEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "FOVModifier", category = Module.Category.VISUAL)
public class FOVModifier extends Module {

    private final Value<Integer> fov = new Value<>("FOV", 120, 0, 160);

    @SubscribeEvent
    public void onRender(Render2DEvent event) {
        mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fov.getValue());
    }

}
