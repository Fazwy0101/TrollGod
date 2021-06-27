package me.hollow.realth.client.other;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class SpeedManager {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static double speedometerCurrentSpeed;

    public static void update() {
        final double distTraveledLastTickX = mc.player.posX - mc.player.prevPosX;
        final double distTraveledLastTickZ = mc.player.posZ - mc.player.prevPosZ;
        speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
    }

    public static double getSpeedKMH() {
        return (10.0 * MathHelper.sqrt(speedometerCurrentSpeed) * 71.2729367892) / 10.0;
    }

}
