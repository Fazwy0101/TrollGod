package me.hollow.realth.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.*;
import me.hollow.realth.api.utils.Timer;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@ModuleManifest(label = "HoleFiller", category = Module.Category.COMBAT)
public class HoleFiller extends Module {

    private final Value<Integer> delay = new Value<>("Delay", 0, 0, 500);
    private final Value<Integer> bpt = new Value<>("Blocks/Tick", 10, 1, 20);
    private final Value<Float> range = new Value<>("Range", 5F, 1F, 6F);
    private final Value<Boolean> up = new Value<>("Up", true);
    private final Value<Boolean> autoDisable = new Value<>("Auto Disable", true);

    private final Timer placeTimer = new Timer();

    private int placeAmount, blockSlot = -1;

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (check()) {
            final EntityPlayer currentTarget = EntityUtil.getClosestPlayer(10);
            if (currentTarget == null) {
                setEnabled(false);
                return;
            }

            final List<BlockPos> holes = calcHoles();
            if (holes.size() == 0) {
                setEnabled(false);
                return;
            }
            holes.sort(Comparator.comparingDouble(currentTarget::getDistanceSq));

            final int lastSlot = mc.player.inventory.currentItem;
            blockSlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            if (blockSlot == -1) {
                setEnabled(false);
                return;
            }

            mc.getConnection().sendPacket(new CPacketHeldItemChange(blockSlot));
            for (int i = 0, holesSize = holes.size(); i < holesSize; i++) {
                final BlockPos pos = holes.get(i);
                if (up.getValue()) {
                    pos.offset(EnumFacing.UP);
                }
                if (BlockUtil.isPositionPlaceable(pos, true) == 3) {
                    placeBlock(pos);
                } else if (up.getValue()) {
                    if (BlockUtil.isPositionPlaceable(pos.down(), true) == 3) {
                        placeBlock(pos.down());
                    }
                }
            }
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            placeTimer.reset();
            if (autoDisable.getValue()) {
                setEnabled(false);
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (bpt.getValue() > placeAmount && placeTimer.passed(delay.getValue())) {
            BlockUtil.placeBlock(pos);
            placeAmount++;
        }
    }

    private boolean check() {
        if (isNull()) {
            return false;
        }
        placeAmount = 0;
        blockSlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);

        if (blockSlot == -1) {
            ChatUtil.printMessage(ChatFormatting.RED + "<HoleFiller> No obsidian.");
            setEnabled(false);
        }
        return true;
    }

    private final BlockPos[] holeOffsets = {
            new BlockPos(0, 0, 1),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, 0)
    };

    public List<BlockPos> calcHoles() {
        final List<BlockPos> safeSpots = new ArrayList<>();
        final List<BlockPos> sphere = BlockUtil.getSphere(range.getValue());
        final BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        final int size = sphere.size();
        for (int i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            if (pos.equals(playerPos))
                continue;
            if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.offset(EnumFacing.UP)).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.offset(EnumFacing.UP, 2)).getBlock() == Blocks.AIR) {
                boolean safe = true;
                for (final BlockPos offset : holeOffsets) {
                    final Block offsetBlock = mc.world.getBlockState(pos.add(offset)).getBlock();
                    if (offsetBlock != Blocks.BEDROCK && offsetBlock != Blocks.OBSIDIAN) {
                        safe = false;
                        break;
                    }
                }

                if (safe) {
                    safeSpots.add(pos);
                }
            }
        }
        return safeSpots;
    }

}
