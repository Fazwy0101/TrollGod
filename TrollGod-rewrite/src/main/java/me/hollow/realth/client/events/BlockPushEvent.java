package me.hollow.realth.client.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class BlockPushEvent extends Event {
    private final double motionX, motionY, motionZ;

    public BlockPushEvent(double motionX, double motionY, double motionZ) {

        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public double getMotionX() {
        return this.motionX;
    }

    public double getMotionY() {
        return this.motionY;
    }

    public double getMotionZ() {
        return this.motionZ;
    }

    public double[] getMotion() {
        return new double[]{this.motionX, this.motionY, this.motionZ};
    }

}