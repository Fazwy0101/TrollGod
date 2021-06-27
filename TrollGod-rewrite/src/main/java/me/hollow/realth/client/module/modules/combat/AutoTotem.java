package me.hollow.realth.client.module.modules.combat;

import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "AutoTotem", category = Module.Category.COMBAT)
public class AutoTotem extends Module {

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        int totemSlot = ItemUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        if (totemSlot == -1)
            return;

        if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
            clickSlot(totemSlot);
        }
    }

    private void clickSlot(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

}
