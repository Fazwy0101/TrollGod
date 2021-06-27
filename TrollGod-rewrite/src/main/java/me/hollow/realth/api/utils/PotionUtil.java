package me.hollow.realth.api.utils;

import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PotionUtil {

    //We store a potion a
    private static final Map<Potion, Integer> potionColorMap = new HashMap<>();

    public PotionUtil() {
        potionColorMap.put(MobEffects.SPEED, new Color(0xAAF0F5).getRGB());
        potionColorMap.put(MobEffects.STRENGTH, new Color(0xFF6868).getRGB());
    }

}
