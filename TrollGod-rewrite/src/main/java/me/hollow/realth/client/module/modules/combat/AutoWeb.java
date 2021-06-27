package me.hollow.realth.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.BlockUtil;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.api.utils.EntityUtil;
import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "AutoWeb", category = Module.Category.COMBAT)
public class AutoWeb extends Module {

    private final Value<Float> webRange = new Value<>("Web Range", 10f, 0f, 13f);
    private final Value<Integer> placePerTick = new Value<>("BPT", 10, 0, 20);

    private int blocksPlaced;

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        blocksPlaced = 0;
        final EntityPlayer player = EntityUtil.getClosestPlayer(9);
        if (player != null) {
            int webSlot = ItemUtil.getBlockFromHotbar(Blocks.WEB);
            if (webSlot == -1) {
                setEnabled(false);
                ChatUtil.printMessage(ChatFormatting.RED + "No webs.");
                return;
            }

            BlockPos pos = new BlockPos(player.getPositionVector());
            if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR || mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR) {
                return;
            }
            int lastSlot = -1;
            if (mc.player.inventory.getCurrentItem().getItem() != Item.getItemFromBlock(Blocks.WEB)) {
                lastSlot = mc.player.inventory.currentItem;
                mc.getConnection().sendPacket(new CPacketHeldItemChange(webSlot));
            }
            placeBlock(pos);
            placeBlock(pos.up());
            if (lastSlot != -1) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            }
        }
    }

    public void placeBlock(BlockPos pos) {
        if (blocksPlaced <= placePerTick.getValue() && mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) < webRange.getValue()) {
            BlockUtil.placeBlock(pos);
            blocksPlaced++;
        }
    }

}
