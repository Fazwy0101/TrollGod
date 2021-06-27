package me.hollow.realth.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.BlockUtil;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label = "SelfFill", category = Module.Category.COMBAT)
public class SelfFill extends Module {

    private final Value<Float> height = new Value<>("Height", 5F, -5F, 5F);
    private final Value<Boolean> preferEChests = new Value<>("Prefer EChests", true);

    public BlockPos startPos;

    private int obbySlot = -1;

    @Override
    public void onEnable() {
        if (isNull()) {
            setEnabled(false);
            return;
        }

        obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        int eChestSlot = ItemUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
        if ((preferEChests.getValue() || obbySlot == -1) && eChestSlot != -1) {
            obbySlot = eChestSlot;
        } else {
            obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            if (obbySlot == -1) {
                ChatUtil.printMessageWithID(ChatFormatting.RED + "<Burrow> Toggling, No obsidian.", -551);
                setEnabled(false);
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }

        int startSlot = mc.player.inventory.currentItem;

        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
        startPos = new BlockPos(mc.player.getPositionVector());

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16, mc.player.posZ, true));

        final boolean onEChest = mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        BlockUtil.placeBlock(onEChest ? startPos.up() : startPos);
        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ, false));

        if (startSlot != -1)
            mc.getConnection().sendPacket(new CPacketHeldItemChange(startSlot));

        setEnabled(false);
    }

}
