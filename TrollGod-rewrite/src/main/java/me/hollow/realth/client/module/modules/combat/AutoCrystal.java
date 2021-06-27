package me.hollow.realth.client.module.modules.combat;

import me.hollow.realth.api.mixin.mixins.AccessorCPacketUseEntity;
import me.hollow.realth.api.property.Value;
import me.hollow.realth.api.utils.*;
import me.hollow.realth.client.events.PacketEvent;
import me.hollow.realth.client.module.Module;
import me.hollow.realth.client.module.ModuleManifest;
import me.hollow.realth.client.module.modules.misc.AutoGG;
import me.hollow.realth.client.module.modules.other.Colors;
import me.hollow.realth.client.other.ColorHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleManifest(label = "AutoCrystal", category = Module.Category.COMBAT)
public class AutoCrystal extends Module {

    private final Value<Integer> breakDelay = new Value<>("Break Delay", 25, 0, 200);
    private final Value<Integer> placeDelay = new Value<>("Place Delay", 25, 0, 200);
    private final Value<Float> range = new Value<>("Range", 11F, 0F, 16F);
    private final Value<Float> placeRange = new Value<>("Place Range", 5F, 0F, 6F);
    private final Value<Float> breakRange = new Value<>("Break Range", 5F, 0F, 6F);
    private final Value<Float> breakWallRange = new Value<>("Wall Range", 5F, 0F, 6F);
    private final Value<Float> minDamage = new Value<>("Min Damage", 4.1F, 0F, 30F);
    private final Value<Float> maxSelf = new Value<>("Max Self", 8.0F, 0F, 36F);
    private final Value<Float> lethalMult = new Value<>("Lethal Mult.", 3F, 0F, 6F);
    private final Value<Float> armorScale = new Value<>("Armor Scale", 10F, 0F, 100F);
    private final Value<Boolean> second  = new Value<>("Second", true);
    private final Value<Boolean> autoSwitch = new Value<>("Switch", true);
    private final Value<Boolean> offhandS = new Value<>("Offhand", true);
    private final Value<Boolean> rainbow = new Value<>("Rainbow", true);

    private final List<BlockPos> placedList = new ArrayList<>();
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private final Timer renderTimer = new Timer();

    private EntityPlayer currentTarget;
    private BlockPos renderPos;
    private boolean offhand;

    @Override
    public void onToggle() {
        placedList.clear();
        breakTimer.reset();
        placeTimer.reset();
        renderTimer.reset();
        currentTarget = null;
        renderPos = null;
        offhand = false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (isNull()) {
            return;
        }

        if (renderTimer.passed(500)) {
            placedList.clear();
            renderPos = null;
            renderTimer.reset();
        }

        offhand = mc.player.inventory.offHandInventory.get(0).getItem() == Items.END_CRYSTAL;
        currentTarget = EntityUtil.getClosestPlayer(range.getValue());
        if (currentTarget == null) {
            return;
        }
        doPlace();
        if (event.phase == TickEvent.Phase.START) {
            doBreak();
        }
    }

    private void doBreak() {
        Entity crystal = null;
        double maxDamage = 0.5D;
        final int size = mc.world.loadedEntityList.size();
        for (int i = 0; i < size; ++i) {
            final Entity entity = mc.world.loadedEntityList.get(i);
            if (entity instanceof EntityEnderCrystal) {
                if (mc.player.getDistance(entity) < (mc.player.canEntityBeSeen(entity) ? breakRange.getValue() : breakWallRange.getValue())) {
                    final float targetDamage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, currentTarget);
                    if (targetDamage > minDamage.getValue() || targetDamage * lethalMult.getValue() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || ItemUtil.isArmorUnderPercent(currentTarget, armorScale.getValue())) {
                        final float selfDamage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, mc.player);
                        if (selfDamage > maxSelf.getValue() || selfDamage + 2 > mc.player.getHealth() + mc.player.getAbsorptionAmount() || selfDamage >= targetDamage || maxDamage > targetDamage) {
                            continue;
                        }
                        maxDamage = targetDamage;
                        crystal = entity;
                    }
                }
            }
        }

        if (crystal != null && breakTimer.passed(breakDelay.getValue())) {
            mc.getConnection().sendPacket(new CPacketUseEntity(crystal));
            mc.player.swingArm(offhandS.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            breakTimer.reset();
        }
    }

    private void doPlace() {
        BlockPos placePos = null;
        double maxDamage = 0.5D;
        final List<BlockPos> sphere = BlockUtil.getSphere(placeRange.getValue());
        final int size = sphere.size();
        for (int i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            if (BlockUtil.canPlaceCrystal(pos, second.getValue())) {
                final float targetDamage = EntityUtil.calculate(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, currentTarget);
                if (targetDamage > minDamage.getValue() || targetDamage * lethalMult.getValue() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || ItemUtil.isArmorUnderPercent(currentTarget, armorScale.getValue())) {
                    final float selfDamage = EntityUtil.calculate(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, mc.player);
                    if (selfDamage > maxSelf.getValue() || selfDamage + 2 > mc.player.getHealth() + mc.player.getAbsorptionAmount() || selfDamage >= targetDamage || maxDamage > targetDamage) {
                        continue;
                    }
                    maxDamage = targetDamage;
                    placePos = pos;
                }
            }
        }

        boolean flag = false;
        if (!offhand && mc.player.inventory.getCurrentItem().getItem() != Items.END_CRYSTAL) {
            flag = true;
            if (!autoSwitch.getValue() || mc.player.inventory.getCurrentItem().getItem() == Items.GOLDEN_APPLE && mc.player.isHandActive()) {
                return;
            }
        }

        if (placePos != null) {
            if (placeTimer.passed(placeDelay.getValue())) {
                if (flag) {
                    int slot = ItemUtil.getItemFromHotbar(Items.END_CRYSTAL);
                    if (slot == -1) {
                        return;
                    }
                    mc.player.inventory.currentItem = slot; //how does this work wtf were not sending a confirmation of dis slot And server still lets us place
                }
                placedList.add(placePos);
                mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0f, 0f, 0f));
                placeTimer.reset();
            }
            renderPos = placePos;
            AutoGG.setCurrentTarget(currentTarget);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            final SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51 && placedList.contains(new BlockPos(packet.getX(), packet.getY() - 1, packet.getZ()))) {
                final AccessorCPacketUseEntity use = (AccessorCPacketUseEntity) new CPacketUseEntity();
                use.setEntityId(packet.getEntityID());
                use.setAction(CPacketUseEntity.Action.ATTACK);
                mc.getConnection().sendPacket((Packet<?>) use);
                mc.player.swingArm(offhandS.getValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                breakTimer.reset();
                return;
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                new ArrayList<>(mc.world.loadedEntityList).forEach(e -> {
                    if (e instanceof EntityEnderCrystal && e.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36) {
                        e.setDead();
                    }
                });
            }
        }
    }

    @Override
    public void onRender3D() {
        if (renderPos != null) {
            RenderUtil.drawBoxOutlined(renderPos, rainbow.getValue() ? new Color(ColorUtil.getRainbow(4000, 0,Colors.saturation.getValue() / 255f)) : ColorHandler.getColor(), 50);
        }
    }
}
