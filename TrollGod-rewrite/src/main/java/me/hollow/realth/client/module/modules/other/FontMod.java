package me.hollow.realth.client.module.modules.other;

import me.hollow.realth.Realth;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.events.ClientEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@ModuleManifest(label = "Blocks", category = Module.Category.OTHER)
public class FontMod extends Module {

    public static FontMod INSTANCE;

    {
        INSTANCE = this;
    }

    public final Value<Style> STYLE = new Value<>("Style", Style.PLAIN);

    public static void init() {
        Realth.INSTANCE.getFontManager().updateFont();
    }

    @SubscribeEvent
    public void onSetting(ClientEvent event) {
        if (event.getProperty() == STYLE) {
            System.out.println("haha");
            Realth.INSTANCE.getFontManager().updateFont();
        }
    }

    @Override
    public void onDisable() {
        Realth.INSTANCE.getFontManager().setCustomFont(false);
    }

    @Override
    public void onEnable() {
        Realth.INSTANCE.getFontManager().setCustomFont(true);
    }

    public enum Style {
        PLAIN(Font.PLAIN),
        BOLD(Font.BOLD),
        ITALIC(Font.ITALIC);

        private final int type;

        Style(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

}
