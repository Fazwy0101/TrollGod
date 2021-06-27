package me.hollow.realth.client.module.modules.combat;

import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "AutoArmor", category = Module.Category.COMBAT)
public class AutoArmor extends Module {

    private final static int[] ARMOR_SLOTS = {
            5, 6, 7, 8
    };

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        for (int j = 0; j < ARMOR_SLOTS.length; j++) {
            int i = ARMOR_SLOTS[j];
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                int slot = ItemUtil.getItemSlot(getArmorFromSlot(i));
                if (slot == -1) {
                    continue;
                }
                mc.playerController.windowClick(0, slot, 0, ClickType.QUICK_MOVE, mc.player);
            }
        }
    }

    private static Item getArmorFromSlot(int i) {
        switch (i) {
            case 5:
                return Items.DIAMOND_HELMET;
            case 6:
                return Items.DIAMOND_CHESTPLATE;
            case 7:
                return Items.DIAMOND_LEGGINGS;
            case 8:
                return Items.DIAMOND_BOOTS;
            default:
                throw new RuntimeException("the fuck");
        }
    }

}
