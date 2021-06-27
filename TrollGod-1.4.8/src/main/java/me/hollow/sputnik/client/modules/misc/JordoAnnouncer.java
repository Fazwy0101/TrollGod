package me.hollow.sputnik.client.modules.misc;

import me.hollow.sputnik.api.property.Setting;
import me.hollow.sputnik.api.util.Timer;
import me.hollow.sputnik.client.events.UpdateEvent;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import tcb.bces.listener.Subscribe;

import java.util.concurrent.ThreadLocalRandom;

@ModuleManifest(label = "JordoAnnouncer", category = Module.Category.MISC, color = 0x964B00 ) //the color of Shit
public class JordoAnnouncer extends Module {

    private final Timer delayTimer = new Timer();

    private final Setting<Integer> delay = register(new Setting<>("Delay", 5000, 0, 10000));
    private final Setting<Boolean> sound = register(new Setting<>("Sound", true));

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null)
            return;
        if (delayTimer.hasReached(delay.getValue())) {
            mc.player.sendChatMessage("Jordo just ran " + ThreadLocalRandom.current().nextInt(1, 30 + 1) + " metres like a chimpanzee thanks to Jordohax!");
            if (sound.getValue()) {
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_PIG_DEATH, 1.0F));
            }
            delayTimer.reset();
        }
    }

}
