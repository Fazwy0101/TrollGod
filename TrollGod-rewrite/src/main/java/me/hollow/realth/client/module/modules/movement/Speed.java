package me.hollow.realth.client.module.modules.movement;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "Speed", category = Module.Category.MOVEMENT)
public class Speed extends Module {

    private final Value<Boolean> jump = new Value<>("Jump",true);
    private static final AxisAlignedBB WATER_WALK_AA = new AxisAlignedBB(0.D, 0.D, 0.D, 1.D, 0.99D, 1.D);
    int waitCounter;
    int forward = 1;


    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
            boolean boost = Math.abs(Minecraft.getMinecraft().player.rotationYawHead - Minecraft.getMinecraft().player.rotationYaw) < 90;

            if(Minecraft.getMinecraft().player.moveForward != 0) {
                if(!Minecraft.getMinecraft().player.isSprinting()) Minecraft.getMinecraft().player.setSprinting(true);
                float yaw = Minecraft.getMinecraft().player.rotationYaw;
                if(Minecraft.getMinecraft().player.moveForward > 0) {
                    if(Minecraft.getMinecraft().player.movementInput.moveStrafe != 0) {
                        yaw += (Minecraft.getMinecraft().player.movementInput.moveStrafe > 0) ? -45 : 45;
                    }
                    forward = 1;
                    Minecraft.getMinecraft().player.moveForward = 1.0f;
                    Minecraft.getMinecraft().player.moveStrafing = 0;
                }else if(Minecraft.getMinecraft().player.moveForward < 0) {
                    if(Minecraft.getMinecraft().player.movementInput.moveStrafe != 0) {
                        yaw += (Minecraft.getMinecraft().player.movementInput.moveStrafe > 0) ? 45 : -45;
                    }
                    forward = -1;
                    Minecraft.getMinecraft().player.moveForward = -1.0f;
                    Minecraft.getMinecraft().player.moveStrafing = 0;
                }
                if (Minecraft.getMinecraft().player.onGround) {
                    Minecraft.getMinecraft().player.setJumping(false);
                    if (waitCounter < 1) {
                        waitCounter++;
                        return;
                    } else {
                        waitCounter = 0;
                    }
                    float f = (float)Math.toRadians(yaw);
                    if(jump.getValue()) {
                        Minecraft.getMinecraft().player.motionY = 0.405;
                        Minecraft.getMinecraft().player.motionX -= (double) (MathHelper.sin(f) * 0.1f) * forward;
                        Minecraft.getMinecraft().player.motionZ += (double) (MathHelper.cos(f) * 0.1f) * forward;
                    } else {
                        if(Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed()){
                            Minecraft.getMinecraft().player.motionY = 0.405;
                            Minecraft.getMinecraft().player.motionX -= (double) (MathHelper.sin(f) * 0.1f) * forward;
                            Minecraft.getMinecraft().player.motionZ += (double) (MathHelper.cos(f) * 0.1f) * forward;
                        }
                    }
                } else {
                    if (waitCounter < 1) {
                        waitCounter++;
                        return;
                    } else {
                        waitCounter = 0;
                    }
                    double currentSpeed = Math.sqrt(Minecraft.getMinecraft().player.motionX * Minecraft.getMinecraft().player.motionX + Minecraft.getMinecraft().player.motionZ * Minecraft.getMinecraft().player.motionZ);
                    double speed = boost ? 1.0064 : 1.001;
                    if(Minecraft.getMinecraft().player.motionY < 0) speed = 1;

                    double direction = Math.toRadians(yaw);
                    Minecraft.getMinecraft().player.motionX = (-Math.sin(direction) * speed * currentSpeed) * forward;
                    Minecraft.getMinecraft().player.motionZ = (Math.cos(direction) * speed * currentSpeed) * forward;
                }
            }
        }
    }
