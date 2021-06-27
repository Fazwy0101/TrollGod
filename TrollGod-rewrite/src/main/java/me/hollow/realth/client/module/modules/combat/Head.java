package me.hollow.realth.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.BlockUtil;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.item.ItemSkull;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "Head", category = Module.Category.COMBAT)
public class Head extends Module {

    private final Value<Boolean> message = new Value<>("Message", true);

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        int slot = ItemUtil.getItemFromHotbar(ItemSkull.class);
        if (slot == -1) {
            ChatUtil.printMessage(ChatFormatting.RED + "No skulls.");
            setEnabled(false);
            return;
        }

        int lastSlot = -1;
        if (mc.player.getHeldItemMainhand().getItem().getClass() != ItemSkull.class) {
            lastSlot = mc.player.inventory.currentItem;
            mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
        }
        BlockUtil.placeBlock(new BlockPos(mc.player.getPositionVector()));
        if (message.getValue()) {
            ChatUtil.printMessageWithID("Just gave head to " + mc.player, -622);
        }
        if (lastSlot != -1) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
        }
        setEnabled(false);
    }

}
