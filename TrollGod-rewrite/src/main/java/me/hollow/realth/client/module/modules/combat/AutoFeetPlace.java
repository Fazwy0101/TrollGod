package me.hollow.realth.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.BlockUtil;
import me.hollow.realth.api.utils.ChatUtil;
import me.hollow.realth.api.utils.ItemUtil;
import me.hollow.realth.api.utils.Timer;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.Map;

@ModuleManifest(label = "AutoFeetPlace", category = Module.Category.COMBAT)
public class AutoFeetPlace extends Module {

    private final Value<Integer> delay = new Value<>("Delay", 50, 0, 250);
    private final Value<Integer> blocksPerTick = new Value<>("BPT", 8, 1, 20);
    private final Value<Boolean> helpingBlocks = new Value<>("HelpingBlocks", true);
    private final Value<Boolean> intelligent = new Value<>("Intelligent", false);
    private final Value<Boolean> antiPedo = new Value<>("Always Help", false);
    private final Value<Boolean> floor = new Value<>("Floor", false);
    private final Value<Integer> retryer = new Value<>("Retries", 4, 1, 15);
    private final Value<Integer> retryDelay = new Value<>("Retry Delay", 200, 1, 2500);
    private final Value<Boolean> existCheck = new Value<>("Exist", false);
    private final Value<Integer> existed = new Value<>("Existed", 4, 1, 15);


    private final Map<BlockPos, Integer> retries = new HashMap<>();
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();

    private boolean didPlace = false;
    private int placements = 0;
    private int obbySlot = -1;


    double posY;

    @Override
    public void onEnable() {
        if (isNull()) {
            setEnabled(false);
            return;
        }
        retries.clear();
        retryTimer.reset();
        posY = mc.player.posY;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (check()) {
            return;
        }

        if (posY < mc.player.posY) {
            setEnabled(false);
            return;
        }

        boolean onEChest = mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (mc.player.posY - (int)mc.player.posY < 0.7) {
            onEChest = false;
        }
        if (!BlockUtil.isSafe(mc.player, onEChest ? 1:0, floor.getValue())) {
            placeBlocks(mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray(mc.player, onEChest ? 1 : 0, floor.getValue()), helpingBlocks.getValue(), false);
        } else if (!BlockUtil.isSafe(mc.player, onEChest ? 0 : -1, false)) {
            if (antiPedo.getValue()) {
                placeBlocks(mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray(mc.player, onEChest ? 0 : -1, false), false, false);
            }
        }

        if (didPlace) {
            timer.reset();
        }
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping) {
        int helpings = 0;
        boolean gotHelp;
        if (obbySlot == -1)
            return false;

        if (mc.player == null)
            return false;
        int lastSlot = mc.player.inventory.currentItem;
        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
        for (final Vec3d vec3d : vec3ds) {
            gotHelp = true;
            helpings++;
            if (isHelping && !intelligent.getValue() && helpings > 1) {
                return false;
            }
            final BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, true)) {
                case -1:
                    continue;
                case 2:
                    if (hasHelpingBlocks) {
                        gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true);
                    } else {
                        continue;
                    }
                case 3:
                    if (gotHelp) {
                        placeBlock(position);
                    }
                    if (isHelping) {
                        return true;
                    }
            }
        }
        mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
        return false;
    }

    private boolean check() {
        if (isNull()) {
            return true;
        }

        didPlace = false;
        placements = 0;
        obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);

        if (retryTimer.passed(retryDelay.getValue())) {
            retries.clear();
            retryTimer.reset();
        }

        if (obbySlot == -1) {
            obbySlot = ItemUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
            if (obbySlot == -1) {
                ChatUtil.printMessage(ChatFormatting.RED + "<AutoFeetPlace> No obsidian.");
                this.setEnabled(false);
                return true;
            }
        }

        return !timer.passed(delay.getValue());
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerTick.getValue()) {
            BlockUtil.placeBlock(pos);
            didPlace = true;
            placements++;
        }
    }

}