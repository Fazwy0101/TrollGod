package me.hollow.realth.client.other;

import me.hollow.realth.api.utils.font.CFontRenderer;
import me.hollow.realth.client.module.modules.other.FontMod;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class FontManager {

    private final Minecraft mc = Minecraft.getMinecraft();

    private CFontRenderer fontRenderer = new CFontRenderer(new Font("Verdana", 0, 18), true, true);
    private boolean customFont;

    public boolean isCustomFont() {
        return customFont;
    }

    public void setCustomFont(boolean customFont) {
        this.customFont = customFont;
    }

    public void updateFont() {
        //noinspection MagicConstant
        fontRenderer = new CFontRenderer(new Font("Verdana", FontMod.INSTANCE.STYLE.getValue().getType(), 18), true, true);
    }

    public void drawString(String text, float x, float y, int color) {
        if (customFont) {
            fontRenderer.drawStringWithShadow(text, x, y, color);
            return;
        }
        mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public int getStringWidth(String text) {
        if (customFont) {
            return fontRenderer.getStringWidth(text);
        }
        return mc.fontRenderer.getStringWidth(text);
    }

}
