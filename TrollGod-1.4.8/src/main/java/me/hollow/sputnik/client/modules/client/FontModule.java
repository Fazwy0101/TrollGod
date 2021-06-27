package me.hollow.sputnik.client.modules.client;

import me.hollow.sputnik.Sputnik;
import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.client.events.ClientEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label = "Font", category = Module.Category.CLIENT)
public class FontModule extends Module {

    public final Setting<String> font = register(new Setting<>("Font", "Verdana"));
    public final Setting<Integer> size = register(new Setting<>("Size", 18, 12, 24));
    public final Setting<Boolean> antiAlias = register(new Setting<>("Anti Alias", true));

    public static FontModule INSTANCE = new FontModule();

    public FontModule() {
        INSTANCE = this;
    }

    @Override
    public void onLoad() {
        Sputnik.fontManager.updateFont();
    }

    @Subscribe
    public void onSetting(ClientEvent event) {
        if (mc.player == null || mc.world == null)
            return;
        if (event.getSetting() == size || event.getSetting() == font || event.getSetting() == null || event.getSetting() == antiAlias) {
            Sputnik.fontManager.updateFont();
        }
    }

    @Override
    public void onEnable() {
        Sputnik.fontManager.customFont = true;
    }

    @Override
    public void onDisable() {
        Sputnik.fontManager.customFont = false;
    }
}
