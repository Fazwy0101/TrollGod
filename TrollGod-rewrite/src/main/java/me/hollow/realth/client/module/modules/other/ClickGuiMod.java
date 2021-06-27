package me.hollow.realth.client.module.modules.other;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.clickgui.ClickGui;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@ModuleManifest(label = "ClickGUI", category = Module.Category.OTHER, key = Keyboard.KEY_INSERT)
public class ClickGuiMod extends Module {

    public final Value<Integer> alpha = new Value<>("Alpha", 100, 0, 255);

    private ClickGui clickGui;

    public static ClickGuiMod INSTANCE;

    {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (clickGui == null) {
            clickGui = new ClickGui();
        }
        mc.displayGuiScreen(clickGui);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen != clickGui) {
            setEnabled(false);
        }
    }

}
