package me.hollow.sputnik.client.modules.player;

import me.hollow.sputnik.api.util.ItemUtil;
import me.hollow.sputnik.client.modules.Module;
import me.hollow.sputnik.client.modules.ModuleManifest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;

@ModuleManifest(label = "AutoTotem", category = Module.Category.PLAYER, color = 0xff40AE70)
public class AutoTotem extends Module {

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) return;
        final int totemSlot = ItemUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        if (mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
            final int slot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
            if (totemSlot != -1) {
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            }
        }
    }

}
