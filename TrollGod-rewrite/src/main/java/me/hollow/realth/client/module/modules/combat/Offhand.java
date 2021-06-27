package me.hollow.realth.client.module.modules.combat;

import me.hollow.realth.Realth;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.EntityUtil;
import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.api.utils.Timer;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "Offhand", category = Module.Category.COMBAT)
public class Offhand extends Module {

    private final Value<Float> crystalHealth = new Value<>("C Health", 15F, 1F, 36F);
    private final Value<Float> gappleHealth = new Value<>("G Health", 15F, 1F, 36F);
    private final Value<Boolean> noCrystal = new Value<>("No Crystal", false);
    private final Value<Boolean> sync = new Value<>("Sync", false);
    private final Value<Integer> syncDelay = new Value<>("Sync Delay", 900, 900, 15000);
    private final Timer syncTimer = new Timer();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (sync.getValue() && syncTimer.passed(syncDelay.getValue())) {
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            syncTimer.reset();
        }
        final Item item = getOffhandItem();
        final int slot = ItemUtil.getItemSlot(item);
        if (slot != -1 && mc.player.getHeldItemOffhand().getItem() != item) {
            clickSlot(slot, !mc.player.getHeldItemOffhand().isEmpty());
        }
    }

    private void clickSlot(int slot, boolean back) {
        mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
        if (back) {
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
        }
        mc.playerController.updateController();
    }

    private Item getOffhandItem() {
        if (Realth.INSTANCE.getSafetyManager().isSafe() || ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) == 0) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && mc.gameSettings.keyBindUseItem.isKeyDown() && EntityUtil.getHealth(mc.player) > gappleHealth.getValue()) {
                return Items.GOLDEN_APPLE;
            }
            if (EntityUtil.getHealth(mc.player) > crystalHealth.getValue() && !noCrystal.getValue() && ItemUtil.getItemCount(Items.END_CRYSTAL) > 0) {
                return Items.END_CRYSTAL;
            }
        }
        return Items.TOTEM_OF_UNDYING;
    }

}
