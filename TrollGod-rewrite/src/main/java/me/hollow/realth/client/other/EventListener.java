package me.hollow.realth.client.other;

import me.hollow.realth.Realth;
import me.hollow.realth.client.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class EventListener {

    public static EventListener INSTANCE = new EventListener();

    private final int size = Realth.INSTANCE.getModuleManager().getModules().size();

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity() == mc.player) {
            Realth.INSTANCE.getSafetyManager().update();
            Realth.INSTANCE.getPopManager().update();
            SpeedManager.update();
        }
    }

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            for (int i = 0; i < size; ++i) {
                final Module mod = Realth.INSTANCE.getModuleManager().getModules().get(i);
                if (mod.getKey() == Keyboard.getEventKey()) {
                    mod.toggle();
                }
            }
        }
    }

}
