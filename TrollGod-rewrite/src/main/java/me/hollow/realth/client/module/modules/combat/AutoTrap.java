package me.hollow.realth.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.*;
import me.hollow.realth.client.events.UpdateEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleManifest(label = "AutoTrap", category = Module.Category.COMBAT)
public class AutoTrap extends Module {

    private final Value<Integer> delay = (new Value<>("Delay/Place", 50, 0, 250));
    private final Value<Integer> blocksPerPlace = (new Value<>("Block/Place", 8, 1, 30));
    private final Value<Double> targetRange = (new Value<>("TargetRange", 10.0, 0.0, 20.0));
    private final Value<Double> range = (new Value<>("PlaceRange", 6.0, 0.0, 10.0));
    private final Value<Boolean> raytrace = (new Value<>("Raytrace", false));
    private final Value<Boolean> antiScaffold = (new Value<>("AntiScaffold", false));
    private final Value<Boolean> antiStep = (new Value<>("AntiStep", false));
    private final Value<Boolean> legs = new Value<>("Legs", false);
    private final Value<Boolean> platform = new Value<>("Platform", false);
    private final Value<Boolean> antiDrop = (new Value<>("AntiDrop", false));
    private final Value<Boolean> entityCheck = (new Value<>("NoBlock", true));
    private final Value<Integer> retryer = (new Value<>("Retries", 4, 1, 15));

    private final Timer timer = new Timer();
    private boolean didPlace = false;
    public EntityPlayer target;
    private int placements = 0;

    private final Map<BlockPos, Integer> retries = new HashMap<>();
    private final Timer retryTimer = new Timer();

    @Override
    public void onEnable() {
        retries.clear();
    }

    @SubscribeEvent
    public void doTrap(UpdateEvent event) {
        if (check()) {
            return;
        }
        doStaticTrap();
        if(didPlace) {
            timer.reset();
        }
    }

    private void doStaticTrap() {
        List<Vec3d> placeTargets = BlockUtil.targets(target.getPositionVector(), antiScaffold.getValue(), antiStep.getValue(), legs.getValue(), platform.getValue(), antiDrop.getValue(), raytrace.getValue());
        placeList(placeTargets);
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));

        int obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        if (obbySlot == -1)
            return;
        int lastSlot = mc.player.inventory.currentItem;
        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
        for(Vec3d vec3d : list) {
            BlockPos position = new BlockPos(vec3d);
            int placeability = BlockUtil.isPositionPlaceable(position, raytrace.getValue());
            if(entityCheck.getValue() && placeability == 1 && (retries.get(position) == null || retries.get(position) < retryer.getValue())) {
                placeBlock(position);
                retries.put(position, (retries.get(position) == null ? 1 : (retries.get(position) + 1)));
                retryTimer.reset();
                continue;
            }

            if(placeability == 3) {
                placeBlock(position);
            }
        }
        mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
    }

    private boolean check() {
        didPlace = false;
        placements = 0;

        if (retryTimer.passed(2000)) {
            retries.clear();
            retryTimer.reset();
        }

        int obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        if (obbySlot == -1) {
            ChatUtil.printMessage(ChatFormatting.RED + "<AutoTrap> No obsidian.");
            setEnabled(false);
            return true;
        }

        target = getTarget(targetRange.getValue());
        return target == null || !timer.passed(delay.getValue());
    }

    private EntityPlayer getTarget(double range) {
        EntityPlayer target = null;
        double distance = Math.pow(range, 2) + 1;
        for(EntityPlayer player : mc.world.playerEntities) {
            if (!EntityUtil.isPlayerValid(player, (float) range)) {
                continue;
            }

            if(target == null) {
                target = player;
                distance = mc.player.getDistanceSq(player);
                continue;
            }

            if(mc.player.getDistanceSq(player) < distance) {
                target = player;
                distance = mc.player.getDistanceSq(player);
            }
        }
        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerPlace.getValue() && mc.player.getDistanceSq(pos) <= range.getValue() * range.getValue()) {
            BlockUtil.placeBlock(pos);
            didPlace = true;
            placements++;
        }
    }

}
