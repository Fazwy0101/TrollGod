package me.hollow.realth.client.module.modules.player;

import me.hollow.realth.api.mixin.accessors.IMinecraft;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "FastPlace", category = Module.Category.PLAYER)
public class FastPlace extends Module {

    private final Value<Boolean> noBlock = new Value<>("No Block", true);

    private final IMinecraft minecraft = ((IMinecraft) mc); // caceche

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && noBlock.getValue())
            return;
        minecraft.setDelay(0);
    }

}
