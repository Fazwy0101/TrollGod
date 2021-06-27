package me.hollow.realth.client.module.modules.misc;

import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.client.events.PacketEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "AutoFish", category = Module.Category.MISC)
public class AutoFish extends Module {

    private int rodSlot = -1;

    @Override
    public void onEnable() {
        if (isNull()) {
            setEnabled(false);
            return;
        }
        rodSlot = ItemUtil.getItemFromHotbar(Items.FISHING_ROD);
    }

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.NEUTRAL && packet.getSound() == SoundEvents.ENTITY_BOBBER_SPLASH) {
               int startSlot = mc.player.inventory.currentItem;
                mc.getConnection().sendPacket(new CPacketHeldItemChange(rodSlot));
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                if (startSlot != -1)
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(startSlot));
                    }
                }
            }
        }
/**
 * @author chard
 * we FISHIN'
 */