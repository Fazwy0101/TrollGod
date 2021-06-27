package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "GuiMove", category = Module.Category.MISC, color = 0x00FF00)
public class GuiMove extends Module {

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if ( mc.currentScreen instanceof Gui | mc.currentScreen instanceof GuiEditSign || mc.currentScreen == null) {
            return;
        }
        KeyBinding[] moveKeys = new KeyBinding[]{
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump
        };
        for (KeyBinding bind : moveKeys) {
            KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
        }

        if (Mouse.isButtonDown(2)) {
            Mouse.setGrabbed(true);
            mc.inGameHasFocus = true;
        } else {
            Mouse.setGrabbed(false);
            mc.inGameHasFocus = false;
        }
    }
}

