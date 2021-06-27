package me.hollow.realth.api.utils;

import java.awt.*;

public class ColorUtil {

    public static int getRainbow(int speed, float s) {
        float hue = (System.currentTimeMillis()) % speed;
        return (Color.getHSBColor(hue / speed, s, 1f).getRGB());
    }

    public static int getRainbow(int speed, int offset, float s) {
        float hue = (System.currentTimeMillis()) % speed + (offset * 15L);
        return (Color.getHSBColor(hue / speed, s, 1f).getRGB());
    }

}
