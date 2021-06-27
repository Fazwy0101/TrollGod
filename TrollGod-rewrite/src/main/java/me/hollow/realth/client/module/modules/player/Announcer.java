package me.hollow.realth.client.module.modules.player;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.Timer;
import me.hollow.realth.client.events.JumpEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "Announcer", category = Module.Category.MISC)
public class Announcer extends Module {

    private final Timer delayTimer = new Timer();

    private final Value<Integer> delay = new Value<>("Delay",0,0,10000);

    @SubscribeEvent
    public void onJump(JumpEvent event) {
        if (mc.player == null)
            return;
        if (delayTimer.passed(delay.getValue())) {
            mc.player.sendChatMessage("I just jumped thanks to TrollGod.CC!");
            delayTimer.reset();
        }
    }
}
