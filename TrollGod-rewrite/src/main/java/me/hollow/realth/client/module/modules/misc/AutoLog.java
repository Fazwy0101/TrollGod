package me.hollow.realth.client.module.modules.misc;

import me.hollow.realth.Realth;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "AutoLog", category = Module.Category.MISC)
public class AutoLog extends Module {

    private final Value<Boolean> onlyNoTotem = new Value<>("No Totem Only", true);

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (Realth.INSTANCE.getSafetyManager().isSafe()) {
            if (ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) == 0 || !onlyNoTotem.getValue()) {
                mc.getConnection().handleDisconnect(new SPacketDisconnect());
            }
        }
    }

}
