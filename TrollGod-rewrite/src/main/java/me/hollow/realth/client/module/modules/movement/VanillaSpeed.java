package me.hollow.realth.client.module.modules.movement;

import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.MovementUtil;
import me.hollow.realth.client.events.MoveEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "VanillaSpeed", category = Module.Category.MOVEMENT)
public class VanillaSpeed extends Module {

    private final Value<Float> speed = new Value<>("Speed", 1.0F, 1.0F, 10.0F);

    /**
     * @author chardnol99
     * got the Utilz from future LOL!
     */

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        double[] calc = MovementUtil.directionSpeed(((double) speed.getValue()) / 10);
        event.setMotionX(calc[0]);
        event.setMotionZ(calc[1]);
    }
}



